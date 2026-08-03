# mall-pay 重构代码结构设计报告

> 设计约束（已确认）：
> 1. **不引入任何缓存**——支付是严谨且实时性要求极高的业务，杜绝缓存一致性问题
> 2. **不强求分布式事务**——支付与 mall-order 的跨服务一致性，用**本地事务 + 补偿机制 + 对账兜底**解决，不引入 Seata 等分布式事务框架
> 3. 表结构遵循 `docs/37` 定稿：`cloud_mall_pay` 8 张表，金额 bigint 分，无外键无联表

## 一、设计思路总览

### 1.1 三条主线

```
┌────────────────────────────────────────────────────────────────────┐
│                       mall-pay（统一支付服务）                        │
│                                                                    │
│  ┌─────────────── 主线一：支付核心  ───────────────┐                │
│  │ 创建支付单 → 渠道下单 → 回调验签 → 状态机流转 → 通知业务方 │        │
│  └────────────────────────────────────────────────┘                │
│                                                                    │
│  ┌─────────────── 主线二：退款  ────────────────────┐               │
│  │ 退款申请 → 审核判定 → 渠道退款 → 回调 → 冲正累计    │             │
│  └────────────────────────────────────────────────┘                │
│                                                                    │
│  ┌─────────────── 主线三：对账兜底  ────────────────┐               │
│  │ 定时下载账单 → 解析入临时表 → 逐笔对比 → 差异处置   │              │
│  └────────────────────────────────────────────────┘                │
└────────────────────────────────────────────────────────────────────┘
```

### 1.2 为什么不用缓存

支付是**状态机 + 账目**系统，每一个状态流转都是**持久化事实**：

- 支付状态（待支付→已支付）**不能有缓存层**——缓存里是"已支付"但 DB 还是"待支付"，对账立刻发现短款，资金风险不可接受
- 渠道配置（`pay_channel_config`）**不缓存**——虽然读多，但渠道密钥变更要立即生效（商户换了证书还在用旧的要出错），且支付 QPS 远不到打爆 DB 的量级
- 幂等（回调防重放）用 Redis 存 nonce 是**唯一的例外**——它不是业务状态缓存，是短期去重标记（TTL 5 分钟），不影响账目一致性

### 1.3 为什么不用分布式事务

跨服务的一致点只有一个：**支付成功 → mall-order 更新业务订单为已支付**。这个点用分布式事务（Seata TCC/AT）成本高（引入框架、全局锁、性能损耗），而支付场景完全可以用更轻的方案：

```
支付成功落库（本地事务，pay_order 已支付）
    │
    ▼
发 MQ 通知 mall-order（先落 biz_notify_status，再发消息）
    │
    ├── mall-order 消费成功 → 更新业务订单已支付 ✅
    └── mall-order 消费失败 / 消息丢失
          → 支付服务定时扫描 biz_notify_status=2（发送失败）
          → 退避重推（1min/5min/15min/1h，最多 5 次）
          → 仍失败 → 人工介入（对账/告警兜底）
```

**最终一致性由三层保证：**
1. **本地事务**：单服务内的状态流转（创建支付单、回调改状态）用 MySQL 事务保证原子
2. **MQ 异步通知 + 重推补偿**：跨服务的业务通知靠消息 + `biz_notify_status` 字段重推
3. **对账兜底**：任何补偿没覆盖到的（消息彻底丢失、业务方漏处理），每日对账用渠道账单反查，`ONLY_CHANNEL` 场景补单

### 1.4 乐观锁替代分布式锁

支付单状态流转的并发（回调重复、退款并发）用**乐观锁**解决，不引入分布式锁：

```sql
UPDATE pay_order
   SET pay_status = #{target}, version = version + 1, update_time = NOW()
 WHERE id = #{id} AND version = #{expectVersion}
   AND pay_status IN (#{fromStatuses})
```

- 回调并发：两个回调同时来，只有一个 `version` 匹配成功，另一个更新 0 行 → 判定为重复通知
- 退款并发：退款累计额更新带 `version` + 「已退+本次 ≤ pay_amount」约束，天然防超退

---

## 二、重构后模块分包结构

### 2.1 mall-pay 模块

```
mall-pay/src/main/java/cn/net/mall/pay/
├── PayApplication.java                    # 启动类（不变）
├── config/
│   ├── PaySecurityConfig.java             # 安全：回调路径放行（PermitAllProvider）
│   ├── SwaggerConfig.java                 # Swagger（不变）
│   ├── RocketMqConfig.java                # RocketMQ 生产者配置（通知业务方）
│   ├── MinioConfig.java                   # MinIO 客户端（对账单存储）
│   └── properties/
│       ├── PayProperties.java             # mall.pay.* 配置映射（crypto/notify/order/recon/channel）
│       └── AlipayProperties.java          # 支付宝沙箱配置（保留，兼容）
├── enums/
│   ├── PayStatusEnum.java                 # 支付状态（10待支付/20已支付/30已关闭/40支付失败）
│   ├── RefundStatusEnum.java              # 退款单状态（0待处理/1处理中/2成功/3失败）
│   ├── RefundAuditStatusEnum.java         # 退款审核状态
│   ├── ChannelCodeEnum.java               # 渠道编码（ALIPAY/WECHAT_PAY/WECHAT_MINI/MOCK）
│   ├── BizNotifyStatusEnum.java           # 业务方通知状态
│   ├── ReconStatusEnum.java               # 对账批次状态
│   └── DiffTypeEnum.java                  # 对账差异类型（LONG_PAYMENT 等 7 种）
├── entity/                                # MyBatis 实体（对应 8 张表）
│   ├── PayOrderEntity.java
│   ├── PayNotifyLogEntity.java
│   ├── PayRefundEntity.java
│   ├── PayChannelConfigEntity.java
│   ├── PayBizConfigEntity.java
│   ├── ReconBatchEntity.java
│   ├── ReconTempEntity.java
│   └── ReconResultEntity.java
├── mapper/                                # MyBatis Mapper 接口 + XML
│   ├── PayOrderMapper.java
│   ├── PayNotifyLogMapper.java
│   ├── PayRefundMapper.java
│   ├── PayChannelConfigMapper.java
│   ├── PayBizConfigMapper.java
│   ├── ReconBatchMapper.java
│   ├── ReconTempMapper.java
│   └── ReconResultMapper.java
├── service/                               # 核心业务服务
│   ├── PayCoreService.java                # 支付核心：创建/查询/关闭/回调处理/状态机
│   ├── RefundCoreService.java             # 退款核心：申请/审核判定/回调/冲正
│   ├── PayChannelService.java             # 渠道配置加载 + 策略分发
│   └── NotifyService.java                 # 业务方通知（MQ 发送 + 失败重推补偿）
├── channel/                               # 渠道策略层（PayChannelStrategy 实现）
│   ├── PayChannelStrategy.java            # 统一渠道接口（prepay/query/close/refund/...）
│   ├── alipay/
│   │   ├── AlipayChannelStrategy.java     # 支付宝策略（App/手机网站）
│   │   ├── AlipaySdkHolder.java           # 支付宝 SDK 客户端持有（用 pay_channel_config 初始化）
│   │   └── AlipayBillParser.java          # 支付宝对账单解析（csv.zip → BillRow）
│   ├── wechat/
│   │   ├── WechatPayChannelStrategy.java  # 微信支付策略（App）
│   │   ├── WechatMiniChannelStrategy.java # 微信小程序策略（JSAPI）
│   │   ├── WechatSdkHolder.java           # 微信 SDK 客户端持有
│   │   └── WechatBillParser.java          # 微信对账单解析（文本 CSV → BillRow）
│   └── mock/
│       └── MockChannelStrategy.java       # MOCK 模拟渠道（dev/test，config 开关）
├── support/                               # 支撑组件
│   ├── IdGenerator.java                   # 雪花 ID（复用 common-workid）+ 支付单号/退款单号生成
│   ├── AesCryptor.java                    # 渠道密钥 AES 加解密（crypto.secret-key）
│   ├── NotifySecurity.java                # 回调安全：timestamp 窗口 + nonce 去重（Redis）+ IP 白名单
│   └── MoneyUtil.java                     # 分/元 换算（支付宝元→分，只在此处）
```
> 📌 **加密/签名实现依据**：渠道侧的具体算法（支付宝 RSA2 / 微信 SHA256-RSA2048 + AES-256-GCM 回调解密 / 签名串格式 / 平台证书验签）见 `docs/39` 第二章「加密与签名机制」。`AesCryptor`/`NotifySecurity` 是实现类，算法细节以 39 为准。
```
├── recon/                                 # 对账子系统
│   ├── ReconJob.java                      # 对账定时任务（@Scheduled 次日10:30，入口）
│   ├── ReconService.java                  # 对账编排：下载→解析→入库→对比→结果
│   ├── ReconDownloadService.java          # 下载 + 落 MinIO（30秒链接原子下载）
│   ├── ReconParserService.java            # 解析 + 批量入库 recon_temp
│   ├── ReconCompareService.java           # 双批查询 + 内存撮合（无联表）
│   └── ReconDiffHandler.java              # 差异处置（长款/短款/单边单 → 自动/人工）
├── controller/
│   ├── internal/
│   │   └── PayInternalController.java     # 内部 Feign 接口（create/query/close/refund/refundQuery）
│   ├── mobile/
│   │   └── PayController.java             # 移动端收银台（调起支付参数，重写）
│   └── notify/
│       ├── AlipayNotifyController.java    # 支付宝回调
│       └── WechatNotifyController.java    # 微信回调（支付/退款）
├── listener/                              # 消息监听（MQ 消费）
│   └── RefundResultListener.java          # 退款结果通知消费（预留）
└── dto/                                   # 对外 DTO + 内部传输对象
    ├── PayCreateDTO.java                  # 创建支付单入参（业务字段，见 37 文档 2.2）
    ├── PayCreateResult.java               # 创建结果（payOrderNo + prepay 参数）
    ├── PayQueryDTO.java
    ├── PayCloseDTO.java
    ├── PayRefundDTO.java
    ├── PayRefundResult.java
    ├── PayRefundQueryDTO.java
    └── PayNotifyMessage.java              # MQ 通知消息体（业务方消费）
```

### 2.2 mall-pay-client 模块（新建独立 client）

```
mall-pay-client/src/main/java/cn/net/mall/pay/
├── client/
│   ├── config/
│   │   └── PayFeignAutoConfig.java        # FallbackFactory 注册（AutoConfiguration.imports）
│   ├── PayFeignClient.java                # Feign 接口（create/query/close/refund/refundQuery）
│   └── PayFeignFallbackFactory.java       # 熔断降级（业务方视角）
├── dto/                                   # 与 mall-pay 共享的 DTO
│   ├── PayCreateDTO.java
│   ├── PayCreateResult.java
│   ├── PayRefundDTO.java
│   └── PayNotifyMessage.java              # 业务方消费 MQ 用
└── constant/
    └── PayMqConstant.java                 # MQ Topic/Tag 常量（PAY_NOTIFY_MALL_ORDER 等）
```

> ⚠️ **依赖方向**：`mall-pay` **不依赖** `mall-pay-client`；`mall-order` **依赖** `mall-pay-client`。消除现有循环依赖（原先 pay 持有 orderFeignClient，order 又调 pay）。

---

## 三、核心类设计

### 3.1 渠道策略接口（channel/PayChannelStrategy）

```java
public interface PayChannelStrategy {
    /** 渠道编码：ALIPAY / WECHAT_PAY / WECHAT_MINI / MOCK */
    String channelCode();

    /** 创建支付单（merchant_order_no 幂等），返回拉起支付参数 */
    PayPrepayResult prepay(PayOrderEntity order, PayChannelConfigEntity config);

    /** 主动查询订单状态（兜底轮询） */
    PayQueryResult query(String merchantOrderNo, PayChannelConfigEntity config);

    /** 关闭未支付订单 */
    boolean close(String merchantOrderNo, PayChannelConfigEntity config);

    /** 申请退款（out_refund_no 幂等；超时后禁止自动重试，先查单） */
    RefundResult refund(PayRefundEntity refund, PayChannelConfigEntity config);

    /** 查询退款结果 */
    RefundQueryResult queryRefund(String refundNo, PayChannelConfigEntity config);

    /** 回调验签 */
    boolean verifyNotify(Map<String, String> params, String rawBody, PayChannelConfigEntity config);

    /** 解析回调报文为统一对象 */
    PayNotifyMessage parseNotify(String rawBody, PayChannelConfigEntity config);

    /** 下载对账单（30秒链接原子下载） */
    byte[] downloadBill(LocalDate tradeDate, PayChannelConfigEntity config);

    /** 解析对账单 → 统一账单行 */
    List<BillRow> parseBill(byte[] content);
}
```

**策略分发（channel 层）**：`PayChannelService` 启动时从 `pay_channel_config` 读全部启用渠道，按 `channel_code` 建 `Map<String, PayChannelStrategy>`，调用时 `strategy = strategies.get(channelCode)`。

**容错包裹（channel 层统一做）**：每个渠道调用的核心方法用 R4J 编程式包裹（`CircuitBreaker` + `TimeLimiter` + `Retry`），按 `mall.pay.channel` 配置；**refund 方法排除在 retry 外**（超时先查单）。

### 3.2 支付核心（service/PayCoreService）

```java
public class PayCoreService {
    // 创建支付单
    public PayCreateResult create(PayCreateDTO dto) {
        // 1. 校验：渠道启用、金额>0、pay_biz_config 存在该 biz_type
        // 2. 生成 pay_order（雪花ID，pay_order_no、merchant_order_no）
        // 3. prepay() 调渠道 → 返回拉起参数
        // 4. 捕获 uk_merchant_order_no 冲突 → 返回已存在单（幂等）
    }

    // 回调处理（渠道 → pay）
    public void handleNotify(PayNotifyLogEntity log) {
        // 1. 验签（NotifySecurity：timestamp窗口 + nonce去重）
        // 2. 乐观锁 10 → 20，回填 channel_trade_no / success_time / pay_amount
        // 3. 发 MQ 通知业务方（NotifyService）
        // 4. 更新 notify_count / notify_time
    }

    // 主动查单（兜底）
    public void queryUnpaidOrders() { ... }

    // 超时关单
    public void closeExpiredOrders() { ... }
}
```

### 3.3 退款核心（service/RefundCoreService）

```java
public class RefundCoreService {
    // 申请退款
    public PayRefundResult apply(PayRefundDTO dto) {
        // 1. 校验：支付单已支付、累计已退+本次 ≤ pay_amount
        // 2. 审核判定：按 biz_order_no+user_id 查 pay_order
        //    → 查到 → audit_status=0 自动退款
        //    → 查不到 → audit_status=1 人工审核
        // 3. 创建 pay_refund
        // 4. 自动退款 → channel.refund()
        // 5. 等回调 / 主动查单 → refund_status 成功
        // 6. 更新 pay_order.refund_amount + refund_status（乐观锁防超退）
    }
}
```

### 3.4 对账编排（recon/ReconService）

```
ReconJob（@Scheduled 次日10:30）
  └→ ReconService.run(渠道×昨日)
       ├→ 1. ReconDownloadService：下载账单 → MinIO 留存（30秒链接原子化）
       ├→ 2. ReconParserService：解析 → 批量灌入 recon_temp
       ├→ 3. ReconCompareService：
       │     ├ 总额校验（三岔：对平/第三方短/第三方长）
       │     └ 逐笔对比（双批查询 + 内存撮合，无联表）
       ├→ 4. 写 recon_result
       └→ 5. ReconDiffHandler：差异处置（自动/人工）
```

### 3.5 补偿机制（关键）

| 补偿点 | 机制 | 触发 |
|--------|------|------|
| MQ 发送失败重推 | `biz_notify_status=2` 扫描 + 退避重推 | 定时（1min/5min/15min/1h，5 次） |
| 回调丢失 | 主动查单轮询（创建 >5min 未回调 → query） | 定时每 5min |
| 支付单超时 | 超时关单（expire_time 到期 → 关闭） | 定时每 10min |
| 退款超时 | 先 queryRefund 查单再决定，不盲目重试 | 渠道超时后 |
| 对账差异 | 长款/短款/单边单 → 自动兜底或人工 | 每日对账 |

**所有补偿都是"幂等 + 可重入"**：无论重试多少次，靠乐观锁 + 唯一键 + 状态前置约束，不会产生副作用。

---

## 四、与 mall-order 的交互（无分布式事务）

```
mall-order                            mall-pay
   │ 下单成功                             │
   │──────────────── PayFeignClient.create() ────►│ 创建支付单，返回 prepay 参数
   │◀────────── 返回 { payOrderNo, prepayParams } ──│
   │ 用户调起支付（前端）                    │
   │                                   │
   │                   渠道回调 ─────────►│ 验签 → 状态机 → 本地事务
   │                                   │
   │◀──── PayNotifyMessage (MQ: PAY_NOTIFY_MALL_ORDER) ──│
   │ 消费 MQ → 更新业务订单已支付             │
   │ 扣减冻结库存（调 inventory）           │
   │                                   │
   │ 售后退款 ──── PayFeignClient.refund() ────►│ 审核 → 渠道退款 → MQ 通知退款结果
```

**一致性的兜底**：
- pay 侧：`biz_notify_status` 重推补偿（支付成功但通知失败 → 重推直到 mall-order 消费）
- order 侧：即使漏消费，用户看支付状态查 pay 服务（`payOrderNo` 查询），对账 `ONLY_CHANNEL` 也会兜住
- **不引入 Seata**——跨服务一致性靠 MQ 最终一致 + 对账，避免分布式事务的性能损耗和运维复杂度

---

## 五、重构步骤（建议顺序）

| 阶段 | 内容 | 说明 |
|:---:|------|------|
| 1 | **建表 + 实体/Mapper 脚手架** | 8 张表（已建），生成 entity/mapper（MyBatis 代码生成器） |
| 2 | **渠道抽象 + MOCK 渠道** | PayChannelStrategy 接口 + MockChannelStrategy，先跑通全流程 |
| 3 | **支付宝渠道打通** | AlipayChannelStrategy + 沙箱联调（prepay/回调/退款） |
| 4 | **微信渠道打通** | Wechat + WechatMini（等商户号） |
| 5 | **支付核心 + 退款核心** | PayCoreService/RefundCoreService + 状态机 + 补偿 |
| 6 | **对账子系统** | ReconJob + 下载/解析/对比/差异处置 |
| 7 | **client 模块 + 通知补偿** | 新 mall-pay-client + MQ 通知 + 重推 |
| 8 | **mall-order 接入改造** | 依赖方向反转，消费 MQ，删除 mockPay |

---

## 六、待确认事项

1. **实体生成方式**：用项目现有的 MyBatis 代码生成器生成 entity/mapper，还是手写？（项目其他模块已有生成器，建议复用）
2. **雪花 ID 复用**：`common-workid` 的具体使用方式（是工具类还是需要 Bean），实现时确认
3. **回调 URL 路径**：`/notify/alipay`、`/notify/wechat` 需要网关放行，确认网关配置项
4. **MQ Topic 命名**：`pay_biz_config.notify_mq_topic` 初始数据当前是 `PAY_NOTIFY_TOPIC` 占位，建议改为 `PAY_NOTIFY_MALL_ORDER`
5. **对账 MinIO 目录**：`pay/recon` 前缀，确认 bucket `mall-dev` 下路径规划

# BFF 写透传迁移计划

> 基于 `34-BFF接口规范.md` 的执行方案
> 日期：2026-07-19

---

## 一、为什么需要迁移

### 1.1 现状问题

当前 `mall-admin-bff` 和 `mall-mobile-bff` 中混杂了大量写操作透传接口：

```java
// AdminBasicController.java — BFF 中的典型写透传
@PostMapping("/basic/photo/insert")
public ApiResult<Integer> insertPhoto(@RequestBody PhotoEntity entity) {
    return ApiResultUtil.success(basicFeignClient.insertPhoto(entity));
    // ↑ 这行代码在 BFF 中完全多余
    // BFF 除了转发什么都没做，还要多一次网络跳转
}
```

这违反了 BFF 的设计初衷。BFF 的价值在于**读聚合**——把多个微服务的数据拼在一起减少前端请求，而不是当传声筒。

### 1.2 透传的代价

| 代价 | 说明 |
|------|------|
| 性能 | 每次写操作多一次网络跳转（前端 → BFF → 微服务），虽然同机房 1-3ms，但没必要 |
| 维护 | BFF 每个写透传方法都要写 Feign 调用代码，增删改查 × N 个模块 = 大量模板代码 |
| 认知负担 | 新人看 BFF 代码分不清哪些是真正的聚合逻辑，哪些是纯透传 |
| 链路复杂度 | 出问题时排查链路变长，多了一层 |

### 1.3 迁移后的收益

```
迁移前：前端 → Gateway → BFF → Feign → 微服务  （写操作多跳一次）
迁移后：前端 → Gateway → 直接到微服务          （写操作一步到位）
```

BFF 只留下真正需要聚合的接口（约 16 个），代码量减少 70% 以上。

---

## 二、总体方案

### 2.1 架构变更

```
迁移前：
┌────────┐    ┌───────────┐    ┌──────────┐    ┌──────────┐
│ 前端    │ →  │ Gateway   │ →  │ BFF      │ →  │ 微服务   │
│        │    │ /api/admin│    │ 写透传   │    │ 真实逻辑 │
└────────┘    └───────────┘    └──────────┘    └──────────┘
                                  ↑ 多余的一跳

迁移后：
┌────────┐    ┌───────────┐    ┌──────────┐
│ 前端    │ →  │ Gateway   │ →  │ 微服务   │
│        │    │ /api/*/** │    │ 写操作   │
│ 写操作 │    │ 直通       │    │          │
└────────┘    └───────────┘    └──────────┘

┌────────┐    ┌───────────┐    ┌──────────┐    ┌──────────┐
│ 前端    │ →  │ Gateway   │ →  │ BFF      │ →  │ 微服务   │
│        │    │ /api/admin│    │ 读聚合   │    │ /v1/int   │
│ 读操作 │    │           │    │          │    │ ernal/**│
└────────┘    └───────────┘    └──────────┘    └──────────┘
```

### 2.2 关键决策

**为什么写操作直通微服务时需要微服务自己包 ApiResult？**

因为 Gateway 无法对响应体做通用包装——Gateway 是 WebFlux 非阻塞架构，操作响应体需要 buffer 整个 payload，违背非阻塞设计。所以微服务的 `/v1/xxx` 公开接口必须自己保证返回 `ApiResult<T>`。

**那 `GlobalApiResultHandler` 为什么不包 `/v1/xxx`？**

因为 `/v1/xxx` 同时被两种场景调用：
- 前端通过 Gateway 直通（需要 ApiResult）
- BFF 通过 Feign 调用 `/v1/xxx`（如果包了 ApiResult，Feign 反序列化会失败）

我们的方案是：Feign 调用统一走 `/v1/internal/xxx`（裸类型），而 `/v1/xxx` 给前端直通用（ApiResult）。这样职责清晰，没有冲突。

---

## 三、分阶段执行计划

### Phase 1：mall-basic（基础服务）

**为什么从 basic 开始？**

基础服务（图片、敏感词、通知、字典）是纯粹的单表 CRUD，没有业务状态机、没有事务关联。即使迁移出错，影响范围可控——图片传不上去不影响下单支付。适合作为整个迁移的"试水"阶段，验证方案的可行性，积累经验。

**涉及接口（18 个）：**

| 操作 | 当前 BFF 路径 | 迁移后微服务路径 |
|------|-------------|-----------------|
| 新增图片 | `POST /admin/v1/basic/photo/insert` | `POST /v1/photo/insert` |
| 修改图片 | `POST /admin/v1/basic/photo/update` | `POST /v1/photo/update` |
| 删除图片 | `POST /admin/v1/basic/photo/delete` | `POST /v1/photo/deleteByIds` |
| 新增分组 | `POST /admin/v1/basic/photoGroup/insert` | `POST /v1/photoGroup/insert` |
| 修改分组 | `POST /admin/v1/basic/photoGroup/update` | `POST /v1/photoGroup/update` |
| 删除分组 | `POST /admin/v1/basic/photoGroup/delete` | `POST /v1/photoGroup/deleteByIds` |
| 新增敏感词 | `POST /admin/v1/basic/sensitiveWord/insert` | `POST /v1/sensitiveWord/insert` |
| 修改敏感词 | `POST /admin/v1/basic/sensitiveWord/update` | `POST /v1/sensitiveWord/update` |
| 删除敏感词 | `POST /admin/v1/basic/sensitiveWord/delete` | `POST /v1/sensitiveWord/deleteByIds` |
| 新增通知 | `POST /admin/v1/basic/notify/insert` | `POST /v1/notify/insert` |
| 修改通知 | `POST /admin/v1/basic/notify/update` | `POST /v1/notify/update` |
| 删除通知 | `POST /admin/v1/basic/notify/delete` | `POST /v1/notify/deleteByIds` |
| 推送全员 | `POST /admin/v1/basic/notify/push/all` | `POST /v1/notify/push/all` |
| 推送指定 | `POST /admin/v1/basic/notify/push/user` | `POST /v1/notify/push/user` |

**为什么这些接口新路径直接是 `/v1/xxx` 而不是保留 BFF 前缀？**

因为这些接口不再经过 BFF，前端通过 Gateway 的 `/api/basic/**` 路由直接到达 basic 微服务。微服务侧的路径沿用现有的 `/v1/basic/xxx` 风格即可，无需额外包装。

**步骤：**

1. **确认 basic 微服务的 Controller 已经返回 ApiResult** — 如果没有，手动包一层或确认 GlobalApiResultHandler 会包装
2. **确认 Gateway 路由** — `/api/basic/** → lb://mall-basic-api` 已存在（现有路由）
3. **在 admin-bff 中删除对应的透传方法** — 从 AdminBasicController.java 中移除
4. **前端修改请求地址** — 从 `/api/admin/basic/photo/insert` 改为 `/api/basic/photo/insert`
5. **测试验证**

---

### Phase 2：mall-admin（认证体系）

**为什么 admin 放在第二？**

因为依赖关系简单。用户、角色、部门、岗位、菜单都是独立的树/表结构，相互之间除了 RBAC 关联外没有复杂的跨表事务。而且数量多（约 24 个接口），清掉后 BFF 的代码量会骤减，让人立刻感受到效果。

**特殊处理：**

登录接口（`/admin/v1/auth/login`）虽然语义上是写操作（创建 session/token），但它同时做了登录验证 + 获取用户信息 + 获取菜单三件事。如果按规范应该拆成两段：
- 登录认证（写）→ `POST /v1/auth/web/user/login`
- 用户信息+菜单（读聚合）→ 保留在 BFF

但考虑到登录场景前端就是一次调用，拆开反而增加请求数，**建议保留在 BFF**。

---

### Phase 3：mall-product（商品体系）

**为什么 product 放在第三？**

因为商品涉及的文件多（商品、分类、品牌、单位、属性、轮播图、首页商品），但好消息是：每个都是独立的 CRUD，没有跨实体的写操作。商品本身的写操作（新增/修改）已经在之前的库存拆分中去掉了库存字段。所以迁移风险可控。

---

### Phase 4：mall-marketing（营销体系）

优惠券、秒杀、发券记录。这些接口少（约 12 个），但因为涉及营销活动的状态（进行中/已结束），需要确认迁移后 coupon 服务自己管理状态一致。

---

### Phase 5：mall-order（订单体系）

**为什么订单放最后？**

订单涉及状态机（待支付 → 已支付 → 已发货 → 已完成 → 已取消→ 退货中），每个状态的变更都有前置条件和后置动作（如支付后要冻结库存）。放在最后是因为：
- 业务复杂度最高
- 出问题影响最大（涉及钱和货）
- 前面的 Phase 1-4 已经验证了整个迁移模式的可靠性

---

### Phase 6：mobile-bff + 收尾

mobile-bff 同理迁移。最后清理 BFF 代码中不再需要的 Feign 客户端引用和 ForwardController 中可能冲突的路由。

---

## 四、风险与应对

| 风险 | 应对 |
|------|------|
| 前端改了地址但旧地址还在用 | BFF 保留旧透传接口一段时间，加 `@Deprecated` 标记，前端全量切完后再删 |
| 微服务接口返回格式不一致 | 每个 microservice 确认 Controller 已手动包 ApiResult 再迁移 |
| Gateway 路由冲突 | BFF 的 ForwardController 路径与直通路经可能重叠，按规范清理 |
| 迁移过程中 BFF 还在被调用 | 先加后删：前端改地址 → 确认跑通 → BFF 删旧接口 |
| Spring Security 权限 | 直通微服务的写操作需要带上 JWT token，通过 Gateway 透传即可 |

---

## 五、迁移检查清单

### 每个接口迁移的通用步骤：

- [ ] 微服务公开接口返回裸 DTO → Controller 手动加 ApiResult 包装，或确认 GlobalApiResultHandler 自动处理
- [ ] Feign 调用全部改为走 `/v1/internal/xxx`
- [ ] BFF 写透传方法删除
- [ ] 前端请求地址更新
- [ ] 更新测试脚本
- [ ] 更新 API 文档（Swagger）

---

## 六、预计收益

| 模块 | 迁移后 BFF 代码变化 |
|------|------------------|
| mall-basic BFF Controller | 删除 ~15 行透传代码 |
| mall-admin（系统管理） | 删除 ~30 行透传代码 |
| mall-product 相关 | 删除 ~40 行透传代码 |
| mall-marketing 相关 | 删除 ~20 行透传代码 |
| mall-order 相关 | 删除 ~15 行透传代码 |
| BFF 写接口总量 | **减少 ~80%** |

迁移完成后，admin-bff 的 controller 从 ~15 个文件减少到 ~6 个，只保留真正的读聚合。

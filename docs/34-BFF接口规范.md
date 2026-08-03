# BFF API 接口规范

> 目的：定义 BFF 与微服务之间的职责边界、接口路径命名规则、响应格式规范
> 日期：2026-07-19
> 状态：初稿

---

## 一、架构原则

### 1.1 BFF 定义

BFF（Backend For Frontend）是**前端的后端**，核心职责是**读聚合**——把多个微服务的数据拼在一起，减少前端请求次数。

> **2026-08 更新：`mall-admin-bff` 已拆除**，管理后台前端直连各微服务公开接口；仅 **mobile-bff** 保留（移动端首页/商品详情等跨服务聚合场景仍有价值）。

### 1.2 分层职责

```
┌─────────────────────────────────────────────────┐
│                  前端（Vue / UniApp）              │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│              Gateway（路由 + 鉴权）                │
│  /api/admin-api/** → lb://mall-admin-api (直连)  │
│  /api/mobile/**   → lb://mall-mobile-bff (读聚合)│
│  /api/*/**        → 直通微服务       (写操作直通)  │
└──────────────────┬──────────────────────────────┘
                   │
    ┌──────────────┴──────────────┐
    │                              │
┌───▼────────────┐    ┌───────────▼──────────┐
│  BFF 层         │    │  微服务层             │
│  mall-mobile-bff│    │  mall-admin          │
│                 │    │  mall-product        │
│ 读聚合 + 返回   │    │  mall-order          │
│ ApiResult 给前端│    │  mall-inventory      │
└────────────────┘    │  mall-basic          │
                      │  mall-marketing      │
                      │  mall-message        │
                      │  ...                 │
                      │                      │
                      │  读写都有             │
                      │  管理后台直连（无 BFF） │
                      │  写操作直通 Gateway   │
                      │  读操作走 /v1/internal│
                      └──────────────────────┘
```

### 1.3 核心规则

| 规则 | 说明 |
|------|------|
| **mobile-bff 做读聚合；admin 无 BFF** | 管理后台无 BFF，前端直连各微服务；仅移动端保留 mobile-bff 做跨服务读聚合 |
| **写操作直通微服务** | 前端 → Gateway → 微服务 `/v1/xxx` 公开接口 |
| **读操作：admin 直连微服务；mobile 走 BFF** | admin 读操作直连微服务公开接口；mobile 读操作走 mobile-bff → Feign(`/v1/internal/xxx`) |
| **响应格式统一** | 所有给前端的响应都是 `ApiResult<T>` 格式 |
| **Feign 内部接口走裸 DTO** | 微服务的 `/v1/internal/xxx` 返回裸类型，Handler 不包装 |

---

## 二、接口路径命名规范

### 2.1 路径层级

```
/{prefix}/{version}/{domain}/{action}
  │        │        │        │
  │        │        │        └── 操作名（searchByPage / insert / update / detail 等）
  │        │        └─────────── 业务域（auth/user / product / order / inventory 等）
  │        └──────────────────── 版本（v1）
  └───────────────────────────── 前缀（api/<service> / mobile / v1）
```

### 2.2 前缀分类

| 前缀 | 所属层 | 职责 | 响应格式 | Handler 处理 |
|------|--------|------|----------|-------------|
| `/api/admin-api/v1/` | 管理后台直连 | 前端直连 mall-admin-api（StripPrefix=2，落到 `/v1/`） | `ApiResult<T>` | ✅ 自动包装 |
| `/mobile/v1/` | mobile-bff | 移动端读聚合 | `ApiResult<T>` | ✅ 自动包装 |
| `/v1/` | 微服务公开 | 写操作（增删改）、单一读查询 | `ApiResult<T>` | ❌ 不包装（Controller 自行处理） |
| `/v1/internal/` | 微服务内部 | Feign 调用（BFF 聚合、服务间调用） | 裸 DTO | ❌ 不包装 |

> 管理后台**无 BFF 层**，`/api/admin-api/**` 由 Gateway 以 `StripPrefix=2` 直连 `lb://mall-admin-api`，剥掉 `/api` + `admin-api` 后落到微服务的 `/v1/**`。
> 其它业务服务（product/inventory/basic/order/marketing/message 等）同理，`/api/<service>/v1/**` 直连各微服务公开接口。

### 2.3 操作动词命名

**读操作（READ）：**

| 动词 | 含义 | 示例 |
|------|------|------|
| `searchByPage` | 分页查询 | `POST /api/admin-api/v1/auth/user/searchByPage` |
| `findByIds` | 批量查询 | `POST /api/admin-api/v1/auth/user/findByIds` |
| `getMenuTree` | 树形结构 | `GET /api/admin-api/v1/auth/menu/getMenuTree` |
| `all` | 全部列表 | `GET /api/admin-api/v1/auth/role/all` |
| `getCode` | 验证码 | `GET /api/admin-api/v1/auth/web/user/getCode` |
| `todayCount` | 统计 | `GET /api/admin-api/v1/auth/user/todayCount` |
| `info` | 用户信息 | `GET /api/admin-api/v1/auth/web/user/info` |

**写操作（WRITE）：**

| 动词 | 含义 | 示例 |
|------|------|------|
| `insert` | 新增 | `POST /v1/user/insert` |
| `update` | 修改 | `POST /v1/user/update` |
| `delete` | 删除 | `POST /v1/user/deleteByIds` |
| `save` | 新增或更新 | `POST /v1/address/save` |
| `cancel` | 取消/撤回 | `POST /v1/order/cancel` |

> 写操作命名保持业务语义，不用 CRUD 缩写。

---

## 三、GlobalApiResultHandler 规则

### 3.1 匹配规则

```java
// GlobalApiResultHandler.matchUrl()
// 内部 Feign 接口不包装
if (uri.contains("/v1/internal/")) return false;
return uri.startsWith("/v1/")
        || uri.startsWith("/mobile/v1/");
```

- **自动包装** ✅：`/v1/`（微服务公开接口，含管理后台直连的 `/api/admin-api/v1/` 落到 `/v1/` 后）、`/mobile/v1/`（mobile-bff）
- **不包装** ⛔：`/v1/internal/`（Feign 内部调用需要裸 DTO）、ForwardController 代理路径等其它路径

### 3.2 处理逻辑

```java
// GlobalApiResultHandler.beforeBodyWrite()
if (body instanceof ApiResult) return body;       // 已包则跳过
return ApiResultUtil.success(body);                // 裸类型自动包
```

### 3.3 使用约定

所有服务通过 `common-web` 的 `AutoConfiguration.imports` 自动获取 `GlobalApiResultHandler`，不需要手动加 `@EnableApiResultWrapper`。

> 注意：Handler 在 `/v1/`（微服务公开接口，含管理后台直连路径）和 `/mobile/v1/`（mobile-bff）路径自动包装。`/v1/internal/` 不包装，由 Feign 消费裸 DTO。

---

## 四、响应格式

### 4.1 统一数据结构

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | int | 200 成功，其他见错误码表 |
| `message` | string | 成功为 null，失败为错误描述 |
| `data` | T | 实际业务数据 |

### 4.2 分页响应

```json
{
  "code": 200,
  "message": null,
  "data": {
    "list": [...],
    "pageNum": 1,
    "pageSize": 10,
    "total": 100,
    "pages": 10
  }
}
```

### 4.3 错误响应

```json
{
  "code": 400,
  "message": "参数校验失败：xxx 不能为空",
  "data": null
}
```

---

## 五、接口分类与迁移计划

### 5.1 管理后台接口（前端直连各微服务）

> **`mall-admin-bff` 已拆除**。管理后台前端**不再经过 BFF 层**，改为通过 Gateway 以 `/api/<service>/v1/**` 直连各微服务公开接口（`StripPrefix=2` 剥掉 `/api` + 服务名后落到 `/v1/**`）。

| 功能域 | 所属微服务 | 直连路径前缀 |
|--------|-----------|-------------|
| 认证 / 系统管理 / 用户 / 菜单 / 角色 / 部门 / 岗位 / 在线用户 | mall-admin（`mall-admin-api`） | `/api/admin-api/v1/auth/**` |
| 商品 / 分类 / 品牌 / 单位 / 属性 / 属性值 / 分组 / 公告 / 推荐商品 / 评价 | mall-product（`mall-product-api`） | `/api/product/v1/**` |
| 库存 / 仓库 / 批次 / 流水 | mall-inventory（`mall-inventory-api`） | `/api/inventory/v1/**` |
| 图片 / 图片分组 / 敏感词 / 作业 / 字典 / 文件上传 | mall-basic（`mall-basic-api`） | `/api/basic/v1/**` |
| 订单分页 / 退货分页 / 退货详情（管理端控制器） | mall-order（`mall-order-api`） | `/api/order/v1/admin/order/**` |
| 优惠券 / 秒杀 | mall-marketing（`mall-marketing-api`） | `/api/marketing/v1/**` |
| 通知 / 站内信 | mall-message（`mall-message-api`） | `/api/message/v1/message/notify/**` |

**说明：**
- mall-admin 的公开控制器已在 `auth` 包下就绪：`WebUserController`（`/v1/auth/web/user/**`，登录/登出/验证码/用户信息/菜单/在线用户）、`UserController`（`/v1/auth/user/**`）、`RoleController`（`/v1/auth/role/**`）、`MenuController`（`/v1/auth/menu/**`）、`DeptController`（`/v1/auth/dept/**`）、`JobController`（`/v1/auth/job/**`）。
- mall-order 新增管理端公开控制器 `OrderAdminController`（`/v1/admin/order/**`：`page` / `return/page` / `return/detail`），供前端直连订单分页/退货查询。
- dashboard 统计卡片由**前端分别调用各服务统计接口自行组装**（用户数→mall-admin，订单数/销售额→mall-order `/v1/admin/trade/statistics`，商品总数→mall-product）。
- 前端直连时注意路径段差异：BFF 的扁平路径（如 `/admin/v1/system/role/page`）对应 mall 服务的真实路径（`/v1/auth/role/searchByPage`），不能只改前缀。

### 5.2 mobile-bff 分类

#### ✅ 保留在 BFF 的读聚合接口

| 路径 | 聚合逻辑 |
|------|---------|
| `GET /mobile/v1/home/index` | 首页聚合（轮播+分类+公告+推荐商品） |
| `GET /mobile/v1/product/{productId}/detail` | 商品详情聚合（商品+评论+收藏） |
| `GET /mobile/v1/user/profile` | 用户中心（用户信息+订单统计） |
| `POST /mobile/v1/checkout/preview` | 结算预览（订单+地址+优惠券） |
| `POST /mobile/v1/cart/list` | 购物车列表 |

#### 🔄 需要迁移的写透传

| 当前 BFF 路径 | 迁移目标 |
|--------------|---------|
| `POST /mobile/v1/cart/add` | `POST /v1/cart/add` |
| `POST /mobile/v1/cart/update` | `POST /v1/cart/update` |
| `POST /mobile/v1/cart/delete` | `POST /v1/cart/delete` |
| `POST /mobile/v1/auth/login` | `POST /v1/auth/web/user/login` |
| `POST /mobile/v1/auth/register` | `POST /v1/member/register` |
| `POST /mobile/v1/auth/logout` | `POST /v1/auth/web/user/logout` |
| `POST /mobile/v1/user/update` | `POST /v1/member/update` |
| `POST /mobile/v1/user/avatar` | `POST /v1/member/updateAvatar` |
| `POST /mobile/v1/user/address/save` | `POST /v1/address/save` |
| `POST /mobile/v1/user/address/delete` | `POST /v1/address/deleteByIds` |
| `POST /mobile/v1/coupon/receive` | `POST /v1/coupon/receive` |
| `POST /mobile/v1/order/submit` | `POST /v1/order/submit` |
| `POST /mobile/v1/order/cancel` | `POST /v1/order/cancel` |
| `POST /mobile/v1/order/pay/mock` | `POST /v1/order/pay/mock` |
| `POST /mobile/v1/order/return/apply` | `POST /v1/return/apply` |

---

## 六、Feign 接口规范

### 6.1 路径规则

微服务之间通过 Feign 调用的接口，放在 `/v1/internal/` 路径下：

```java
@FeignClient(name = "mall-inventory-api", path = "/v1/internal/inventory")
public interface InventoryFeignClient {
    @GetMapping("/{productId}")
    InventoryDTO getByProductId(@PathVariable Long productId);
}
```

### 6.2 返回类型

Feign 客户端声明**裸 DTO**，不包 `ApiResult`：

```java
// ✅ 正确
InventoryDTO getByProductId(Long productId);

// ❌ 错误
ApiResult<InventoryDTO> getByProductId(Long productId);
```

### 6.3 异常处理

Feign 调用失败通过 `FallbackFactory` + `FeignFallbackProxy` 降级：

```java
@Slf4j
public class InventoryFeignFallbackFactory implements FallbackFactory<InventoryFeignClient> {
    @Override
    public InventoryFeignClient create(Throwable cause) {
        log.error("InventoryFeignClient 调用失败", cause);
        return FeignFallbackProxy.create(InventoryFeignClient.class, cause);
    }
}
```

---

## 七、总结

### 7.1 数据一览

| 指标 | 数量 |
|------|:----:|
| 管理后台直连接口（admin 无 BFF，前端直连各微服务） | ~150 |
| mobile-bff 保留（读聚合） | ~5 |
| mobile-bff 写透传（已迁至微服务） | ~15 |
| 微服务 `/v1/internal/` Feign 接口 | ~20 |

### 7.2 关键约束

1. **BFF 不包含任何写操作逻辑**（mobile-bff 也不含写逻辑）
2. **管理后台无 BFF 层**，前端直连各微服务公开接口（`/api/<service>/v1/**`）
3. **微服务公开接口（`/v1/`）统一返回 `ApiResult<T>`**（Controller 手动包或依赖注解）
4. **微服务内部接口（`/v1/internal/`）返回裸 DTO**
5. **GlobalApiResultHandler 在 `/v1/` 和 `/mobile/v1/` 路径自动包装，`/v1/internal/` 不包装**
6. **前端的写请求通过 Gateway 直通微服务，不经过 BFF**

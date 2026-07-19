# BFF API 接口规范

> 目的：定义 BFF 与微服务之间的职责边界、接口路径命名规则、响应格式规范
> 日期：2026-07-19
> 状态：初稿

---

## 一、架构原则

### 1.1 BFF 定义

BFF（Backend For Frontend）是**前端的后端**，核心职责是**读聚合**——把多个微服务的数据拼在一起，减少前端请求次数。

### 1.2 分层职责

```
┌─────────────────────────────────────────────────┐
│                  前端（Vue / UniApp）              │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│              Gateway（路由 + 鉴权）                │
│  /api/admin/** → mall-admin-bff  (BFF 读聚合)    │
│  /api/mobile/** → mall-mobile-bff (BFF 读聚合)   │
│  /api/*/**      → 直通微服务       (写操作直通)    │
└──────────────────┬──────────────────────────────┘
                   │
    ┌──────────────┴──────────────┐
    │                              │
┌───▼────────────┐    ┌───────────▼──────────┐
│  BFF 层         │    │  微服务层             │
│  mall-admin-bff │    │  mall-admin          │
│  mall-mobile-bff│    │  mall-product        │
│                 │    │  mall-order          │
│ 读聚合 + 返回   │    │  mall-inventory      │
│ ApiResult 给前端│    │  ...                 │
└────────────────┘    │                      │
                      │  读写都有             │
                      │  写操作直通 Gateway   │
                      │  读操作走 /v1/internal│
                      └──────────────────────┘
```

### 1.3 核心规则

| 规则 | 说明 |
|------|------|
| **BFF 只做读聚合** | 所有写操作（insert/update/delete）不经过 BFF |
| **写操作直通微服务** | 前端 → Gateway → 微服务 `/v1/xxx` 公开接口 |
| **读操作走 BFF** | 前端 → Gateway → BFF → Feign(`/v1/internal/xxx`) |
| **响应格式统一** | 所有给前端的响应都是 `ApiResult<T>` 格式 |
| **Feign 内部接口走裸 DTO** | 微服务的 `/v1/internal/xxx` 返回裸类型，Handler 不包装 |

---

## 二、接口路径命名规范

### 2.1 路径层级

```
/{prefix}/{version}/{domain}/{action}
  │        │        │        │
  │        │        │        └── 操作名（page / insert / update / detail 等）
  │        │        └─────────── 业务域（user / product / order / inventory 等）
  │        └──────────────────── 版本（v1）
  └───────────────────────────── 前缀（admin / mobile / v1）
```

### 2.2 前缀分类

| 前缀 | 所属层 | 职责 | 响应格式 | Handler 处理 |
|------|--------|------|----------|-------------|
| `/admin/v1/` | admin-bff | 管理后台读聚合 | `ApiResult<T>` | ✅ 自动包装 |
| `/mobile/v1/` | mobile-bff | 移动端读聚合 | `ApiResult<T>` | ✅ 自动包装 |
| `/v1/` | 微服务公开 | 写操作（增删改）、单一读查询 | `ApiResult<T>` | ❌ 不包装（Controller 自行处理） |
| `/v1/internal/` | 微服务内部 | Feign 调用（BFF 聚合、服务间调用） | 裸 DTO | ❌ 不包装 |

### 2.3 操作动词命名

**读操作（READ）：**

| 动词 | 含义 | 示例 |
|------|------|------|
| `page` | 分页查询 | `POST /admin/v1/user/page` |
| `detail` | 详情 | `GET /admin/v1/product/detail` |
| `tree` | 树形结构 | `GET /admin/v1/system/menu/tree` |
| `all` | 全部列表 | `GET /admin/v1/system/role/all` |
| `list` | 条件列表 | `POST /admin/v1/system/menu/list` |
| `edit-data` | 编辑页所需聚合数据 | `GET /admin/v1/product/{id}/edit-data` |
| `stats` | 统计数据 | `GET /admin/v1/dashboard/stats` |

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
return uri.startsWith("/admin/v1/")
    || uri.startsWith("/mobile/v1/");
```

- **BFF 路径** ✅ 自动包装为 `ApiResult<T>`
- **其它路径**（`/v1/`、`/v1/internal/`、ForwardController 代理路径）⛔ 不包装

### 3.2 处理逻辑

```java
// GlobalApiResultHandler.beforeBodyWrite()
if (body instanceof ApiResult) return body;       // 已包则跳过
return ApiResultUtil.success(body);                // 裸类型自动包
```

### 3.3 使用约定

所有服务通过 `common-web` 的 `AutoConfiguration.imports` 自动获取 `GlobalApiResultHandler`，不需要手动加 `@EnableApiResultWrapper`。

> 注意：Handler 只在 BFF 层（`/admin/v1/` 和 `/mobile/v1/` 路径）有效。微服务的 `/v1/xxx` 接口由各 Controller 自行决定是否手动包 `ApiResult`。

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

### 5.1 admin-bff 分类

#### ✅ 保留在 BFF 的读聚合接口（16 个）

这些接口做了聚合操作（调用多个 Feign 或拼装数据），应该保留在 BFF：

| 路径 | 聚合逻辑 |
|------|---------|
| `GET /admin/v1/product/{id}/edit-data` | ProductDTO + InventoryDTO |
| `GET /admin/v1/user/{id}/edit-data` | UserDTO + 角色/部门关联数据 |
| `GET /admin/v1/dashboard/stats` | 用户数 + 订单数 + 销售额统计 |
| `GET /admin/v1/auth/userInfo` | 用户信息 + 权限 |
| `GET /admin/v1/auth/menus` | 菜单树（前端需要格式） |
| `GET /admin/v1/inventory/{productId}` | 库存查询（单一查询，轻量聚合） |
| `POST /admin/v1/inventory/batch` | 批量库存（轻量聚合） |

#### 🔄 需要迁移到微服务的写透传接口（43 个）

这些接口在 BFF 中仅做 `feignClient.method()` 透传，没有任何聚合逻辑，**应该迁移到微服务层，通过 Gateway 直通**：

| 当前 BFF 路径 | 迁移目标（微服务） |
|--------------|------------------|
| `POST /admin/v1/user/insert` | `POST /v1/user/insert` |
| `POST /admin/v1/user/update` | `POST /v1/user/update` |
| `POST /admin/v1/user/delete` | `POST /v1/user/deleteByIds` |
| `POST /admin/v1/user/updateAvatar` | `POST /v1/user/updateAvatar` |
| `POST /admin/v1/user/updateUser` | `POST /v1/user/updateUser` |
| `POST /admin/v1/system/role/insert` | `POST /v1/role/insert` |
| `POST /admin/v1/system/role/update` | `POST /v1/role/update` |
| `POST /admin/v1/system/role/delete` | `POST /v1/role/deleteByIds` |
| `POST /admin/v1/system/dept/insert` | `POST /v1/dept/insert` |
| `POST /admin/v1/system/dept/update` | `POST /v1/dept/update` |
| `POST /admin/v1/system/dept/delete` | `POST /v1/dept/deleteByIds` |
| `POST /admin/v1/system/job/insert` | `POST /v1/job/insert` |
| `POST /admin/v1/system/job/update` | `POST /v1/job/update` |
| `POST /admin/v1/system/job/delete` | `POST /v1/job/deleteByIds` |
| `POST /admin/v1/product/insert` | `POST /v1/product/insert` |
| `POST /admin/v1/product/update` | `POST /v1/product/update` |
| `POST /admin/v1/product/delete` | `POST /v1/product/deleteByIds` |
| `POST /admin/v1/product-mgr/category/insert` | `POST /v1/category/insert` |
| `POST /admin/v1/product-mgr/category/update` | `POST /v1/category/update` |
| `POST /admin/v1/product-mgr/category/delete` | `POST /v1/category/deleteByIds` |
| `POST /admin/v1/product-mgr/brand/insert` | `POST /v1/brand/insert` |
| `POST /admin/v1/product-mgr/brand/update` | `POST /v1/brand/update` |
| `POST /admin/v1/product-mgr/brand/delete` | `POST /v1/brand/deleteByIds` |
| `POST /admin/v1/product-mgr/unit/insert` | `POST /v1/unit/insert` |
| `POST /admin/v1/product-mgr/unit/update` | `POST /v1/unit/update` |
| `POST /admin/v1/product-mgr/unit/delete` | `POST /v1/unit/deleteByIds` |
| `POST /admin/v1/product-extra/attribute/insert` | `POST /v1/attribute/insert` |
| `POST /admin/v1/product-extra/attribute/update` | `POST /v1/attribute/update` |
| `POST /admin/v1/product-extra/attribute/delete` | `POST /v1/attribute/deleteByIds` |
| `POST /admin/v1/product-extra/attributeValue/insert` | `POST /v1/attributeValue/insert` |
| `POST /admin/v1/product-extra/attributeValue/update` | `POST /v1/attributeValue/update` |
| `POST /admin/v1/product-extra/attributeValue/delete` | `POST /v1/attributeValue/deleteByIds` |
| `POST /admin/v1/product-extra/productGroup/insert` | `POST /v1/productGroup/insert` |
| `POST /admin/v1/product-extra/productGroup/update` | `POST /v1/productGroup/update` |
| `POST /admin/v1/product-extra/productGroup/delete` | `POST /v1/productGroup/deleteByIds` |
| `POST /admin/v1/product-extra/indexNotice/insert` | `POST /v1/indexNotice/insert` |
| `POST /admin/v1/product-extra/indexNotice/update` | `POST /v1/indexNotice/update` |
| `POST /admin/v1/product-extra/indexNotice/delete` | `POST /v1/indexNotice/deleteByIds` |
| `POST /admin/v1/product-extra/indexProduct/insert` | `POST /v1/indexProduct/insert` |
| `POST /admin/v1/product-extra/indexProduct/update` | `POST /v1/indexProduct/update` |
| `POST /admin/v1/product-extra/indexProduct/delete` | `POST /v1/indexProduct/deleteByIds` |
| `POST /admin/v1/inventory/inbound` | `POST /v1/inventory/inbound` |
| `POST /admin/v1/shopping/productComment/insert` | `POST /v1/productComment/insert` |
| `POST /admin/v1/shopping/productComment/update` | `POST /v1/productComment/update` |
| `POST /admin/v1/shopping/productComment/delete` | `POST /v1/productComment/deleteByIds` |
| `POST /admin/v1/basic/photo/insert` | `POST /v1/photo/insert` |
| `POST /admin/v1/basic/photo/update` | `POST /v1/photo/update` |
| `POST /admin/v1/basic/photo/delete` | `POST /v1/photo/deleteByIds` |
| `POST /admin/v1/basic/photoGroup/insert` | `POST /v1/photoGroup/insert` |
| `POST /admin/v1/basic/photoGroup/update` | `POST /v1/photoGroup/update` |
| `POST /admin/v1/basic/photoGroup/delete` | `POST /v1/photoGroup/deleteByIds` |
| `POST /admin/v1/basic/sensitiveWord/insert` | `POST /v1/sensitiveWord/insert` |
| `POST /admin/v1/basic/sensitiveWord/update` | `POST /v1/sensitiveWord/update` |
| `POST /admin/v1/basic/sensitiveWord/delete` | `POST /v1/sensitiveWord/deleteByIds` |
| `POST /admin/v1/basic/notify/insert` | `POST /v1/notify/insert` |
| `POST /admin/v1/basic/notify/update` | `POST /v1/notify/update` |
| `POST /admin/v1/basic/notify/delete` | `POST /v1/notify/deleteByIds` |
| `POST /admin/v1/marketing/coupon/insert` | `POST /v1/coupon/insert` |
| `POST /admin/v1/marketing/coupon/update` | `POST /v1/coupon/update` |
| `POST /admin/v1/marketing/coupon/delete` | `POST /v1/coupon/deleteByIds` |
| `POST /admin/v1/marketing/seckill/insert` | `POST /v1/seckill/insert` |
| `POST /admin/v1/marketing/seckill/update` | `POST /v1/seckill/update` |
| `POST /admin/v1/marketing/seckill/delete` | `POST /v1/seckill/deleteByIds` |
| `POST /admin/v1/order/update` | `POST /v1/order/update` |
| `POST /admin/v1/order/delete` | `POST /v1/order/deleteByIds` |
| `POST /admin/v1/order/return/approve` | `POST /v1/return/approve` |
| `POST /admin/v1/order/return/reject` | `POST /v1/return/reject` |
| `POST /admin/v1/auth/testLogin` | `POST /v1/auth/web/user/login` |
| `POST /admin/v1/auth/login` | `POST /v1/auth/web/user/login` |
| `POST /admin/v1/auth/logout` | `POST /v1/auth/web/user/logout` |

#### ✅ 保留在 BFF 的纯读查询（无须迁移）

这些是单纯的查询透传，但因为**前端只认 BFF 地址**，保留在 BFF 更简单：

| 路径 | 说明 |
|------|------|
| `POST /admin/v1/user/page` | 用户分页 |
| `GET /admin/v1/user/findByIds` | 用户批量查询 |
| `GET /admin/v1/user/findByPhone` | 手机号查用户 |
| `POST /admin/v1/system/role/page` | 角色分页 |
| `GET /admin/v1/system/role/all` | 角色列表 |
| `GET /admin/v1/system/menu/tree` | 菜单树 |
| `POST /admin/v1/system/menu/list` | 菜单列表 |
| `POST /admin/v1/system/dept/page` | 部门分页 |
| `GET /admin/v1/system/dept/tree` | 部门树 |
| `POST /admin/v1/system/job/page` | 岗位分页 |
| `GET /admin/v1/system/job/all` | 岗位列表 |
| `POST /admin/v1/product/page` | 商品分页 |
| `GET /admin/v1/product/detail` | 商品详情 |
| `POST /admin/v1/product-mgr/category/tree` | 分类树 |
| `GET /admin/v1/auth/getCode` | 验证码 |
| `GET /admin/v1/auth/userDetail` | 用户详情 |
| `GET /admin/v1/auth/onlineUsers` | 在线用户 |
| `POST /admin/v1/order/page` | 订单分页 |
| `POST /admin/v1/order/return/page` | 退货分页 |
| `GET /admin/v1/order/return/detail` | 退货详情 |
| `POST /admin/v1/marketing/coupon/page` | 优惠券分页 |
| `POST /admin/v1/marketing/seckill/page` | 秒杀分页 |
| `POST /admin/v1/shopping/productComment/page` | 评价分页 |
| `POST /admin/v1/basic/photo/page` | 图片分页 |
| `POST /admin/v1/basic/photoGroup/page` | 图片分组分页 |
| `POST /admin/v1/basic/sensitiveWord/page` | 敏感词分页 |
| `POST /admin/v1/basic/notify/page` | 通知分页 |

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
| 总接口数 | ~280 |
| BFF 保留（读聚合） | ~16 |
| BFF 保留（纯读查询透传） | ~25 |
| 需要迁移（BFF 写透传 → 微服务） | ~80 |
| 微服务 `/v1/internal/` Feign 接口 | ~20 |

### 7.2 关键约束

1. **BFF 不包含任何写操作逻辑**
2. **微服务公开接口（`/v1/`）统一返回 `ApiResult<T>`**（Controller 手动包或依赖注解）
3. **微服务内部接口（`/v1/internal/`）返回裸 DTO**
4. **GlobalApiResultHandler 只在 BFF 路径自动包装**
5. **前端的写请求通过 Gateway 直通微服务，不经过 BFF**

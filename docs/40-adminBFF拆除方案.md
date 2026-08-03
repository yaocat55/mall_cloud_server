# 40-adminBFF拆除方案：admin 后台前端直连后端

> 基于 34-BFF接口规范、35-BFF写透传迁移计划的最终演进
> 日期：2026-08-03

---

## 一、背景与决策

### 1.1 现状问题

`mall-admin-bff` 的存在对管理后台来说是**多余的**。核实后确认：

| 事实 | 证据 |
|------|------|
| BFF 绝大部分接口是**读透传** | `AdminSystemController` 的 `/admin/v1/system/role/page` 直接 `roleFeignClient.searchByPage(c)`，纯转发无聚合 |
| 真正的聚合逻辑只有 3 个 | `dashboard/stats`（聚合4个Feign）、`auth/login`（登录聚合）、`common/image/upload`（文件上传） |
| mall-admin 已经是身份管理模块 | 持有 JWT 密钥 + `JwtTokenFilter`（黑名单检查）+ `SpringSecurityConfig`（RBAC）+ 用户/角色/菜单/部门/岗位 CRUD |
| mall-admin 的公开接口已就绪 | `WebUserController` 已暴露 `/v1/auth/web/user/**` |
| 响应包装已就绪 | `GlobalApiResultHandler` 已覆盖 `/v1/**` 自动包 `ApiResult` |
| 前端直连的网关路由已存在 | `mall-gateway-dev.yaml` 已有 `/api/basic/**`、`/api/product/**` 等 `StripPrefix=2` 直连路由 |

### 1.2 结论

**adminBFF 应该完全拆除**。理由：
1. admin 侧全是 CRUD，没有跨服务聚合需求（聚合价值在 mobile-bff 的 `home/index` 那种）
2. 每次请求多一跳（前端 → Gateway → BFF → Feign → 微服务）
3. 每个 CRUD 跨 3 层，改字段动 3 处，维护成本高
4. 黑名单检查在 BFF 和 mall-admin **各执行一次**（重复）

### 1.3 收益

```
拆除前：前端 → Gateway → admin-bff (读透传 + 重复黑名单检查) → Feign → mall-admin
拆除后：前端 → Gateway → mall-admin（一步到位，黑名单只查一次）
```

---

## 二、目标架构

```
┌────────┐    ┌───────────┐    ┌──────────────────────────────┐
│ 前端    │ →  │ Gateway   │ →  │ mall-admin（身份管理 + 权限） │
│        │    │ /api/admin │    │  /v1/auth/web/user/**       │
│ 登录/   │    │            │    │  /v1/internal/auth/**       │
│ 用户/   │    │            │    │  JwtTokenFilter(黑名单)      │
│ 角色/   │    │            │    │  SpringSecurityConfig(RBAC) │
│ 菜单/   │    │            │    └──────────────────────────────┘
└────────┘    └───────────┘
```

- mall-admin 直接对外暴露身份管理接口
- 黑名单检查只在 mall-admin 一处
- RBAC 权限控制保留在 mall-admin 的 `SpringSecurityConfig`
- 删除 `mall-admin-bff` 模块

---

## 三、接口映射总表（已调研确认）

### 3.1 结论：业务模块读接口几乎全部已存在

逐一核实了 admin-bff 每个 controller 的 Feign 调用对应的业务模块公开接口，**除了 order 订单分页外全部已存在**：

| BFF 域 | BFF Controller | 对应服务 | 公开接口 | 状态 |
|--------|---------------|---------|---------|------|
| auth（登录/用户/菜单/在线） | `AdminAuthController` | mall-admin | `/v1/auth/web/user/**` | ✅ 已有 |
| system（角色/菜单/部门/岗位） | `AdminSystemController` | mall-admin | `/v1/auth/{role,menu,dept,job}/**` | ✅ 已有 |
| user（用户分页） | `AdminUserController` | mall-admin | `/v1/auth/user/**` | ✅ 已有 |
| product（商品） | `AdminProductController` | mall-product | `/v1/product/**` | ✅ 已有 |
| product-mgr（分类/品牌/单位） | `AdminProductManagerController` | mall-product | `/v1/{category,brand,unit}/**` | ✅ 已有 |
| product-extra（属性/属性值/分组） | `AdminProductExtraController` | mall-product | `/v1/{attribute,attributeValue,productGroup,...}/**` | ✅ 已有 |
| inventory（库存） | `AdminInventoryController` | mall-inventory | `/v1/inventory/**` | ✅ 已有 |
| basic（图片/敏感词/分组/作业） | `AdminBasicController` | mall-basic | `/v1/{commonPhoto,commonPhotoGroup,commonSensitiveWord,commonJob}/**` | ✅ 已有 |
| notify（通知） | `AdminBasicController` | mall-message | `/v1/message/notify/**` | ✅ 已有 |
| marketing（优惠券/秒杀） | `AdminMarketingController` | mall-marketing | `/v1/{coupon,couponUserProvide,couponUserReceive,seckillProduct}/**` | ✅ 已有 |
| shopping/comment（评价） | `AdminShoppingController` | mall-product | `/v1/productComment/**` | ✅ 已有 |
| upload（文件上传） | `AdminCommonController` | mall-basic | `/v1/...`（UploadController） | ✅ 已有 |
| **order（订单分页）** | `AdminOrderController` | mall-order | 仅 `/v1/internal/order/searchByPage` | ⚠️ **缺公开版** |
| **order（退货分页）** | `AdminOrderController` | mall-order | 仅 `/v1/internal/return/searchByPage` | ⚠️ **缺公开版** |
| dashboard（统计） | `AdminDashboardController` | 跨 4 服务 | — | ⚠️ 见第七节 |

### 3.2 唯一缺口：order 管理端分页公开接口

`AdminOrderController` 的三个接口对应的 order 模块内部方法全在 `/v1/internal/**`（Feign 专用）：

| BFF 接口 | 对应内部方法 | 缺公开版原因 |
|---------|-------------|-------------|
| `GET /admin/v1/order/page` | `OrderInternalController.searchByPage`（`/v1/internal/order/searchByPage`） | `/v1/internal/**` 前端不可直连 |
| `POST /admin/v1/order/return/page` | `ReturnInternalController.searchByPage`（`/v1/internal/return/searchByPage`） | 同上 |
| `GET /admin/v1/order/return/detail` | `ReturnInternalController.findDetailById`（`/v1/internal/return/findDetailById`） | 同上 |

**解决**：order 模块新增一个管理端公开控制器 `OrderAdminController`（`/v1/admin/order/**`），复用现有 service，仅转发。与已有的 `OrderStatisticsController`（`/v1/admin/trade/statistics`）风格一致。

### 3.3 mall-admin 系统管理公开接口（无需新增）

mall-admin 的 `auth` 包下已有公开控制器，且方法齐全：

| 控制器 | 路径 | 关键方法 |
|--------|------|---------|
| `UserController` | `/v1/auth/user` | `searchByPage` / `findByIds` / `findByPhone` / `todayCount` |
| `RoleController` | `/v1/auth/role` | `searchByPage` / `all` |
| `MenuController` | `/v1/auth/menu` | `getMenuTree` / `searchByPage` / `getMenu` |
| `DeptController` | `/v1/auth/dept` | `searchByPage` / `searchByTree` |
| `JobController` | `/v1/auth/job` | `searchByPage` / `all` |

**这些已能满足前端系统管理需求，无需新增 Web 控制器。**

> ⚠️ 之前方案的「新增 WebRoleController 等」结论作废——调研后发现 `auth` 包下的公开控制器已存在且完整。

---

## 四、实施步骤

### Phase 0：接口映射调研（✅ 已完成）

逐模块核实了 admin-bff 全部接口对应的业务模块公开接口，结论见第三节映射总表：**除 order 订单分页外全部已存在**。前端改 URL 即可直连。

### Phase 1：order 新增管理端公开控制器（唯一后端改动）

order 模块新增 `OrderAdminController`（`/v1/admin/order/**`），复用现有 service 转发：

| 公开路径 | 转发到 | 说明 |
|---------|--------|------|
| `POST /v1/admin/order/searchByPage` | `OrderService.searchByPage` | 复用 `OrderInternalController.searchByPage` 逻辑 |
| `POST /v1/admin/order/return/searchByPage` | `ReturnService.searchByPage` | 复用 `ReturnInternalController` 逻辑 |
| `GET /v1/admin/order/return/findDetailById` | `ReturnService.findDetailById` | 复用 `ReturnInternalController` 逻辑 |

放在 `mall-order/.../controller/admin/` 包，与 `OrderStatisticsController`（`/v1/admin/trade`）风格一致。

> 为什么不能直接暴露 `/v1/internal/**`：`GlobalApiResultHandler` 对 internal 不包装（返回裸 DTO），且 `SpringSecurityConfig` 对 internal permitAll。前端需要 `ApiResult` 包装 + 认证，必须走公开 `/v1/**` 路径。

### Phase 2：网关路由调整

`D:\nacos-config\mall-cloud\mall-gateway-dev.yaml` 中**已经存在** `mall-admin-api` 直连路由：

```yaml
- id: mall-admin-api
  uri: lb://mall-admin-api
  predicates:
  - Path=/api/admin-api/**
  filters:
  - StripPrefix=2
```

前端直连 mall-admin 走 `/api/admin-api/v1/**` 即可，无需新增路由。

需要处理的路由：
1. 删除 `admin-bff` 路由（`Path=/admin/**`，无 StripPrefix）
2. 删除 `mall-admin-bff-old` 路由（`Path=/api/admin/**` + StripPrefix=1）
3. `noAuth` 白名单更新：原 `/api/admin/v1/...` 路径改为 `/api/admin-api/v1/...`

### Phase 3：删除 admin-bff

1. 从父 pom 移除 `mall-admin-bff` 模块引用
2. 删除 `mall-admin-bff/` 目录（源码 + 配置 + 测试）
3. 从 `docs/34-BFF接口规范.md` 等文档移除 admin-bff 相关描述（可选）

### Phase 4：前端 URL 调整

前端将 baseURL 从 `/api/admin/v1/` 调整到各服务的实际路径。**关键：`/api/<service>/` 前缀 + `StripPrefix=2` 剥掉后落到 `/v1/...`。**

| 原 BFF 前缀 | 新前缀 | 落到 mall 服务 | 说明 |
|------------|--------|--------------|------|
| `/api/admin/v1/auth/**` | `/api/admin-api/v1/auth/web/user/**` | mall-admin | 登录/登出/用户信息/菜单/在线 |
| `/api/admin/v1/system/role/**` | `/api/admin-api/v1/auth/role/**` | mall-admin | 角色分页/全部 |
| `/api/admin/v1/system/menu/**` | `/api/admin-api/v1/auth/menu/**` | mall-admin | 菜单树/列表 |
| `/api/admin/v1/system/dept/**` | `/api/admin-api/v1/auth/dept/**` | mall-admin | 部门分页/树 |
| `/api/admin/v1/system/job/**` | `/api/admin-api/v1/auth/job/**` | mall-admin | 岗位分页/全部 |
| `/api/admin/v1/user/**` | `/api/admin-api/v1/auth/user/**` | mall-admin | 用户分页/查询 |
| `/api/admin/v1/product/**` | `/api/product/v1/product/**` | mall-product | 商品分页/详情 |
| `/api/admin/v1/product-mgr/category/**` | `/api/product/v1/category/**` | mall-product | 分类分页/树 |
| `/api/admin/v1/product-mgr/brand/**` | `/api/product/v1/brand/**` | mall-product | 品牌分页 |
| `/api/admin/v1/product-mgr/unit/**` | `/api/product/v1/unit/**` | mall-product | 单位分页 |
| `/api/admin/v1/product-extra/attribute/**` | `/api/product/v1/attribute/**` | mall-product | 属性分页 |
| `/api/admin/v1/product-extra/attributeValue/**` | `/api/product/v1/attributeValue/**` | mall-product | 属性值分页 |
| `/api/admin/v1/product-extra/productGroup/**` | `/api/product/v1/productGroup/**` | mall-product | 商品分组分页 |
| `/api/admin/v1/product-extra/indexNotice/**` | `/api/product/v1/indexNotice/**` | mall-product | 公告分页 |
| `/api/admin/v1/product-extra/indexProduct/**` | `/api/product/v1/indexProduct/**` | mall-product | 推荐商品分页 |
| `/api/admin/v1/inventory/**` | `/api/inventory/v1/inventory/**` | mall-inventory | 库存 CRUD/批次/流水 |
| `/api/admin/v1/basic/photo/**` | `/api/basic/v1/commonPhoto/**` | mall-basic | 图片分页 |
| `/api/admin/v1/basic/photoGroup/**` | `/api/basic/v1/commonPhotoGroup/**` | mall-basic | 图片分组分页 |
| `/api/admin/v1/basic/sensitiveWord/**` | `/api/basic/v1/commonSensitiveWord/**` | mall-basic | 敏感词分页/校验 |
| `/api/admin/v1/marketing/coupon/**` | `/api/marketing/v1/coupon/**` | mall-marketing | 优惠券分页 |
| `/api/admin/v1/marketing/seckill/**` | `/api/marketing/v1/seckillProduct/**` | mall-marketing | 秒杀分页/详情 |
| `/api/admin/v1/shopping/productComment/**` | `/api/product/v1/productComment/**` | mall-product | 评价分页/详情 |
| `/api/admin/v1/order/**` | `/api/order/v1/admin/order/**` | mall-order | 订单分页/退货（新增控制器） |
| `/api/admin/v1/common/image/upload` | `/api/basic/v1/image/upload` | mall-basic | 文件上传 |
| `/api/admin/v1/dashboard/**` | **前端自行聚合**（见第五节） | — | — |

> ⚠️ 注意路径段差异：BFF 的扁平路径（如 `/admin/v1/system/role/page`）对应的是 mall 服务的真实路径（`/v1/auth/role/searchByPage`）。前端不能只改前缀，要按上表改完整路径。

---

## 五、dashboard 处理：前端自行聚合

`dashboard/stats` 原由 BFF 聚合用户数、订单数、销售额、销量排行。拆除 BFF 后，**不做后端聚合，前端分别调用各服务的统计接口自行组装**：

| 统计卡片 | 数据来源 | 接口 |
|---------|---------|------|
| 用户数 / 今日新增 | mall-admin | `GET /api/admin-api/v1/auth/user/todayCount` + 分页 |
| 订单数 / 销售额 / 各状态数量 | mall-order | `POST /api/order/v1/admin/trade/statistics` |
| 商品总数 | mall-product | `POST /api/product/v1/product/page`（pageSize=1 取 total） |
| 销量排行 Top 5 | mall-product | `POST /api/product/v1/product/topSales`（或前端自行实现） |
| 最近订单 | mall-order | `POST /api/order/v1/admin/order/page` |

`AdminCommonController.uploadImage`（文件上传）归属 mall-basic（上传本身就在 basic），前端直连 `/api/basic/v1/image/upload`。

`auth/login` 直接复用 `WebUserController.login`（已有）。

---

## 六、风险与应对

| 风险 | 应对 |
|------|------|
| 业务模块 `/v1/**` 公开接口不存在 | Phase 0 先核实；若不存在则新增 Web 控制器 |
| 响应格式不一致 | `GlobalApiResultHandler` 已覆盖 `/v1/**`，自动包 ApiResult |
| 认证失效 | mall-admin 的 `JwtTokenFilter` + `SpringSecurityConfig` 已就绪，前端带 JWT 即可 |
| 前端改动量大 | 前端 baseURL 是统一配置，改一处即可；BFF 保留过渡期 |
| 黑名单重复检查 | 拆 BFF 后自然消除，只查一次 |
| 权限控制缺失 | RBAC 仍在 mall-admin `SpringSecurityConfig`，前端直连后权限校验路径不变 |

---

## 七、验收标准

- [ ] 前端登录/用户信息/菜单/角色/部门/岗位 CRUD 全部可用
- [ ] 黑名单检查只发生一次（去掉 BFF 后）
- [ ] 文件上传可用（归属 mall-basic）
- [ ] `mall-admin-bff` 模块删除
- [ ] 网关无 `lb://mall-admin-bff` 引用
- [ ] Swagger 文档更新（前端只看 mall-admin + 各业务模块的 Swagger）

---

## 八、与现有文档的关系

| 文档 | 关系 |
|------|------|
| `34-BFF接口规范.md` | 本方案是其最终演进：不再需要 BFF 层 |
| `35-BFF写透传迁移计划.md` | 已完成的写透传迁移是拆除的先行步骤 |
| `26-权限目录清单.md` | 需要更新：权限路径从 `/admin/v1/**` 改为各服务 `/v1/**` |
| `27-鉴权架构与信任链路.md` | 需要更新：信任链去掉 BFF 一跳 |

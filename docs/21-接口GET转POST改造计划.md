# 接口分类与 GET→POST 改造报告

> 扫描范围：全项目 12 个模块，共 633 个接口
> 扫描日期：2026-07-07

---

## 一、统计概览

| 模块 | 接口总数 | GET | POST | GET→POST 改造数 |
|------|:-------:|:---:|:----:|:--------------:|
| mall-admin | 114 | 28 | 86 | **28** |
| mall-admin-bff | 190 | 17 | 173 | **17** |
| mall-basic | 52 | 12 | 40 | **12** |
| mall-customer | 5 | 1 | 4 | **1** |
| mall-marketing | 19 | 5 | 14 | **5** |
| mall-message | 5 | 2 | 3 | **2** |
| mall-mobile-bff | 53 | 19 | 34 | **19** |
| mall-order | 23 | 10 | 13 | **10** |
| mall-pay | 3 | 0 | 3 | **0** |
| mall-product | 115 | 25 | 90 | **25** |
| mall-recommend | 4 | 3 | 1 | **3** |
| **总计** | **583** | **122** | **461** | **122** |

---

## 二、GET 接口分类详表

### 2.1 Admin BFF（需改 17 个）

```
模块路径: /admin/v1/auth
  GET getCode        → POST   # 获取图形验证码（无参）
  GET userInfo       → POST   # 获取用户信息
  GET userDetail     → POST   # 获取用户详情
  GET menus          → POST   # 获取菜单树
  GET onlineUsers    → POST   # 在线用户列表
  GET dashboard/stats → POST  # 仪表盘统计

模块路径: /admin/v1/marketing
  GET seckill/detail?id=X → POST  # 秒杀商品详情

模块路径: /admin/v1/order
  GET deliveryAddress/detail?id=X → POST  # 收货地址详情
  GET return/detail?code=X → POST  # 退货详情

模块路径: /admin/v1/product
  GET /{id}/edit-data → POST   # 商品编辑数据（路径变量）

模块路径: /admin/v1/product-extra
  GET indexNotice/detail?id=X → POST  # 公告详情

模块路径: /admin/v1/shopping
  GET deliveryAddress/detail → POST  # 收货地址详情
  GET productComment/detail  → POST  # 评价详情
  GET productFavorites/detail → POST # 收藏详情
  GET shoppingCart/detail    → POST  # 购物车详情

模块路径: /admin/v1/system
  GET role/all         → POST  # 角色列表
  GET menu/tree        → POST  # 菜单树
  GET job/all          → POST  # 岗位列表

模块路径: /admin/v1/user
  GET findByIds?ids=X → POST  # 批量用户查询
  GET findByPhone?phone=X → POST  # 手机号查用户
  GET deliveryAddress/list → POST  # 地址列表
  GET deliveryAddress/detail → POST  # 地址详情
  GET /{id}/edit-data → POST  # 用户编辑数据
```

### 2.2 Mobile BFF（需改 19 个）

```
模块路径: /mobile/v1/auth
  GET getCode?uuid=X → POST  # 图形验证码

模块路径: /mobile/v1/coupon
  GET user/list        → POST  # 用户优惠券列表
  GET obtainable       → POST  # 可领取优惠券列表

模块路径: /mobile/v1/home
  GET index            → POST  # 首页
  GET carousel         → POST  # 轮播图
  GET notices          → POST  # 公告列表
  GET notice/detail    → POST  # 公告详情
  GET products         → POST  # 推荐商品

模块路径: /mobile/v1/order
  GET detail/{code}    → POST  # 订单详情（路径变量）
  GET count            → POST  # 订单数量统计

模块路径: /mobile/v1/product
  GET /{productId}/detail → POST  # 商品详情（路径变量）
  GET detail           → POST  # 商品详情(参数)
  GET category         → POST  # 商品分类

模块路径: /mobile/v1/user
  GET profile          → POST  # 用户档案
  GET detail           → POST  # 用户详情
  GET address/list     → POST  # 地址列表
  GET address/detail   → POST  # 地址详情
  GET area             → POST  # 地区数据
```

### 2.3 Admin 原生（需改 28 个）

```
模块路径: /v1/dept
  GET findById?id=X → POST  # 部门详情

模块路径: /v1/job
  GET findById?id=X → POST  # 岗位详情

模块路径: /v1/internal/job
  GET all → POST  # 岗位列表（Feign）

模块路径: /v1/menu
  GET findById?id=X   → POST  # 菜单详情
  GET getMenuTree    → POST  # 菜单树
  GET getChild       → POST  # 子菜单

模块路径: /v1/internal/role
  GET all → POST  # 角色列表（Feign）

模块路径: /v1/role
  GET findById?id=X → POST  # 角色详情
  GET all → POST  # 角色列表

模块路径: /v1/roleDept / roleMenu / userRole / userAvatar
  GET findById → POST  # 各模块详情

模块路径: /v1/user
  GET findById → POST  # 用户详情

模块路径: /v1/internal/user
  GET findByPhone → POST  # Feign 调用

模块路径: /v1/web/user
  GET getCode          → POST  # 验证码
  GET getUserDetail    → POST  # 用户详情
  GET info             → POST  # 用户信息
  GET onlineUsers      → POST  # 在线用户列表

模块路径: /v1/test
  GET testOpenFeign → POST  # 测试接口

模块路径: /v1/mobile/deliveryAddress
  GET getUserDeliveryAddressList → POST  # 地址列表
  GET getDetail → POST  # 地址详情
```

### 2.4 Basic / Customer / Marketing / Order / Product / Message / Recommend

（同 pattern，不再全列，详见上方完整列表）

---

## 三、改造方案

### 3.1 改造规则

所有 GET 统一改为 POST，规则如下：

**规则 1：** 路径变量 `{id}` → 改为 `@RequestBody` DTO 中的 `id` 字段

```java
// 改前
@GetMapping("/{id}/edit-data")
public DataDTO getEditData(@PathVariable Long id) { ... }

// 改后
@PostMapping("/edit-data")
public DataDTO getEditData(@RequestBody IdDTO dto) {
    return service.getEditData(dto.getId());
}
```

**规则 2：** `@RequestParam` → `@RequestBody` DTO

```java
// 改前
@GetMapping("/detail")
public DataDTO getDetail(@RequestParam Long id) { ... }

// 改后
@PostMapping("/detail")
public DataDTO getDetail(@RequestBody IdDTO dto) { ... }
```

**规则 3：** 无参 GET（如 `/getCode`）→ 空 DTO 或 `@RequestBody(required = false)`

```java
// 改前
@GetMapping("/getCode")
public CaptchaDTO getCode() { ... }

// 改后
@PostMapping("/getCode")
public CaptchaDTO getCode(@RequestBody(required = false) EmptyDTO dto) { ... }
```

### 3.2 工作量估算

- **DTO 层：** 新增约 20 个 `IdDTO`、`PageDTO` 等通用请求 DTO（已有 `RequestPageEntity` 可用）
- **Controller 层：** 改 122 个接口的注解 + 参数签名（每个改 1-3 行）
- **Swagger 层：** 自动受益——所有参数变为 `@RequestBody` 后，Swagger 直接展示带 example 的 JSON

### 3.3 无需改动的 POST（已符合要求）

| 类别 | 数量 | 示例 |
|------|:----:|------|
| CRUD 分页/search | ~120 | `POST /searchByPage` |
| CRUD 增/改/删 | ~150 | `POST /insert`, `/update`, `/deleteByIds` |
| 订单/支付提交 | ~30 | `POST /submit`, `/doPay` |
| Feign 内部 POST | ~50 | `POST /v1/internal/*` |
| 文件/短信/推送 | ~20 | `POST /image/upload`, `/sms/send` |

---

## 四、建议策略

**推荐分批改造，按优先级：**

```
第一批：BFF 层（admin-bff + mobile-bff） — 36 个
  影响最直接，前端调的就是 BFF

第二批：admin 原生 — 28 个
  后端 CRUD，改造后 Swagger 马上好看

第三批：product / basic / marketing / order — 58 个
  批量 sed 处理，pattern 统一
```

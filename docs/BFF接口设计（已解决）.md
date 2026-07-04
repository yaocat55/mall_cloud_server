# BFF 聚合 API 设计方案

> 目标：前端只需看两个文档（mall-admin-api、mall-mobile-api）就能写出完整应用。

---

## 一、分析结论

### 移动端（uni-app）— 53 个页面 → 需要聚合的只有 5 处

| 聚合场景 | 当前调用数 | 聚合后 | 页面 | 优先级 |
|---------|:--------:|:-----:|------|:-----:|
| **首页** | 4（轮播图+分类导航+公告+推荐商品） | 1 | `index.vue` | 🔴 |
| **商品详情** | 3（商品信息+评论+收藏状态） | 1 | `product-detail.vue` | 🔴 |
| **用户中心** | 3（用户详情+订单统计+推荐） | 1 | `user.vue` | 🟡 |
| **结算预览** | 3（确认订单+地址+优惠券） | 1 | `create-order.vue` | 🟡 |
| **地区选择** | 3（省→市→区三级级联） | 1 | `edit-address.vue` | 🟢 |

其余页面：
- **~20 个页面**是纯 CRUD 管理或静态页面，直接走 ForwardController 透传
- **~15 个页面**只有 1 个 API 调用，无需聚合
- **~13 个页面**纯 Mock 数据，没有真实 API 调用

### 管理后台（Vue Web）— 92 个视图 → 需要聚合的只有 2 处

| 聚合场景 | 当前调用数 | 聚合后 | 页面 | 优先级 |
|---------|:--------:|:-----:|------|:-----:|
| **登录** | 3（验证码+登录+获取用户信息+菜单树） | 1 | `login.vue` + `store/modules/user.js` | 🔴 |
| **仪表盘** | 0（目前纯 UI 无真实数据） | 1 | `home.vue` | 🟢 |

其余页面：**58 个有 API 调用的视图**全部是标准 CRUD 模式（searchByPage / insert / update / deleteByIds），走 ForwardController 透传即可，不需要聚合。

---

## 二、聚合接口详细设计

### 🔴 Mobile: 商品详情聚合

**现状：** `product-detail.vue` 打开时调 3 个接口：
```
GET  /product/v1/mobile/product/getDetail?productId=123
POST /product/v1/mobile/product/searchProductComment  {productId, type, pageNo:1, pageSize:10}
GET  /recommend/v1/mobile/recommend/favoriteStatus?productId=123
```

**设计：**
```
GET /mobile/v1/product/{productId}/detail
```
返回体：
```json
{
  "product": {
    "id": 123,
    "name": "商品名称",
    "price": 99.00,
    "coverUrl": "https://...",
    "swiper": ["https://..."],
    "detail": "<html>...",
    "stock": 100,
    "saleCount": 999,
    "attributeValues": ["红色", "M码"]
  },
  "comments": {
    "list": [{ "content": "好评", "score": 5, "userName": "xxx", "createTime": "2024-01-01" }],
    "totalCount": 10
  },
  "isFavorited": false
}
```

### 🔴 Mobile: 首页聚合（增强）

**现状：** `index.vue` 的 `created()` 调 4 个接口：
```
GET /product/v1/mobile/index/getIndexCarouselImageList
GET /product/v1/mobile/category/getCategoryByParentId?parentId=0
GET /product/v1/mobile/index/getIndexNoticeList
GET /product/v1/mobile/index/getIndexProductList?type=1
```

**设计：** 之前只做了三合一，漏了 `getCategoryByParentId`。增强 `/mobile/v1/home/index` 再加上分类导航：

```json
{
  "carouselList": [{ "id": 1, "imageUrl": "...", "linkUrl": "..." }],
  "categoryList": [{ "id": 1, "name": "手机数码", "iconUrl": "..." }],
  "noticeList": [{ "id": 1, "title": "..." }],
  "productList": [{ "id": 1, "name": "...", "price": 99.00 }]
}
```

### 🟡 Mobile: 用户中心聚合

**现状：** `user.vue` 的 `onShow()` 调 3 个接口：
```
GET  /auth/v1/web/user/getUserDetail
GET  /order/v1/mobile/trade/getUserOrderTradeCount
GET  /recommend/v1/mobile/recommend/recommendProduct
```

**设计：**
```
GET /mobile/v1/user/profile
```
返回体：
```json
{
  "user": {
    "id": 1,
    "nickName": "小明",
    "avatarUrl": "https://...",
    "phone": "138****8000"
  },
  "orderCounts": {
    "pendingPay": 2,
    "pendingShip": 1,
    "pendingReceive": 0,
    "pendingEvaluate": 3
  },
  "recommendProducts": [
    { "id": 10, "name": "推荐商品", "price": 49.00, "coverUrl": "https://..." }
  ]
}
```

### 🟡 Mobile: 结算预览聚合

**现状：** `create-order.vue` 打开时涉及：
```
POST /order/v1/mobile/trade/confirm  { items }
GET  /auth/v1/mobile/deliveryAddress/getUserDeliveryAddressList
GET  /marketing/v1/coupon/getObtainableCouponList
```

**设计：**
```
POST /mobile/v1/checkout/preview  { "items": [{ "productId": 1, "quantity": 1 }] }
```
返回体：
```json
{
  "orderItems": [{ "productId": 1, "name": "xxx", "price": 99.00, "quantity": 1, "coverUrl": "..." }],
  "totalAmount": 99.00,
  "payAmount": 89.00,
  "addresses": [{ "id": 1, "receiverName": "张三", "phone": "138****8000", "address": "..." }],
  "availableCoupons": [{ "id": 1, "name": "满100减10", "offMoney": 10 }]
}
```

### 🟢 Mobile: 地区三级级联

**现状：** `edit-address.vue` 打开地区选择器时级联调 3 次：
```
GET /mobile/v1/area/getAreaByParentId?parentId=0
GET /mobile/v1/area/getAreaByParentId?parentId={provinceId}
GET /mobile/v1/area/getAreaByParentId?parentId={cityId}
```

**设计：** /mobile/v1/area/getAreaByParentId 保持原样即可，因为级联交互需要异步加载。但 BFF 应该透传这个接口，不需要聚合。前端已经按需调用了。

### 🔴 Admin: 登录聚合

**现状：** 登录流程涉及 3 步：
```
POST /auth/v1/web/user/login  { username, password, code, uuid }
GET  /auth/v1/web/user/info   （成功后立即调用）
GET  /auth/v1/menu/getMenuTree（获取菜单树）
```

**设计：**
```
POST /admin/v1/auth/login  { username, password, code, uuid }
```
返回体在现有 TokenDTO 基础上增强：
```json
{
  "token": "eyJ...",
  "user": {
    "id": 1,
    "userName": "admin",
    "nickName": "管理员",
    "avatarUrl": "https://...",
    "deptName": "技术部"
  },
  "menus": [
    { "id": 1, "name": "系统管理", "path": "/system", "children": [...] }
  ]
}
```

### 🟢 Admin: 仪表盘聚合

当前 `home.vue` 的仪表盘没有任何真实数据调用（纯 ECharts 空壳）。设计上聚合各服务统计数据：

```
GET /admin/v1/dashboard/stats
```
返回体：
```json
{
  "userCount": 128,
  "productCount": 1024,
  "orderCount": 256,
  "todayOrderCount": 12,
  "todayRevenue": 1234.56
}
```

---

## 三、透传接口（ForwardController）

所有 CRUD 操作通过 ForwardController 转发到后端，URL 统一为 `/api/{service}/{path}`：

| 路径前缀 | 转发 | 说明 |
|----------|------|------|
| `/{prefix}` | `{prefix}-api` | prefix 为 auth/basic/product/marketing/order/pay/recommend/message |

前端开发者只需知道这个规则，不用知道具体后端地址。BFF 是唯一入口。

---

## 四、实施计划

| 优先级 | 聚合端点 | BFF | 工作量 |
|:-----:|----------|:---:|:-----:|
| 🔴 | `GET /admin/v1/auth/login` 增强（返回 user+menus） | admin | 2h |
| 🔴 | `POST /mobile/v1/product/{id}/detail`（商品详情+评论+收藏） | mobile | 2h |
| 🔴 | `GET /mobile/v1/home/index` 增强（加 categoryList） | mobile | 0.5h |
| 🟡 | `GET /mobile/v1/user/profile`（用户中心三合一） | mobile | 1.5h |
| 🟡 | `POST /mobile/v1/checkout/preview`（结算预览） | mobile | 2h |
| 🟢 | `GET /admin/v1/dashboard/stats`（仪表盘） | admin | 1h |
| 🟢 | `GET /mobile/v1/user/favorites`（收藏列表） | mobile | 0.5h |
| 🟢 | `GET /mobile/v1/user/footprint`（浏览记录） | mobile | 0.5h |
| 🟢 | `GET /mobile/v1/home/recommend`（推荐商品） | mobile | 0.5h |

**总计：** ~10h 工作量

---

## 五、注意事项

1. **商品详情页的评论是分页的**，第一次只加载第一页，滚动加载更多。所以聚合只返回第一页，后续分页单独走 `/mobile/v1/product/comment/page`
2. **保存地址时的三级地区选择**是异步级联的，不适合一次聚合。保持逐级查询即可
3. **首页的 WebSocket 连接**是独立通道，不参与聚合，保持原样
4. **登录成功后获取菜单树**是管理后台特有的需求，移动端不需要

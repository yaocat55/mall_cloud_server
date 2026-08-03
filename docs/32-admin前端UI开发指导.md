# Admin 前端开发 UI 指导

## 说明

本文档给前端开发工程师看，指导 admin 后台管理系统的前端页面开发。

**接口基准：** 前端按各微服务 Swagger 对接（mall-admin 是 `/api/admin-api/v3/api-docs`，其他微服务是各自服务的 `/v3/api-docs`）。
**后端基准：** 所有接口返回 `ApiResult<T>` 格式：`{"code": 200, "msg": "成功", "data": {...}}`
**基建：** Vue 3 + Element Plus + Axios，用哪个 UI 库你定，这里只列页面结构和业务组件需求。

---

## 路由总览

```
/login                        → 登录页
/layout                       → 主布局（侧边栏 + 顶栏 + 内容区）
  /dashboard                  → 工作台
  /system/users               → 用户管理
  /products                   → 商品列表
  /products/categories        → 分类管理
  /products/brands            → 品牌管理
  /products/units             → 单位管理
  /products/attributes        → 属性管理
  /products/groups            → 商品分组
  /products/photos            → 商品图片
  /products/index             → 首页管理
  /orders                     → 订单列表
  /orders/returns             → 退货审核
  /marketing/coupons          → 优惠券管理
  /marketing/seckills         → 秒杀管理
  /marketing/provides         → 发放记录
  /marketing/receives         → 领取记录
  /basic/photos               → 图片库
  /basic/photo-groups         → 图片分组
  /basic/sensitive            → 敏感词管理
  /basic/notices              → 通知管理
  /comments                   → 评价管理
```

**侧边栏菜单由后端返回**（`GET /api/admin-api/v1/auth/web/user/menus`），前端根据返回的路由树动态渲染。

---

## 通用规则

### 列表页标准模式

每个列表页基本长一个样：

```
┌─────────────────────────────────────────────┐
│  搜索条件（可折叠）                          │
│  ┌─────┐ ┌─────┐ ┌──────┐ ┌────────┐       │
│  │ 输入框│ │下拉框│ │日期选择│ │搜索/重置│   │
│  └─────┘ └─────┘ └──────┘ └────────┘       │
├─────────────────────────────────────────────┤
│  ┌─────┐ ┌─────┐                 右下角显示：│
│  │ 新增 │ │ 删除 │           共 N 条 / 每页  │
│  └─────┘ └─────┘                            │
├─────────────────────────────────────────────┤
│  ┌─────┬────────────────┬──────┬──────┬──┐  │
│  │  #  │  字段1         │ 字段2│状态  │操作│  │
│  ├─────┼────────────────┼──────┼──────┼──┤  │
│  │  1  │  xxx           │  xxx │ 启用 │编辑│  │
│  │  2  │  xxx           │  xxx │ 禁用 │删除│  │
│  └─────┴────────────────┴──────┴──────┴──┘  │
├─────────────────────────────────────────────┤
│              [分页组件]                       │
└─────────────────────────────────────────────┘
```

### 分页传参

所有 `/searchByPage` / `/page` 接口统一传：

```json
{
  "pageNo": 1,
  "pageSize": 10
}
```

（筛选条件字段直接平铺在 JSON 顶层，和 `pageNo`/`pageSize` 同级。）

返回：

```json
{
  "list": [...],
  "totalCount": 100,
  "totalPage": 10,
  "pageNo": 1,
  "pageSize": 10
}
```

### 表单弹窗

新增/编辑统一用 **Dialog 弹窗**，字段根据后端 Swagger 的 `@Schema(description)` 标注渲染。

### 状态字段处理

后端状态字段是数字（`0`/`1`），对应含义看 Swagger 的 `@Schema(allowableValues)`。前端需自行转换成中文显示：

| 类型 | 0 | 1 | 2 | 3 |
|------|---|---|---|---|
| 商品 `status` | 下架 | 上架 | - | - |
| 优惠券/公告/轮播 `useStatus` | 未启用 | 已启用 | - | - |
| 订单 `orderStatus` | 待支付 | 已支付 | 已发货 | 已完成 |
| 退货 `applyStatus` | 待审核 | 已通过 | 已拒绝 |
| 评论 `status` | 待审核 | 显示 | 隐藏 |

---

## 1. 登录页

**路径：** `/login`

**需要实现的组件：**

| 组件 | 说明 |
|------|------|
| 登录表单 | 用户名 + 密码输入框 + 验证码 |
| 验证码图片 | `GET /api/admin-api/v1/auth/web/user/getCode` 返回图片流，点一下刷新 |
| 提交按钮 | `POST /api/admin-api/v1/auth/web/user/login` → 成功就把 token 存本地（localStorage） |

**验证码实现方式：** 后端 `getCode` 接口返回图片 + 把验证码存 Redis，前端显示图片，用户在表单输入验证码提交。

**登录成功后：** 跳 `/dashboard`，同时调 `GET /api/admin-api/v1/auth/web/user/info` 获取当前用户信息（头像、昵称、角色），存全局状态。

**侧边栏加载：** 调 `GET /api/admin-api/v1/auth/web/user/menus` → 前端渲染多级菜单（支持无限嵌套）。

---

## 2. 工作台

**路径：** `/dashboard`

**需要实现的组件：**

| 组件 | API | 说明 |
|------|-----|------|
| 统计卡片（4 个） | 前端自行聚合 | 订单数、商品数、用户数、退款数，分别调用订单/商品/用户/退货接口统计（原 `GET /admin/v1/dashboard/stats` 已删除） |
| 最近订单列表（小型表格） | `POST /api/order/v1/admin/order/page` | 最近几条订单的简略信息 |

**可选美化：** 统计卡片带图标 + 渐变色背景，数据动态加载。

---

## 3. 用户管理

**路径：** `/system/users`

**Swagger 分组：** 系统管理

前端需要调多个接口拼页面：

| 用途 | 接口 | 说明 |
|------|------|------|
| 用户列表 | `POST /api/admin-api/v1/auth/user/searchByPage` | 传 `{pageNo:1, pageSize:10}`，筛选条件平铺顶层 |
| 用户详情 | `GET /api/admin-api/v1/auth/user/findById?id=xxx` | 编辑弹窗时调，返回用户基本信息（原 edit-data 已删除，前端自行聚合） |
| 新增用户 | `POST /api/admin-api/v1/auth/user/insert` | 传用户信息 JSON |
| 修改用户 | `POST /api/admin-api/v1/auth/user/update` | 传用户信息 JSON（带 id） |
| 删除用户 | `POST /api/admin-api/v1/auth/user/deleteByIds` | 传 `[1, 2]` |
| **角色下拉** | `GET /api/admin-api/v1/auth/role/all` | 新增/编辑弹窗时调，用作角色下拉选项 |
| **部门树下拉** | `POST /api/admin-api/v1/auth/dept/searchByTree` | 新增/编辑弹窗时调，用作部门树选择器 |
| **岗位下拉** | `GET /api/admin-api/v1/auth/job/all` | 新增/编辑弹窗时调，用作岗位下拉选项 |

**注意：** 新增/编辑弹窗打开时，一次性调 role/all + dept/tree + job/all 缓存起来就行，不用每次翻页都调。

---

## 4. 商品管理

### 4.1 商品列表

**路径：** `/products`

**Swagger 分组：** 商品管理

| 前端元素 | API | 说明 |
|---------|-----|------|
| 搜索区 | `POST /api/product/v1/product/searchByPage` | 传 `{pageNo, pageSize, name, categoryId, status}` |
| 表格列 | - | 商品名称、分类、价格、库存、销量、上下架状态、创建时间 |
| 状态切换 | `POST /api/product/v1/product/update` | 传 `{id, status: 1/0}` 直接改上下架 |
| 新增商品 | `POST /api/product/v1/product/insert` | 传商品完整信息 |
| 编辑商品 | `GET /api/product/v1/product/findById?id=xxx` → `POST /api/product/v1/product/update` | 先查商品信息，修改后提交（原 edit-data 已删除，前端自行聚合） |
| 商品详情 | `GET /api/product/v1/product/findById?id=xxx` | 查看商品完整信息 |
| 删除 | `POST /api/product/v1/product/deleteByIds` | 传 `[1, 2]` |
| **分类下拉** | `POST /api/product/v1/category/searchByTree` | 商品新增/编辑弹窗时调，用作分类树选择器 |
| **品牌下拉** | `POST /api/product/v1/brand/searchByPage` | 弹窗时调，传 `{pageNo:1, pageSize:999}` |
| **单位下拉** | `POST /api/product/v1/unit/searchByPage` | 同上 |

**注意：** edit-data 已删除，不再聚合分类树/品牌/单位，需要前端自己在弹窗打开时单独调上面 3 个接口。

### 4.2 分类管理

**路径：** `/products/categories`

**后端控制器：** mall-product 的 `CategoryController`（`/api/product/v1/category`）

| 元素 | API | 说明 |
|------|-----|------|
| 分类树 | `POST /api/product/v1/category/searchByTree` | 无限级树形结构，用 `<el-tree>` 渲染 |
| 新增分类 | `POST /api/product/v1/category/insert` | 弹窗：分类名称、排序、上级分类（树选择器）、图标 |
| 编辑分类 | `POST /api/product/v1/category/update` | 同新增，数据回填 |
| 删除分类 | `POST /api/product/v1/category/deleteByIds` | 传 `ids` 数组，有子分类不允许删除 |

### 4.3 品牌管理

**路径：** `/products/brands`

**后端控制器：** mall-product 的 `BrandController`（`/api/product/v1/brand`）

标准列表页，字段：品牌名称、品牌图片、排序、操作

### 4.4 单位管理

**路径：** `/products/units`

**后端控制器：** mall-product 的 `UnitController`（`/api/product/v1/unit`）

标准列表页，字段：单位名称、排序、操作

### 4.5 属性管理

**路径：** `/products/attributes`

**后端控制器：** mall-product 的 `AttributeController` / `AttributeValueController`（`/api/product/v1/attribute` / `/api/product/v1/attributeValue`）

| 元素 | API | 说明 |
|------|-----|------|
| 属性列表 | `POST /api/product/v1/attribute/searchByPage` | 标准列表，字段：属性名称、属性类型（规格/参数）、排序 |
| 属性值管理 | 作为一个**子页面**或**内嵌表格**：`/api/product/v1/attributeValue/searchByPage` | 在属性详情中展开显示属性值列表（属性值名称、排序） |
| 新增属性 | `POST /api/product/v1/attribute/insert` | 弹窗：属性名称、属性类型下拉、排序 |
| 新增属性值 | `POST /api/product/v1/attributeValue/insert` | 注意传 `attributeId` |

**设计建议：** 左侧是属性列表（穿梭框或树形），点击属性后在右侧显示其属性值列表（内嵌表格）。

### 4.6 商品分组

**路径：** `/products/groups`

**后端控制器：** mall-product 的 `ProductGroupController`（`/api/product/v1/productGroup`）

标准列表页，字段：分组名称、排序、状态（启用/禁用）

### 4.7 商品图片

**路径：** `/products/photos`

**后端控制器：** mall-product 的 `ProductPhotoController`（`/api/product/v1/productPhoto`）

| 元素 | API | 说明 |
|------|-----|------|
| 图片列表 | `POST /api/product/v1/productPhoto/searchByPage` | 字段：图片缩略图、图片地址、所属商品、排序、创建时间 |
| 新增图片 | `POST /api/product/v1/productPhoto/insert` | 需要带商品 ID + 图片地址（URL 由图片库上传后获得） |
| 图片预览 | 直接显示缩略图 | 点击可放大预览 |

### 4.8 首页管理

**路径：** `/products/index`

**后端控制器：** mall-product 的 `IndexCarouselImageController` / `IndexNoticeController` / `IndexProductController`（`/api/product/v1/...`）

分三个 Tab：

| Tab | API | 说明 |
|-----|-----|------|
| 轮播图 | `POST /api/product/v1/indexCarouselImage/searchByPage` | 字段：图片（缩略图）、标题、排序、状态（启用/禁用） |
| 公告管理 | `POST /api/product/v1/indexNotice/searchByPage` | 字段：公告标题、内容（Textarea）、排序、状态 |
| 推荐商品 | `POST /api/product/v1/indexProduct/searchByPage` | 字段：商品名称（只读显示）、排序、状态 |

---

## 5. 订单管理

### 5.1 订单列表

**路径：** `/orders`

**后端控制器：** mall-order 的 `OrderAdminController`（`/api/order/v1/admin/order`）

| 元素 | API | 说明 |
|------|-----|------|
| 搜索区 | `POST /api/order/v1/admin/order/page` | 搜索字段：订单号、用户手机号、订单状态（下拉）、日期范围 |
| 表格列 | - | 订单号、用户、商品数量、总金额、实付金额、订单状态（中文 tag）、支付方式、下单时间、操作 |
| 订单详情 | 展开行（分页数据已含详情） | 显示完整的商品清单 + 金额明细 + 收货地址 + 物流信息 |
| 编辑/删除 | 不支持 | admin 后台订单只读，修改/删除属于 C 端下单流程 |

**注意事项：** 订单不是全部都在同一个数据库表，**不支持**从 admin 修改商品数量和价格。

### 5.2 退货审核

**路径：** `/orders/returns`

**后端控制器：** mall-order 的 `OrderAdminController` / `OrderReturnApprovalController`

| 元素 | API | 说明 |
|------|-----|------|
| 退货列表 | `POST /api/order/v1/admin/order/return/page` | 搜索字段：退货单号、申请状态（待审核/已通过/已拒绝）、日期 |
| 退货详情 | `GET /api/order/v1/admin/order/return/detail?id=xxx` | 退货商品、退款金额、申请原因、凭证图片、申请人 |
| 审核操作 | `POST /api/order/v1/trade/return/approve` / `POST /api/order/v1/trade/return/reject` | 通过不需要填原因，拒绝需要填写拒绝原因 |

**设计建议：** 点击"审核"打开详情弹窗，弹窗底部显示"通过"/"拒绝"按钮，拒绝时弹出输入框。

---

## 6. 营销管理

### 6.1 优惠券管理

**路径：** `/marketing/coupons`

**后端控制器：** mall-marketing 的 `CouponController`（`/api/marketing/v1/coupon`）

标准列表页 + 弹窗形式。

| 元素 | API | 说明 |
|------|-----|------|
| 列表 | `POST /api/marketing/v1/coupon/searchByPage` | 字段：优惠券名称、类型（满减/折扣/现金）、面值、门槛、发行量、已领取、状态 |
| 新增/编辑 | `POST /api/marketing/v1/coupon/insert` / `update` | 表单：名称、类型下拉、面值、使用门槛、有效期（日期范围）、发行总量 |
| 删除 | `POST /api/marketing/v1/coupon/deleteByIds` | 传 `ids` |

### 6.2 秒杀管理

**路径：** `/marketing/seckills`

标准列表页。字段：活动名称、秒杀商品、秒杀价、库存、活动时间、状态

### 6.3 发放记录 & 领取记录

**路径：** `/marketing/provides` / `/marketing/receives`

纯列表页，没有编辑操作。只需要表格展示和搜索。

| 记录类型 | 需要显示的列 |
|---------|------------|
| 发放记录 | 优惠券名称、发放数量、接收用户（或全部）、发放时间、操作人 |
| 领取记录 | 优惠券名称、用户、领取时间、使用状态（已使用/未使用） |

---

## 7. 基础数据

### 7.1 图片库

**路径：** `/basic/photos`

**后端控制器：** mall-basic 的 `CommonPhotoController` / `CommonPhotoGroupController`（`/api/basic/v1/commonPhoto` / `/api/basic/v1/commonPhotoGroup`）

| 元素 | API | 说明 |
|------|-----|------|
| 图片列表 | `POST /api/basic/v1/commonPhoto/searchByPage` | 卡片模式展示，而不是表格！字段：缩略图、分组、图片地址、大小、上传时间 |
| 上传图片 | `POST /api/basic/v1/image/upload` | 上传组件：支持拖拽 + 点击上传，后端返回图片 URL |
| 删除图片 | `POST /api/basic/v1/commonPhoto/deleteByIds` | 传 `ids` |
| 分组筛选 | `POST /api/basic/v1/commonPhotoGroup/searchByPage` | 先加载分组列表做下拉筛选 |

**设计建议：** 图片库用**卡片网格**展示（类似 File Manager），不是传统表格。点击图片可放大预览，右键/悬停出现编辑/删除操作。

### 7.2 图片分组

**路径：** `/basic/photo-groups`

标准列表页，字段：分组名称、排序、操作（图库按分组筛选时需要这个数据）。

### 7.3 敏感词管理

**路径：** `/basic/sensitive`

**后端控制器：** mall-basic 的 `CommonSensitiveWordController`（`/api/basic/v1/commonSensitiveWord`）

| 元素 | API | 说明 |
|------|-----|------|
| 敏感词列表 | `POST /api/basic/v1/commonSensitiveWord/searchByPage` | 标准列表，字段：敏感词、类型（政治/广告/色情等）、状态、创建时间 |
| 新增/编辑 | `POST /api/basic/v1/commonSensitiveWord/insert` / `update` | 弹窗：敏感词输入、类型下拉、状态开关 |
| 删除 | `POST /api/basic/v1/commonSensitiveWord/deleteByIds` | 传 `ids` |
| 敏感词校验（**可选**） | `POST /api/basic/v1/commonSensitiveWord/checkSensitiveWord` | 传文本内容，后端返回是否包含敏感词 |

### 7.4 通知管理

**路径：** `/basic/notices`

**后端控制器：** mall-message 的 `MessageNotifyController`（`/api/message/v1/message/notify`）

| 元素 | API | 说明 |
|------|-----|------|
| 通知列表 | `POST /api/message/v1/message/notify/searchByPage` | 字段：标题、推送范围（全部/指定用户）、状态、创建时间 |
| 新增通知 | `POST /api/message/v1/message/notify/insert` | 表单：标题 + 内容（富文本/Textarea） |
| 推送操作 | `POST /api/message/v1/message/notify/push/all`（推送给全部用户）/ `POST /api/message/v1/message/notify/push/user`（推送给指定用户） | 推送给指定用户时需要传用户 ID 列表。注意推送不是创建，创建通知后需要手动点"推送"按钮才发送 |

---

## 8. 评价管理

**路径：** `/comments`

**后端控制器：** mall-product 的 `ProductCommentController`（`/api/product/v1/productComment`）

| 元素 | API | 说明 |
|------|-----|------|
| 评价列表 | `POST /api/product/v1/productComment/searchByPage` | 字段：商品名称、用户、评分（1-5 星）、内容、状态、评价时间 |
| 详情 | `GET /api/product/v1/productComment/findById?id=xxx` | 点击查看完整评价内容 + 图片 |
| 审核/隐藏 | `POST /api/product/v1/productComment/update` | 修改 `status` 字段：待审核→显示、显示→隐藏 |
| 回复 | `POST /api/product/v1/productComment/insert` | 需要传 `parentId` 关联原评价 |
| 删除 | `POST /api/product/v1/productComment/deleteByIds` | 传 `ids` |

**设计建议：** 评价列表里直接显示星级（用 Element Plus 的 `<el-rate>` 只读模式），状态用 tag 显示。

---

## 9. 前端 Mock

如果后端接口还没全部跑通，建议前端用 **Mock.js** 或者 **[Apifox](https://apifox.com)** / **[Swagger Editor](https://editor.swagger.io)** 根据 Swagger 文档模拟数据。

Swagger 地址：`http://{后端地址}:{端口}/v3/api-docs`（各微服务独立，mall-admin 为 `/api/admin-api/v3/api-docs`）

---

## 10. 组件复用建议

| 组件 | 复用场景 |
|------|---------|
| `<SearchForm>` | 所有列表页的搜索区（可折叠、搜索/重置按钮） |
| `<CrudTable>` | 几乎所有页面（支持多选、分页、操作列插槽） |
| `<FormDialog>` | 新增/编辑弹窗（标题、确认/取消按钮、表单验证） |
| `<StatusTag>` | 状态字段显示（根据枚举值渲染不同颜色的 tag） |
| `<ImageUpload>` | 图片库上传、品牌图标、轮播图等 |
| `<TreeSelect>` | 分类选择、部门选择、上级菜单选择 |
| `<Pagination>` | 统一分页组件（如果 Element Plus 自带的不够用） |
| `<ConfirmDelete>` | 删除确认弹窗（"确认删除 XX？"） |

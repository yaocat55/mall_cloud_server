# Admin 前端功能规划

## 说明

本文档定义 admin 后台的菜单结构与功能范围，基于当前 admin-bff 提供的接口设计。
菜单权限由超级管理员在 RBAC 系统中配置，不同角色看到不同的菜单。

## 菜单树

```
系统管理（仅超级管理员可见）
├── 用户管理      → /admin/v1/user/*
├── 角色管理      → /admin/v1/system/role/*
├── 菜单管理      → /admin/v1/system/menu/*
├── 部门管理      → /admin/v1/system/dept/*
└── 岗位管理      → /admin/v1/system/job/*

商品管理（运营可见）
├── 商品列表      → /admin/v1/product/*
├── 分类管理      → /admin/v1/product-mgr/category/*
├── 品牌管理      → /admin/v1/product-mgr/brand/*
├── 单位管理      → /admin/v1/product-mgr/unit/*
├── 属性管理      → /admin/v1/product-extra/attribute/*
├── 属性值管理    → /admin/v1/product-extra/attributeValue/*
├── 商品分组      → /admin/v1/product-extra/productGroup/*
└── 商品图片      → /admin/v1/product-extra/productPhoto/*

订单管理（客服可见）
├── 订单列表      → /admin/v1/order/page
├── 配送地址      → /admin/v1/order/deliveryAddress/*
└── 退货审核      → /admin/v1/order/return/*

营销管理（运营可见）
├── 优惠券管理    → /admin/v1/marketing/coupon/*
├── 秒杀管理      → /admin/v1/marketing/seckill/*
├── 发放记录      → /admin/v1/marketing/couponUserProvide/*
└── 领取记录      → /admin/v1/marketing/couponUserReceive/*

首页管理（运营可见）
├── 轮播图        → /admin/v1/product-extra/indexCarouselImage/*
├── 公告管理      → /admin/v1/product-extra/indexNotice/*
└── 推荐商品      → /admin/v1/product-extra/indexProduct/*

基础数据（运营可见）
├── 行政区域      → /admin/v1/common/area/*
├── 图片管理      → /admin/v1/basic/photo/*
├── 图片分组      → /admin/v1/basic/photoGroup/*
├── 敏感词管理    → /admin/v1/basic/sensitiveWord/*
├── 短信记录      → /admin/v1/basic/smsRecord/*
└── 公告通知      → /admin/v1/basic/notify/*

商品评价（运营可见）
└── 评价管理      → /admin/v1/shopping/productComment/*

数据统计（运营可见）
└── 仪表盘        → /admin/v1/dashboard/stats

认证相关（所有人可见，不属于菜单树）
├── 登录          → /admin/v1/auth/login
├── 退出          → /admin/v1/auth/logout
├── 当前用户信息  → /admin/v1/auth/userInfo
├── 菜单树        → /admin/v1/auth/menus
└── 验证码        → /admin/v1/auth/getCode
```

## 各角色可见菜单

### 超级管理员

```
系统管理
商品管理
订单管理
营销管理
首页管理
基础数据
商品评价
数据统计
```

### 运营

```
商品管理
营销管理
首页管理
基础数据
商品评价
数据统计
```

### 客服

```
订单管理
商品评价
订单金额查看（只读）
```

### 财务

```
订单管理（只读：金额/退款）
订单金额查看（只读）
```

## 前端路由设计建议

```
/login                  登录页
/dashboard              仪表盘

/system/users           用户管理
/system/roles           角色管理
/system/menus           菜单管理
/system/depts           部门管理
/system/jobs            岗位管理

/products               商品列表
/products/categories    分类管理
/products/brands        品牌管理
/products/units         单位管理
/products/attributes    属性管理
/products/groups        商品分组

/orders                 订单列表
/orders/returns         退货审核

/marketing/coupons      优惠券管理
/marketing/seckills     秒杀管理

/home/carousel          轮播图
/home/notices           公告管理
/home/recommends        推荐商品

/basic/areas            行政区域
/basic/photos           图片管理
/basic/sensitive        敏感词管理
/basic/sms              短信记录
/basic/notices          通知管理

/comments               商品评价
```

## 未定事项

- 是否需要在商品编辑页实现聚合接口（品牌/单位下拉选项当前为 TODO）
- 订单详情页是否需要聚合商品信息、物流轨迹
- 数据统计仪表盘具体展示哪些指标

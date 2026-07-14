# Admin 前端功能规划

## 说明

本文档定义 admin 后台的菜单结构与功能范围，基于当前 admin-bff 提供的接口设计。
菜单权限由超级管理员在 RBAC 系统中配置，不同角色看到不同的菜单。

> 注意：**菜单管理**（新增/编辑路由路径）和**字典管理**（key-value 配置）不在前端展示，
> 这些由开发通过数据库或配置文件维护，超级管理员不需要在界面上操作。


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
```

### 财务

```
订单管理（只读：金额/退款）
```

## 前端路由设计建议

```
/login                  登录页
/dashboard              仪表盘

/system/users           用户管理
/system/roles           角色管理
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

## 不展示的功能（由开发维护）

| 功能 | 原因 |
|:----|:------|
| 菜单管理 | 路由路径 + 权限标识由开发在代码里预设 |
| 字典管理 | key-value 技术常量，运营不需要理解 |
| 定时任务 | 技术运维，非运营职责 |

## 未定事项

- 是否需要在商品编辑页实现聚合接口（品牌/单位下拉选项当前为 TODO）
- 订单详情页是否需要聚合商品信息、物流轨迹
- 数据统计仪表盘具体展示哪些指标


## 超级管理员完整菜单（全部 106 项）

```
[目录] 系统管理 → /system
  [菜单] 用户管理 → /system/user
    [按钮] 用户列表 [user:list] [hidden]
    [按钮] 新增用户 [user:add] [hidden]
    [按钮] 编辑用户 [user:edit] [hidden]
    [按钮] 删除用户 [user:del] [hidden]
  [菜单] 角色管理 → /system/role
    [按钮] 角色列表 [role:list] [hidden]
    [按钮] 新增角色 [role:add] [hidden]
    [按钮] 编辑角色 [role:edit] [hidden]
    [按钮] 删除角色 [role:del] [hidden]
  [菜单] 菜单管理 → /system/menu [内部]
    [按钮] 菜单列表 [menu:list] [hidden]
    [按钮] 新增菜单 [menu:add] [hidden]
    [按钮] 编辑菜单 [menu:edit] [hidden]
    [按钮] 删除菜单 [menu:del] [hidden]
  [菜单] 部门管理 → /system/dept
    [按钮] 部门列表 [dept:list] [hidden]
    [按钮] 新增部门 [dept:add] [hidden]
    [按钮] 编辑部门 [dept:edit] [hidden]
    [按钮] 删除部门 [dept:del] [hidden]
  [菜单] 岗位管理 → /system/job
    [按钮] 岗位列表 [job:list] [hidden]
    [按钮] 新增岗位 [job:add] [hidden]
    [按钮] 编辑岗位 [job:edit] [hidden]
    [按钮] 删除岗位 [job:del] [hidden]
[目录] 基础数据 → /basic
  [菜单] 字典管理 → /basic/dict [内部]
    [按钮] 字典列表 [dict:list] [hidden]
    [按钮] 新增字典 [dict:add] [hidden]
    [按钮] 编辑字典 [dict:edit] [hidden]
    [按钮] 删除字典 [dict:del] [hidden]
  [菜单] 图片管理 → /basic/image
    [按钮] 图片列表 [image:list] [hidden]
    [按钮] 上传图片 [image:add] [hidden]
    [按钮] 删除图片 [image:del] [hidden]
  [菜单] 敏感词管理 → /basic/sensitive
    [按钮] 敏感词列表 [sensitive:list] [hidden]
    [按钮] 新增敏感词 [sensitive:add] [hidden]
    [按钮] 编辑敏感词 [sensitive:edit] [hidden]
    [按钮] 删除敏感词 [sensitive:del] [hidden]
  [菜单] 短信记录 → /basic/sms
    [按钮] 短信列表 [sms:list] [hidden]
  [菜单] 定时任务 → /basic/job [内部]
    [按钮] 任务列表 [task:list] [hidden]
    [按钮] 新增任务 [task:add] [hidden]
    [按钮] 编辑任务 [task:edit] [hidden]
    [按钮] 删除任务 [task:del] [hidden]
[目录] 商品管理 → /product
  [菜单] 商品列表 → /product/list
    [按钮] 商品查询 [product:list] [hidden]
    [按钮] 新增商品 [product:add] [hidden]
    [按钮] 编辑商品 [product:edit] [hidden]
    [按钮] 删除商品 [product:del] [hidden]
  [菜单] 商品分类 → /product/category
    [按钮] 分类列表 [category:list] [hidden]
    [按钮] 新增分类 [category:add] [hidden]
    [按钮] 编辑分类 [category:edit] [hidden]
    [按钮] 删除分类 [category:del] [hidden]
  [菜单] 品牌管理 → /product/brand
    [按钮] 品牌列表 [brand:list] [hidden]
    [按钮] 新增品牌 [brand:add] [hidden]
    [按钮] 编辑品牌 [brand:edit] [hidden]
    [按钮] 删除品牌 [brand:del] [hidden]
  [菜单] 属性管理 → /product/attr
    [按钮] 属性列表 [attr:list] [hidden]
    [按钮] 新增属性 [attr:add] [hidden]
    [按钮] 编辑属性 [attr:edit] [hidden]
    [按钮] 删除属性 [attr:del] [hidden]
  [菜单] 单位管理 → /product/unit
    [按钮] 单位列表 [unit:list] [hidden]
    [按钮] 新增单位 [unit:add] [hidden]
    [按钮] 编辑单位 [unit:edit] [hidden]
    [按钮] 删除单位 [unit:del] [hidden]
[目录] 首页内容 → /home
  [菜单] 轮播图管理 → /home/banner
    [按钮] 轮播图列表 [banner:list] [hidden]
    [按钮] 新增轮播图 [banner:add] [hidden]
    [按钮] 编辑轮播图 [banner:edit] [hidden]
    [按钮] 删除轮播图 [banner:del] [hidden]
  [菜单] 公告管理 → /home/notice
    [按钮] 公告列表 [notice:list] [hidden]
    [按钮] 新增公告 [notice:add] [hidden]
    [按钮] 编辑公告 [notice:edit] [hidden]
    [按钮] 删除公告 [notice:del] [hidden]
  [菜单] 推荐商品 → /home/recommend
    [按钮] 推荐列表 [recommend:list] [hidden]
    [按钮] 新增推荐 [recommend:add] [hidden]
    [按钮] 编辑推荐 [recommend:edit] [hidden]
    [按钮] 删除推荐 [recommend:del] [hidden]
[目录] 营销管理 → /marketing
  [菜单] 优惠券管理 → /marketing/coupon
    [按钮] 优惠券列表 [coupon:list] [hidden]
    [按钮] 新增优惠券 [coupon:add] [hidden]
    [按钮] 编辑优惠券 [coupon:edit] [hidden]
    [按钮] 删除优惠券 [coupon:del] [hidden]
  [菜单] 秒杀管理 → /marketing/seckill
    [按钮] 秒杀列表 [seckill:list] [hidden]
    [按钮] 新增秒杀 [seckill:add] [hidden]
    [按钮] 编辑秒杀 [seckill:edit] [hidden]
    [按钮] 删除秒杀 [seckill:del] [hidden]
[目录] 订单管理 → /order
  [菜单] 订单列表 → /order/list
    [按钮] 订单查询 [order:list] [hidden]
    [按钮] 订单详情 [order:detail] [hidden]
    [按钮] 编辑订单 [order:edit] [hidden]
```

共 106 项，其中标 [内部] 的由开发维护，不在前端展示。另外 79 个 [hidden] 按钮不在树中显示。

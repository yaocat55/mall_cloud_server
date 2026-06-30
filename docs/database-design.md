# 数据库设计

## 部署拓扑

| 微服务 | 数据库 | 分片策略 | 说明 |
|--------|--------|---------|------|
| `mall-gateway` | 无 | — | 纯网关，无数据库 |
| `mall-auth` | `mall_auth` | 单库 | 认证鉴权、RBAC、收货地址 |
| `mall-basic` | `mall_basic` | 单库 | 字典、行政区域、图片、任务调度 |
| `mall-product` | `mall_product` | 单库 | 商品中心、分类品牌、首页管理、购物车 |
| `mall-marketing` | `mall_marketing` | 单库 | 优惠券、秒杀 |
| `mall-order` | `mall_trade_0~7` | 8 库 × N 表 | 订单、订单项、收货地址 |
| `mall-pay` | 无 | — | 纯支付 API 对接 |
| `mall-recommend` | `mall_recommend_0~7` | 8 库 × 16/64 表 | 收藏、浏览历史 |
| `mall-message` | `mall_message_0~7` | 8 库 × 64 表 | 站内通知 |

## 各服务对应的表

### mall-auth（库：`mall_auth`）

| 表名 | 说明 |
|------|------|
| `auth_user` | 系统用户 |
| `auth_user_role` | 用户-角色关联 |
| `auth_user_avatar` | 用户头像 |
| `auth_role` | 角色 |
| `auth_role_menu` | 角色-菜单关联 |
| `auth_role_dept` | 角色-部门关联 |
| `auth_menu` | 菜单权限 |
| `auth_dept` | 部门 |
| `auth_job` | 岗位 |
| `delivery_address` | 用户收货地址 |

### mall-basic（库：`mall_basic`）

| 表名 | 说明 |
|------|------|
| `common_dict` | 字典 |
| `common_dict_detail` | 字典详情 |
| `common_area` | 行政区域 |
| `common_photo` | 图片资源 |
| `common_photo_group` | 图片分组 |
| `common_job` | Quartz 定时任务 |
| `common_job_log` | 任务执行日志 |
| `common_sms_record` | 短信发送记录 |
| `common_sensitive_word` | 敏感词库 |
| `common_task` | 异步任务 |

### mall-product（库：`mall_product`）

| 表名 | 说明 |
|------|------|
| `product` | 商品 |
| `product_detail` | 商品详情（富文本） |
| `product_photo` | 商品图片 |
| `product_comment` | 商品评价 |
| `product_comment_photo` | 评价图片 |
| `product_attribute` | 商品-属性关联 |
| `product_group` | 商品分组 |
| `product_group_attribute` | 分组-属性关联 |
| `category` | 分类 |
| `brand` | 品牌 |
| `attribute` | 属性 |
| `attribute_value` | 属性值 |
| `unit` | 单位 |
| `shopping_cart` | 购物车 |
| `product_favorites` | 商品收藏（写操作） |
| `product_view_record` | 浏览记录（写操作） |
| `mall_index_carousel_image` | 首页轮播图 |
| `mall_index_notice` | 首页公告 |
| `mall_index_product` | 首页推荐商品 |

> `product_favorites` 和 `product_view_record` 由 mall-product 写入，mall-recommend 通过 RocketMQ 异步消费后同步到分库分表做查询。

### mall-marketing（库：`mall_marketing`）

| 表名 | 说明 |
|------|------|
| `coupon` | 优惠券定义 |
| `coupon_user_receive` | 用户领券记录 |
| `coupon_user_provide` | 用户发券记录 |
| `seckill_product` | 秒杀商品 |

### mall-order（库：`mall_trade_0~7`，ShardingSphere 分库分表）

| 逻辑表名 | 物理分片 | 说明 |
|----------|---------|------|
| `t_order` | 8 库 × 32 表 | 订单主表 |
| `t_order_item` | 8 库 × 256 表 | 订单明细 |
| `t_order_delivery_address` | 8 库 × 32 表 | 订单收货地址 |
| `t_order_return_apply` | 每库 4 表 | 退货申请 |
| `t_order_return_item` | 每库 8 表 | 退货明细 |
| `t_order_return_voucher` | 每库 16 表 | 退货凭证 |

### mall-recommend（库：`mall_recommend_0~7`，ShardingSphere 分库分表）

| 逻辑表名 | 物理分片 | 分片键 | 说明 |
|----------|---------|--------|------|
| `product_favorites` | 8 库 × 16 表 | `user_id` | 用户收藏 |
| `product_view_record` | 8 库 × 64 表 | `user_id` | 浏览历史 |

### mall-message（库：`mall_message_0~7`，ShardingSphere 分库分表）

| 逻辑表名 | 物理分片 | 分片键 | 说明 |
|----------|---------|--------|------|
| `common_notify` | 8 库 × 64 表 | `to_user_id` | 站内通知 |

### mall-pay / mall-gateway

无数据库。

> [!WARNING]
> 分库分表的建表 SQL 位于各服务 `sql/` 目录下：`mall_trade_sharding.sql`（约 408 KB）、`mall_message_sharding.sql`、`mall_recommend_sharding.sql`。**务必通过脚本批量初始化，严禁逐表手动创建**。

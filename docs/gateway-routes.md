# 网关路由与白名单

## 路由表

| 路径前缀 | 转发目标 | 备注 |
|----------|----------|------|
| `/api/admin/**` | `lb://mall-admin-api` | 【BFF】管理后台聚合（8090） |
| `/api/mobile/**` | `lb://mall-mobile-api` | 【BFF】移动端聚合（8091） |
| `/api/basic/**` | `lb://mall-basic-api` | 基础服务 |
| `/api/auth/**` | `lb://mall-auth-api` | 认证服务 |
| `/api/product/**` | `lb://mall-product-api` | 商品服务 |
| `/api/marketing/**` | `lb://mall-marketing-api` | 营销服务 |
| `/api/order/**` | `lb://mall-order-api` | 订单服务 |
| `/api/pay/**` | `lb://mall-pay-api` | 支付服务 |
| `/api/recommend/**` | `lb://mall-recommend-api` | 推荐服务 |
| `/api/message/ws**` | `lb:ws://mall-message-api` | WebSocket 连接 |
| `/api/message/**` | `lb://mall-message-api` | 消息服务 |

## JWT 白名单列表

**JWT 白名单（无需 Token）—— 完整列表（共 37 个路径）：**

> 白名单配置在 Nacos `mall-gateway-dev.yaml` → `gateway.filter.noAuth` 中，一行逗号分隔。以下为按服务分类的格式化列表。

```
# mall-auth（认证相关）
/api/auth/v1/web/user/getCode              /api/auth/v1/web/user/login
/api/auth/v1/web/user/loginByPhone         /api/auth/v1/web/user/logout
/api/auth/v1/web/user/info                 /api/auth/v1/web/user/resetPassword
/api/auth/v1/mobile/user/register

# mall-auth（菜单/角色/部门/岗位）
/api/auth/v1/menu/searchByPage             /api/auth/v1/menu/insert
/api/auth/v1/menu/update                   /api/auth/v1/menu/deleteByIds
/api/auth/v1/role/all                      /api/auth/v1/dept/findById
/api/auth/v1/dept/searchByPage             /api/auth/v1/dept/searchByTree
/api/auth/v1/job/searchByPage              /api/auth/v1/job/deleteByIds
/api/auth/v1/user/findByPhone              /api/auth/v1/test/testOpenFeign

# mall-basic（文件/短信/敏感词）
/api/basic/v1/file/upload                  /api/basic/v1/image/upload
/api/basic/v1/commonSmsRecord/findSmsRecord
/api/basic/v1/commonSensitiveWord/checkSensitiveWord

# mall-product（移动端）
/api/product/v1/mobile/product/searchProduct
/api/product/v1/mobile/product/getDetail
/api/product/v1/mobile/product/searchProductComment
/api/product/v1/mobile/index/getIndexCarouselImageList
/api/product/v1/mobile/index/getIndexProductList
/api/product/v1/mobile/index/getIndexNoticeList
/api/product/v1/mobile/index/searchIndexNoticeByPage
/api/product/v1/mobile/index/getIndexNoticeDetail
/api/product/v1/mobile/category/getCategoryByParentId

# mall-product（管理端）
/api/product/v1/category/searchByTree

# mall-pay
/api/pay/v1/mobile/pay/doPay               /api/pay/v1/mobile/pay/createQrCode
```

> [!WARNING]
> 白名单托管在 Nacos `mall-gateway-dev.yaml` 的 `gateway.filter.noAuth` 中。各服务 `@NoLogin` 注解仅做标记，实际拦截在 Gateway 层。**当前 Gateway 行为为"验签但放行"**，身份校验由下游服务自行完成。如要启用 Gateway 层拦截，需确保白名单完整覆盖所有无需登录的接口。

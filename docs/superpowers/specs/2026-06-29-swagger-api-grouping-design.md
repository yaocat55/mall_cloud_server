# Swagger 3 API 三层分组与文档标准化设计

> 日期：2026-06-29
> 状态：已批准

## 1. 目标

将项目各微服务模块的 Swagger 3 (OpenAPI 3) 文档从当前的 mobile/admin 两层分组升级为三层分组
（mobile / admin / internal），并统一 @Tag、@Operation、@Schema 标注规范，清晰区分
前端接口与内部微服务接口，同时文档化微服务间的调用关系。

## 2. 三层分组架构

### 2.1 分组定义

| 分组 | 显示名 | 用途 | 前端项目 |
|---|---|---|---|
| `mobile` | 📱 移动端接口 | 移动端前端（小程序）调用的 API | `susan_mall_cloud_uni` (UniApp) |
| `admin` | ⚙️ 管理后台接口 | 管理后台前端调用的 API | `susan_mall_cloud_web` (Vue) |
| `internal` | 🔗 内部微服务接口 | 仅限微服务间 Feign 调用，不对外暴露 | — |

### 2.2 各模块分组矩阵

| 模块 | mobile | admin | internal | 说明 |
|---|---|---|---|---|
| mall-auth | ✅ | ✅ | ✅ | 权限认证核心服务 |
| mall-basic | ✅ | ✅ | ✅ | 字典/文件/短信/地区 |
| mall-product | ✅ | ✅ | ✅ | 商品/分类/品牌/购物车 |
| mall-order | ✅ | — | ✅ | 订单/退货，无管理后台接口 |
| mall-pay | ✅ | — | ✅ | 支付，无管理后台接口 |
| mall-marketing | — | ✅ | ✅ | 优惠券/秒杀，无移动端直接接口 |
| mall-message | — | ✅ | ✅ | 消息推送，无移动端直接接口 |
| mall-recommend | ✅ | ✅ | ✅ | 推荐/浏览历史 |
| mall-admin-api (BFF) | — | ✅ | — | 管理端 BFF 聚合层，不暴露内部接口 |
| mall-mobile-api (BFF) | ✅ | — | — | 移动端 BFF 聚合层，不暴露内部接口 |

## 3. Package 结构规范

### 3.1 标准包结构

每个业务模块的 controller 包按照以下结构组织：

```
controller/
├── mobile/          ← 📱 移动端前端接口（BFF 层 -> 服务层）
│   └── XxxController.java
├── admin/ 或 product/ / auth/ / common/   ← ⚙️ 管理后台接口
│   └── XxxController.java
├── internal/        ← 🔗 【新增】纯内部微服务接口（仅 Feign 调用）
│   └── XxxInternalController.java
└── * 其他子包（如 shopping/）按业务归属
```

### 3.2 接口迁移原则

1. **纯 Feign 调用**（前端不直接请求）：从原 controller 迁移到 `controller/internal/`
2. **纯前端调用**：保持原位
3. **双用途接口**（既被前端调用也被 Feign 调用）：保持原位，通过 internal 组的 `pathsToMatch` 将该路由也包含进 internal 分组

### 3.3 接口迁移清单

#### mall-product

| 当前路径 | 前端也用？ | 迁移目标 |
|---|---|---|
| `/v1/product/findByIds` | ❌ | → `internal/ProductInternalController.java` |
| `/v1/product/findDetailById` | ❌ | → `internal/ProductInternalController.java` |
| `/v1/productPhoto/findByProductIds` | ❌ | → `internal/ProductInternalController.java` |
| `/v1/product/reduceStockBatch` | ❌ | → `internal/ProductInternalController.java` |
| `/v1/product/searchFromES` | ✅ (admin) | 留在 `product/` + internal pathsToMatch |
| `/v1/mobile/product/getDetail` | ✅ (mobile) | 留在 `mobile/` + internal pathsToMatch |
| `/v1/mobile/product/addOrCancelFavorites` | ✅ (mobile) | 留在 `mobile/` + internal pathsToMatch |
| `/v1/mobile/product/searchProductComment` | ✅ (mobile) | 留在 `mobile/` + internal pathsToMatch |
| `/v1/mobile/product/saveProductComment` | ✅ (mobile) | 留在 `mobile/` + internal pathsToMatch |
| `/v1/mobile/product/addProductComments` | ✅ (mobile) | 留在 `mobile/` + internal pathsToMatch |
| `/v1/mobile/category/getCategoryByParentId` | ✅ (mobile) | 留在 `mobile/` + internal pathsToMatch |
| `/v1/shoppingCart/findByIds` | ❌ | → `internal/ShoppingCartInternalController.java` |
| `/v1/shoppingCart/addShoppingCart` | ✅ (mobile) | 留在 `shopping/` + internal pathsToMatch |
| `/v1/shoppingCart/updateShoppingCart` | ✅ (mobile) | 留在 `shopping/` + internal pathsToMatch |
| `/v1/shoppingCart/getShoppingCartProduct` | ❌ | → `internal/ShoppingCartInternalController.java` |
| `/v1/shoppingCart/deleteShoppingCart` | ✅ (mobile) | 留在 `shopping/` + internal pathsToMatch |

#### mall-auth

| 当前路径 | 前端也用？ | 迁移目标 |
|---|---|---|
| `/v1/user/findByIds` | ❌ | → `internal/UserInternalController.java` |
| `/v1/user/findByPhone` | ❌ | → `internal/UserInternalController.java` |
| `/v1/user/updateAvatar` | ❌ | → `internal/UserInternalController.java` |
| `/v1/mobile/user/bindPhone` | ✅ (mobile) | 留在 `mobile/` |
| `/v1/mobile/user/register` | ✅ (mobile) | 留在 `mobile/` |
| `/v1/web/user/info` | ✅ (admin) | 留在 `web/` |
| `/v1/web/user/login` | ✅ (admin) | 留在 `web/` |
| `/v1/web/user/loginByPhone` | ✅ (mobile) | 留在 `web/` |
| DeliveryAddress CRUD | ✅ (mobile) | 留在 `mobile/` |

#### mall-basic

| 当前路径 | 前端也用？ | 迁移目标 |
|---|---|---|
| `/v1/sms/send` | ❌ | → `internal/SmsInternalController.java` |
| `/v1/smsRecord/search` | ❌ | → `internal/SmsInternalController.java` |
| `/v1/dict/getByCode` | ❌ | → `internal/DictInternalController.java` |
| `/v1/area/list` | ❌ | → `internal/AreaInternalController.java` |
| `/v1/photo/*` | ✅ (admin) | 留在 `common/` |
| `/v1/upload/*` | ✅ (admin) | 留在 `upload/` |

#### mall-order

| 当前路径 | 前端也用？ | 迁移目标 |
|---|---|---|
| `/v1/mobile/trade/create` | ✅ (mobile) | 留在 `mobile/` + internal pathsToMatch |
| `/v1/mobile/trade/search` | ✅ (mobile) | 留在 `mobile/` + internal pathsToMatch |
| `/v1/mobile/trade/getDetail/{id}` | ✅ (mobile) | 留在 `mobile/` + internal pathsToMatch |
| `/v1/mobile/trade/getDetailByCode/{code}` | ❌ | → `internal/OrderInternalController.java` |
| `/v1/mobile/trade/confirm` | ✅ (mobile) | 留在 `mobile/` |
| `/v1/mobile/trade/submit` | ✅ (mobile) | 留在 `mobile/` |
| `/v1/mobile/trade/cancel` | ✅ (mobile) | 留在 `mobile/` |
| `/v1/mobile/trade/confirmReceive` | ✅ (mobile) | 留在 `mobile/` |
| `/v1/mobile/trade/evaluate` | ✅ (mobile) | 留在 `mobile/` |
| `/v1/mobile/trade/return/apply` | ✅ (mobile) | 留在 `mobile/` |

#### mall-pay

| 当前路径 | 前端也用？ | 迁移目标 |
|---|---|---|
| `/v1/pay/create` | ✅ (mobile) | 留在 `mobile/` |
| `/v1/pay/notify` | ❌ | → `internal/PayInternalController.java` |
| `/v1/pay/refund` | ❌ | → `internal/PayInternalController.java` |

#### mall-marketing

| 当前路径 | 前端也用？ | 迁移目标 |
|---|---|---|
| `/v1/coupon/getUserCouponList` | ✅ (mobile) | 留在 `controller/` + internal pathsToMatch |
| `/v1/coupon/getObtainableCouponList` | ✅ (mobile) | 留在 `controller/` + internal pathsToMatch |
| `/v1/coupon/calculateOrderPrice` | ❌ | → `internal/CouponInternalController.java` |
| `/v1/coupon/useCoupons` | ❌ | → `internal/CouponInternalController.java` |
| `/v1/coupon/receiveCoupon` | ✅ (mobile) | 留在 `controller/` |
| 秒杀管理 | ✅ (admin) | 留在 `controller/` |

#### mall-recommend

| 当前路径 | 前端也用？ | 迁移目标 |
|---|---|---|
| `/v1/recommend/list` | ✅ (mobile) | 留在 `mobile/` |

#### mall-message

| 当前路径 | 前端也用？ | 迁移目标 |
|---|---|---|
| 通知推送 | ✅ (admin) | 留在 `controller/` |

## 4. SwaggerConfig 配置模板

### 4.1 通用模板

```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI xxxOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("XX服务文档")
                        .description("XX服务功能描述")
                        .version("v0.0.1"));
    }

    // ── 移动端接口 ──
    @Bean
    public GroupedOpenApi xxxMobileApi() {
        return GroupedOpenApi.builder()
                .group("mobile")
                .displayName("📱 移动端接口")
                .packagesToScan("cn.net.mall.xxx.controller.mobile")
                .build();
    }

    // ── 管理后台接口 ──
    @Bean
    public GroupedOpenApi xxxAdminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("⚙️ 管理后台接口")
                .packagesToScan("cn.net.mall.xxx.controller.xxx") // 具体包名
                .build();
    }

    // ── 内部微服务接口 ──
    @Bean
    public GroupedOpenApi xxxInternalApi() {
        return GroupedOpenApi.builder()
                .group("internal")
                .displayName("🔗 内部微服务接口")
                .packagesToScan("cn.net.mall.xxx.controller.internal")
                // 双用途接口路径
                .pathsToMatch("/v1/xxx/yyy", "/v1/zzz/...")
                .build();
    }
}
```

### 4.2 各模块 URL 规范

| 模块 | internal 包路径 | 双用途 pathsToMatch 示例 |
|---|---|---|
| product | `cn.net.mall.product.controller.internal` | `/v1/product/searchFromES`, `/v1/mobile/product/getDetail`, `/v1/mobile/category/getCategoryByParentId`, `/v1/shoppingCart/findByIds`, `/v1/shoppingCart/getShoppingCartProduct`, `/v1/mobile/product/addOrCancelFavorites`, `/v1/mobile/product/searchProductComment`, `/v1/mobile/product/saveProductComment` |
| auth | `cn.net.mall.auth.controller.internal` | `/v1/deliveryAddress/findByIds` |
| basic | `cn.net.mall.basic.controller.internal` | — |
| order | `cn.net.mall.order.controller.internal` | `/v1/mobile/trade/create`, `/v1/mobile/trade/search`, `/v1/mobile/trade/getDetail/{id}`, `/v1/mobile/trade/update` |
| pay | `cn.net.mall.pay.controller.internal` | — |
| marketing | `cn.net.mall.marketing.controller.internal` | `/v1/coupon/getUserCouponList`, `/v1/coupon/getObtainableCouponList` |

### 4.3 不需要分组变更的模块

- **mall-admin-api (BFF)**: 已有 admin 分组，不需要 internal
- **mall-mobile-api (BFF)**: 已有 mobile 分组，不需要 internal

## 5. 标注规范

### 5.1 @Tag 规范

| 分组 | @Tag name 格式 | @Tag description 格式 | 示例 |
|---|---|---|---|
| mobile | `移动端-<功能名>` | `移动端：<功能说明>` | `@Tag(name = "移动端-商品详情", description = "移动端：商品详情、评价、收藏")` |
| admin | `<功能名>` | `管理后台：<功能说明>` | `@Tag(name = "商品管理", description = "管理后台：商品 CRUD 操作")` |
| internal | `内部服务-<功能名>` | `内部微服务：[调用方列表] <功能说明>` | `@Tag(name = "内部服务-商品", description = "内部微服务：order-service、recommend-service 通过 Feign 调用")` |

### 5.2 @Operation 规范

```java
// 前端接口
@Operation(summary = "获取商品详情",
           description = "移动端：查询单个商品完整详情，含规格、价格、评价、SKU 信息")

// 管理后台接口
@Operation(summary = "新增商品",
           description = "管理后台：创建新商品，包含基本信息、规格、图片")

// 内部微服务接口（标注调用方）
@Operation(summary = "批量查询商品信息",
           description = "内部服务：由 order-service（订单服务）通过 Feign 调用，"
                       + "根据ID集合批量获取商品基本信息（不含详情）")
```

### 5.3 @Schema 规范

```java
// DTO 类级 + 字段级全描述
@Schema(description = "商品查询条件，用于搜索商品列表")
public class ProductSearchConditionDTO {

    @Schema(description = "商品名称，支持模糊匹配")
    private String name;

    @Schema(description = "分类ID，精确匹配")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "价格下限，单位：分（如 1000 表示 10.00 元）")
    @Min(value = 0, message = "价格不能为负")
    private Long priceMin;

    @Schema(description = "价格上限，单位：分")
    private Long priceMax;
}
```

### 5.4 优先级规则

如果同一个 DTO 被多个分组共用：
1. 在 DTO 上标注最通用的描述
2. 各 Controller 的方法级 @Operation 描述该场景特有的约束
3. 字段级 @Schema 描述字段的通用含义和格式

## 6. 调用方文档化策略

### 6.1 三层标注

**第一层 — 类级（JavaDoc）：**
```java
/**
 * 内部商品接口
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>order-service（订单服务）— 下单时查询商品信息</li>
 *   <li>recommend-service（推荐服务）— 获取商品数据构建推荐列表</li>
 * </ul>
 * <b>不对外暴露</b>，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-商品", description = "内部微服务：order-service、recommend-service 调用")
```

**第二层 — 方法级（@Operation description）：**
```java
@Operation(summary = "根据ID集合批量查询商品",
           description = "调用方：order-service（下单时查商品信息）、"
                       + "recommend-service（构建推荐列表）。"
                       + "批量查询，返回基本商品信息不含详情。")
```

**第三层 — Feign Client 接口：**
```java
/**
 * 商品服务 Feign 客户端
 * <p>
 * 调用方：mall-order、mall-recommend
 */
@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "productFeignClient")
public interface ProductFeignClient {

    @Operation(summary = "【Feign】批量查询商品基本信息",
               description = "由 mall-order 下单确认时调用，根据ID批量获取商品快照")
    @PostMapping("/v1/internal/product/findByIds")
    List<ProductDTO> findByIds(@RequestBody List<Long> ids);
}
```

### 6.2 调用关系全景

| 接口提供方 | 接口路径 | 调用方（Feign Client 所在模块） |
|---|---|---|
| mall-product | `/v1/internal/product/findByIds` | mall-order, mall-recommend |
| mall-product | `/v1/internal/product/findDetailById` | mall-order |
| mall-product | `/v1/internal/productPhoto/findByProductIds` | mall-order |
| mall-product | `/v1/internal/product/reduceStockBatch` | mall-order |
| mall-product | `/v1/internal/shoppingCart/findByIds` | mall-order |
| mall-product | `/v1/internal/shoppingCart/getShoppingCartProduct` | mall-order |
| mall-auth | `/v1/user/findByIds` | mall-admin-api (BFF) |
| mall-auth | `/v1/user/findByPhone` | mall-admin-api (BFF) |
| mall-auth | `/v1/deliveryAddress/findByIds` | mall-order |
| mall-basic | `/v1/area/list` | mall-order, mall-product, mall-auth |
| mall-basic | `/v1/sms/send` | mall-auth |
| mall-basic | `/v1/dict/getByCode` | 所有模块 |
| mall-order | `/v1/mobile/trade/getDetailByCode` | mall-pay |
| mall-order | `/v1/mobile/trade/getTrade` | mall-pay |
| mall-pay | `/v1/pay/create` | mall-order |
| mall-marketing | `/v1/coupon/calculateOrderPrice` | mall-order |
| mall-marketing | `/v1/coupon/useCoupons` | mall-order |

## 7. 实施步骤

### Phase 1：配置准备（SwaggerConfig）
1. 在每个业务模块的 SwaggerConfig 中新增 `internal` 分组的 `GroupedOpenApi` Bean
2. 更新现有分组的 `displayName` 添加 emoji 前缀

### Phase 2：新建 Internal Controller
3. 在各模块中新建 `controller/internal/` 包
4. 将纯内部接口从原 Controller 迁移至 InternalController
5. 更新 URL 映射路径（可选，保持与原路径一致更安全）

### Phase 3：标注更新
6. 更新所有 Controller 的 `@Tag` 按新规范
7. 更新所有 `@Operation` 按新规范，内部接口标注调用方
8. 更新所有 DTO 加上 `@Schema(description = ...)`

### Phase 4：双用途接口配置
9. 为每个双用途接口在 internal 分组的 `pathsToMatch` 中添加路径

### Phase 5：Feign Client 文档化
10. 在所有 Feign Client 接口上补充类级 JavaDoc 和方法级 `@Operation`

### Phase 6：验证
11. 启动各服务验证 Swagger UI 中分组正确
12. 验证所有 Feign 调用不受接口迁移影响

## 8. 注意事项

1. **接口路径不变原则**：迁移到 internal/ 包后保持 URL 路径不变，避免 Feign 调用断裂
2. **渐进式实施**：每个模块独立实施，互不阻塞
3. **双用途接口的 pathsToMatch 显式列举**：不要用通配符，避免误包含
4. **SpringDoc 版本**：当前使用 springdoc-openapi 2.6.0 + Knife4j，确保配置兼容

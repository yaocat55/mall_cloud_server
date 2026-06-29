package cn.net.mall.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI productOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("商品服务文档")
                        .description("商品、分类、品牌、购物车、首页管理等")
                        .version("v0.0.1"));
    }

    @Bean
    public GroupedOpenApi productMobileApi() {
        return GroupedOpenApi.builder()
                .group("mobile")
                .displayName("📱 移动端接口")
                .packagesToScan("cn.net.mall.product.controller.mobile")
                .build();
    }

    @Bean
    public GroupedOpenApi productAdminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("⚙️ 管理后台接口")
                .packagesToScan("cn.net.mall.product.controller.product",
                                "cn.net.mall.product.controller.shopping")
                .build();
    }

    @Bean
    public GroupedOpenApi productInternalApi() {
        return GroupedOpenApi.builder()
                .group("internal")
                .displayName("🔗 内部微服务接口")
                .packagesToScan("cn.net.mall.product.controller.internal")
                .pathsToMatch(
                    "/v1/product/searchFromES",
                    "/v1/mobile/product/getDetail",
                    "/v1/mobile/category/getCategoryByParentId",
                    "/v1/mobile/product/addOrCancelFavorites",
                    "/v1/mobile/product/searchProductComment",
                    "/v1/mobile/product/saveProductComment",
                    "/v1/mobile/product/addProductComments",
                    "/v1/shoppingCart/addShoppingCart",
                    "/v1/shoppingCart/updateShoppingCart",
                    "/v1/shoppingCart/deleteShoppingCart"
                )
                .build();
    }
}

package cn.net.mall.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI orderOpenAPI() {
        return new OpenAPI().info(new Info().title("订单服务文档").description("订单、退货、分库分表").version("v0.0.1"));
    }
    @Bean
    public GroupedOpenApi orderMobileApi() {
        return GroupedOpenApi.builder().group("mobile").displayName("📱 移动端接口")
                .packagesToScan("cn.net.mall.order.controller.mobile").build();
    }
    @Bean
    public GroupedOpenApi orderInternalApi() {
        return GroupedOpenApi.builder().group("internal").displayName("🔗 内部微服务接口")
                .packagesToScan("cn.net.mall.order.controller.internal")
                .pathsToMatch("/v1/mobile/trade/search", "/v1/mobile/trade/getDetail/{id}",
                              "/v1/mobile/trade/update", "/v1/mobile/trade/getTradeItem")
                .build();
    }
}

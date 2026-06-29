package cn.net.mall.marketing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI marketingOpenAPI() {
        return new OpenAPI().info(new Info().title("营销服务文档").description("优惠券、秒杀").version("v0.0.1"));
    }
    @Bean
    public GroupedOpenApi marketingAdminApi() {
        return GroupedOpenApi.builder().group("admin").displayName("⚙️ 管理后台接口")
                .packagesToScan("cn.net.mall.marketing.controller")
                .packagesToExclude("cn.net.mall.marketing.controller.internal")
                .build();
    }
    @Bean
    public GroupedOpenApi marketingInternalApi() {
        return GroupedOpenApi.builder().group("internal").displayName("🔗 内部微服务接口")
                .packagesToScan("cn.net.mall.marketing.controller.internal").build();
    }
}

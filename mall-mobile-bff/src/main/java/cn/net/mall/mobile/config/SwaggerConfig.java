package cn.net.mall.mobile.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 移动端 BFF Swagger 配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI mobileOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("移动端 BFF 文档")
                        .description("移动端 BFF 聚合接口——前端只需看此文档即可开发")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Token"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Token",
                                new SecurityScheme()
                                        .name("Bearer Token")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi mobileFrontendApi() {
        return GroupedOpenApi.builder()
                .group("mobile-frontend")
                .displayName("📱 移动端前端接口")
                .packagesToScan("cn.net.mall.mobile.controller.mobile")
                .build();
    }
}

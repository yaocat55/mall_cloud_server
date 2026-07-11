package cn.net.mall.admin.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("用户权限服务文档")
                        .description("Swagger3 Spring Boot 3.0 application")
                        .version("v0.0.1"))
                .externalDocs(new ExternalDocumentation()
                        .description("swagger 3 详解")
                        .url("https://springshop.wiki.github.org/docs"));
    }

    @Bean
    public GroupedOpenApi authAdminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("⚙️ 管理后台接口")
                .packagesToScan("cn.net.mall.admin.controller")
                .build();
    }
}

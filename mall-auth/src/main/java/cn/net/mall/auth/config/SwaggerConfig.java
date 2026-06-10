package cn.net.mall.auth.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

/**
 * @date 2024/1/8 下午4:45
 */
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
}

package cn.net.mall.message.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI messageOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("消息推送服务文档")
                        .description("WebSocket 实时推送、站内通知")
                        .version("v0.0.1"));
    }

    @Bean
    public GroupedOpenApi messageAdminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("管理后台接口")
                .packagesToScan("cn.net.mall.message.controller")
                .build();
    }
}

package cn.net.mall.basic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI basicOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("基础服务文档")
                        .description("字典、文件、短信、地区、定时任务等")
                        .version("v0.0.1"));
    }

    @Bean
    public GroupedOpenApi basicMobileApi() {
        return GroupedOpenApi.builder()
                .group("mobile")
                .displayName("📱 移动端接口")
                .packagesToScan("cn.net.mall.basic.controller.mobile")
                .build();
    }

    @Bean
    public GroupedOpenApi basicAdminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("⚙️ 管理后台接口")
                .packagesToScan("cn.net.mall.basic.controller")
                .packagesToExclude("cn.net.mall.basic.controller.mobile",
                                   "cn.net.mall.basic.controller.internal")
                .build();
    }

    @Bean
    public GroupedOpenApi basicInternalApi() {
        return GroupedOpenApi.builder()
                .group("internal")
                .displayName("🔗 内部微服务接口")
                .packagesToScan("cn.net.mall.basic.controller.internal")
                .build();
    }
}

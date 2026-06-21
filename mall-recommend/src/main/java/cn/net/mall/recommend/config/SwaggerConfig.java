package cn.net.mall.recommend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI recommendOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("推荐服务文档")
                        .description("商品推荐、收藏管理、浏览历史")
                        .version("v0.0.1"));
    }

    @Bean
    public GroupedOpenApi recommendMobileApi() {
        return GroupedOpenApi.builder()
                .group("mobile")
                .displayName("移动端接口")
                .packagesToScan("cn.net.mall.recommend.controller.mobile")
                .build();
    }

    @Bean
    public GroupedOpenApi recommendAdminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("管理后台接口")
                .packagesToScan("cn.net.mall.recommend.controller")
                .packagesToExclude("cn.net.mall.recommend.controller.mobile")
                .build();
    }
}

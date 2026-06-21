package cn.net.mall.pay.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI payOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("支付服务文档")
                        .description("支付宝沙箱支付、二维码生成")
                        .version("v0.0.1"));
    }

    @Bean
    public GroupedOpenApi payMobileApi() {
        return GroupedOpenApi.builder()
                .group("mobile")
                .displayName("移动端接口")
                .packagesToScan("cn.net.mall.pay.controller.mobile")
                .build();
    }
}

package cn.net.mall.admin.client.config;

import cn.net.mall.admin.client.fallback.UserFeignFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FallbackFactoryAutoConfiguration {
    @Bean
    public UserFeignFallbackFactory userFeignFallbackFactory() {
        return new UserFeignFallbackFactory();
    }
}

package cn.net.mall.customer.client.config;

import cn.net.mall.customer.client.fallback.MemberFeignFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FallbackFactoryAutoConfiguration {
    @Bean
    public MemberFeignFallbackFactory memberFeignFallbackFactory() {
        return new MemberFeignFallbackFactory();
    }
}

package cn.net.mall.marketing.client.config;

import cn.net.mall.marketing.client.fallback.MarketingFeignFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FallbackFactoryAutoConfiguration {
    @Bean
    public MarketingFeignFallbackFactory marketingFeignFallbackFactory() {
        return new MarketingFeignFallbackFactory();
    }
}

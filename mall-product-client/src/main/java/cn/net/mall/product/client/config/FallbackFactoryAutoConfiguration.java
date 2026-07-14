package cn.net.mall.product.client.config;

import cn.net.mall.product.client.fallback.ProductFeignFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FallbackFactoryAutoConfiguration {
    @Bean
    public ProductFeignFallbackFactory productFeignFallbackFactory() {
        return new ProductFeignFallbackFactory();
    }
}

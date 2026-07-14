package cn.net.mall.order.client.config;

import cn.net.mall.order.client.fallback.OrderFeignFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FallbackFactoryAutoConfiguration {
    @Bean
    public OrderFeignFallbackFactory orderFeignFallbackFactory() {
        return new OrderFeignFallbackFactory();
    }
}

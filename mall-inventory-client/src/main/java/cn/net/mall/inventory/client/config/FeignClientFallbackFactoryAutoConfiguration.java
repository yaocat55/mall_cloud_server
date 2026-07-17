package cn.net.mall.inventory.client.config;

import cn.net.mall.inventory.client.fallback.InventoryFeignFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 降级工厂自动配置.
 */
@Configuration
public class FeignClientFallbackFactoryAutoConfiguration {

    @Bean
    public InventoryFeignFallbackFactory inventoryFeignFallbackFactory() {
        return new InventoryFeignFallbackFactory();
    }
}

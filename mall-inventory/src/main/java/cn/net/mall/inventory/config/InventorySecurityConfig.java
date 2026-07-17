package cn.net.mall.inventory.config;

import cn.net.mall.auth.config.PermitAllProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 库存服务安全配置 — 库存接口由内部 Feign 调用，放行鉴权.
 */
@Configuration
public class InventorySecurityConfig {

    @Bean
    public PermitAllProvider inventoryPublicUrls() {
        return () -> List.of(
                "/v1/inventory/**"
        );
    }
}

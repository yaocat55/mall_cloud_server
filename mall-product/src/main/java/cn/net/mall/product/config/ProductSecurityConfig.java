package cn.net.mall.product.config;

import cn.net.mall.auth.config.PermitAllProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Product 安全扩展配置.
 *
 * <p>C 端移动端接口无需认证，其余由默认自动配置处理（JWT 验签 + Redis 黑名单）。</p>
 */
@Configuration
public class ProductSecurityConfig {

    @Bean
    public PermitAllProvider productMobilePermitUrls() {
        return () -> List.of("/v1/mobile/**");
    }
}

package cn.net.mall.basic.config;

import cn.net.mall.auth.config.PermitAllProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Basic 安全扩展配置.
 *
 * <p>C 端接口无需认证，B 端接口由默认自动配置处理。</p>
 */
@Configuration
public class BasicSecurityConfig {

    @Bean
    public PermitAllProvider basicMobilePermitUrls() {
        return () -> List.of("/v1/mobile/**");
    }
}

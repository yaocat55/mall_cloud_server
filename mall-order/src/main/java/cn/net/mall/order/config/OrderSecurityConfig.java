package cn.net.mall.order.config;

import cn.net.mall.auth.config.PermitAllProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OrderSecurityConfig {
    @Bean
    public PermitAllProvider orderMobilePermitUrls() {
        return () -> List.of("/v1/mobile/**");
    }
}

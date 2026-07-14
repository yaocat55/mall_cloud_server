package cn.net.mall.pay.config;

import cn.net.mall.auth.config.PermitAllProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class PaySecurityConfig {
    @Bean
    public PermitAllProvider payMobilePermitUrls() {
        return () -> List.of("/v1/mobile/**");
    }
}

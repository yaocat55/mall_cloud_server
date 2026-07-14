package cn.net.mall.customer.config;

import cn.net.mall.auth.config.PermitAllProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class CustomerSecurityConfig {
    @Bean
    public PermitAllProvider customerMobilePermitUrls() {
        return () -> List.of("/v1/mobile/**");
    }
}

package cn.net.mall.recommend.config;

import cn.net.mall.auth.config.PermitAllProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class RecommendSecurityConfig {
    @Bean
    public PermitAllProvider recommendMobilePermitUrls() {
        return () -> List.of("/v1/mobile/**", "/mobile/v1/**");
    }
}

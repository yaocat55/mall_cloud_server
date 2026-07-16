package cn.net.mall.admin.config;

import cn.net.mall.auth.config.PermitAllProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Admin BFF 安全配置 — 注册公共白名单.
 *
 * <p>放行登录、验证码等不需认证的端点。</p>
 */
@Configuration
public class AdminBffSecurityConfig {

    @Bean
    public PermitAllProvider adminBffPublicUrls() {
        return () -> List.of(
                "/admin/v1/auth/login",
                "/admin/v1/auth/getCode"
        );
    }
}

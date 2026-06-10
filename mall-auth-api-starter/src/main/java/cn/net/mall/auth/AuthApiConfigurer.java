package cn.net.mall.auth;

import cn.net.mall.auth.interceptor.AuthApiInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 权限拦截器配置类
 *
 * @date 2025/1/13 18:53
 */
public class AuthApiConfigurer implements WebMvcConfigurer {

    private final AuthApiInterceptor authApiInterceptor;

    public AuthApiConfigurer(AuthApiInterceptor authApiInterceptor) {
        this.authApiInterceptor = authApiInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authApiInterceptor);
    }
}
package cn.net.mall.auth.config;

import cn.net.mall.auth.AuthApiConfigurer;
import cn.net.mall.auth.interceptor.AuthApiInterceptor;
import cn.net.mall.auth.interceptor.FeignAuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 权限API配置类
 *
 * @date 2025/1/13 16:36
 */
@Import({AuthApiConfigurer.class})
@Configuration
public class AuthApiAutoConfiguration {

    @Bean
    public AuthApiInterceptor authApiInterceptor(@Value("${mall.mgt.tokenSecret:}") String tokenSecret) {
        return new AuthApiInterceptor(tokenSecret);
    }

    @Bean
    public FeignAuthInterceptor feignAuthInterceptor() {
        return new FeignAuthInterceptor();
    }
}

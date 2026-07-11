package cn.net.mall.auth.config;

import cn.net.mall.auth.AuthApiConfigurer;
import cn.net.mall.auth.interceptor.AuthApiInterceptor;
import cn.net.mall.auth.interceptor.FeignAuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 认证 API 自动配置。
 *
 * 作用：
 *   将认证相关的核心组件注册为 Spring Bean，供所有依赖 mall-auth-api-starter
 *   的服务自动装配使用。
 *
 * 注册的 Bean：
 *   1. AuthApiInterceptor - 请求拦截器，解析 JWT 并注入 SecurityContext
 *   2. FeignAuthInterceptor - Feign 请求拦截器，自动透传 Authorization 令牌
 *
 * 同时导入 AuthApiConfigurer，用于将 AuthApiInterceptor 注册到 Spring MVC
 * 的拦截器链中。
 *
 * 配置要求：
 *   需要外部配置 mall.mgt.tokenSecret，用于 JWT 验签。
 *   若未配置，拦截器会跳过验签逻辑（不抛异常）。
 *
 * @date 2025/1/13 16:36
 */
@Import({AuthApiConfigurer.class})
@Configuration
public class AuthApiAutoConfiguration {

    /**
     * 注册认证 API 拦截器。
     *
     * 从配置文件读取 mall.mgt.tokenSecret 作为 JWT 签名密钥，
     * 若未配置则使用空字符串，拦截器会跳过 JWT 解析。
     *
     * @param tokenSecret JWT 签名密钥，配置项：mall.mgt.tokenSecret
     * @return AuthApiInterceptor 实例
     */
    @Bean
    public AuthApiInterceptor authApiInterceptor(@Value("${mall.mgt.tokenSecret:}") String tokenSecret) {
        return new AuthApiInterceptor(tokenSecret);
    }

    /**
     * 注册 Feign 调用拦截器。
     *
     * 在微服务间通过 Feign 发起内部调用时，自动将当前请求的
     * Authorization 令牌透传到下游服务。
     *
     * @return FeignAuthInterceptor 实例
     */
    @Bean
    public FeignAuthInterceptor feignAuthInterceptor() {
        return new FeignAuthInterceptor();
    }
}
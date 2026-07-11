package cn.net.mall.auth.config;

import java.util.List;

/**
 * Spring Security 白名单路径扩展接口。
 *
 * 如果服务需要额外的免认证白名单路径，定义一个 PermitAllProvider Bean 即可，
 * 无需重写整个 SecurityFilterChain。
 *
 * 用法示例：
 *
 *     @Configuration
 *     public class MySecurityExtender {
 *         @Bean
 *         public PermitAllProvider myPublicPaths() {
 *             return () -> List.of(
 *                 "/v1/public/**",
 *                 "/v1/callback/**"
 *             );
 *         }
 *     }
 *
 * 多个 Provider Bean 会自动合并，所有路径都会加入白名单。
 *
 * @see AuthSecurityAutoConfiguration#defaultFilterChain(HttpSecurity, List)
 * @since 1.0.0
 */
@FunctionalInterface
public interface PermitAllProvider {

    /**
     * 返回需要免认证的白名单路径列表。
     *
     * 路径格式支持 Spring Security 的 AntPathMatcher 通配符，
     * 如 /v1/public/** 匹配该路径下的所有子路径。
     *
     * @return 白名单路径列表，无白名单时返回空列表（不允许返回 null）
     */
    List<String> getPermitAllUrls();
}
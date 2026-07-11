package cn.net.mall.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security 默认自动配置。
 *
 * 为所有依赖 mall-auth-api-starter 的服务提供开箱即用的 Spring Security 保护。
 *
 * 默认行为：
 *   全部请求需认证（anyRequest().authenticated()），但自动放行以下路径：
 *   - Knife4j / Swagger 文档页面及静态资源
 *   - Druid 监控控制台
 *   - 通用静态资源（.html / .css / .js）
 *   - CORS 预检请求（OPTIONS）
 *
 * 扩展白名单：
 *   服务只需定义一个 PermitAllProvider Bean，即可添加额外的免认证路径，
 *   无需重写整个过滤器链。多个 Provider 的路径会自动合并。
 *
 * 自定义覆盖：
 *   - 任何服务定义自己的 SecurityFilterChain Bean 即可覆盖默认链
 *   - 任何服务定义自己的 PasswordEncoder Bean 即可覆盖默认加密器（BCrypt）
 *   - 任何服务定义自己的 GrantedAuthorityDefaults Bean 即可覆盖角色前缀（ROLE_）
 *
 * 所有条件注入均使用 @ConditionalOnMissingBean，保证用户自定义 Bean 优先。
 *
 * @see SpringSecurityConfig in mall-admin 自定义链的例子
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AuthSecurityAutoConfiguration {

    /**
     * 文档 / 监控 / 通用放行路径。
     *
     * 默认对所有服务生效，自定义 SecurityFilterChain 时需自行包含这些路径。
     */
    private static final List<String> PERMIT_ALL_URLS = List.of(
            "/doc.html",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/*",
            "/webjars/**",
            "/v3/api-docs/**",
            "/v3/api-docs/*/**",
            "/druid/*"
    );

    /**
     * 默认安全过滤器链。
     *
     * 配置策略：
     *   - 禁用 CSRF（无状态 API 无需开启）
     *   - 无状态会话（SessionCreationPolicy.STATELESS）
     *   - 未认证返回 401（HttpStatus.UNAUTHORIZED）
     *   - 合并内置白名单 + 服务扩展白名单（PermitAllProvider）
     *   - 其余所有请求需要认证
     *
     * 被任何自定义 SecurityFilterChain Bean 自动覆盖。
     */
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    SecurityFilterChain defaultFilterChain(
            HttpSecurity httpSecurity,
            List<PermitAllProvider> permitAllProviders) throws Exception {

        List<String> allPermitUrls = new ArrayList<>(PERMIT_ALL_URLS);
        if (permitAllProviders != null) {
            for (PermitAllProvider provider : permitAllProviders) {
                List<String> urls = provider.getPermitAllUrls();
                if (urls != null) {
                    allPermitUrls.addAll(urls);
                }
            }
        }

        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(HttpMethod.GET,
                                        "/*.html",
                                        "/*/*.html",
                                        "/*/*.css",
                                        "/*/*.js"
                                ).permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/*").permitAll()
                                .requestMatchers(allPermitUrls.toArray(new String[0])).permitAll()
                                .anyRequest().authenticated())
                .build();
    }

    /**
     * 默认密码编码器。
     *
     * 使用 BCrypt 强哈希算法，被任何自定义 PasswordEncoder Bean 覆盖。
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 默认角色前缀。
     *
     * 标准 Spring Security 行为，hasRole('admin') 匹配数据库中的 "ROLE_admin"。
     * 被任何自定义 GrantedAuthorityDefaults Bean 覆盖。
     */
    @Bean
    @ConditionalOnMissingBean(GrantedAuthorityDefaults.class)
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("ROLE_");
    }
}
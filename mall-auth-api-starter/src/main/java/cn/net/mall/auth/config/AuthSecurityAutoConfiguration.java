package cn.net.mall.auth.config;

import cn.net.mall.auth.filter.JwtAuthenticationFilter;
import cn.net.mall.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security 默认自动配置。
 *
 * <p>为所有依赖 mall-auth-api-starter 的服务提供开箱即用的 Spring Security 保护。</p>
 *
 * <h3>默认行为</h3>
 * <ul>
 *   <li>Bearer Token JWT 验签 + Redis 黑名单检查</li>
 *   <li>全部请求需认证，但自动放行 Swagger/Druid/静态资源</li>
 *   <li>无状态会话（STATELESS）</li>
 * </ul>
 *
 * <h3>扩展白名单</h3>
 * 服务只需定义一个 PermitAllProvider Bean，即可添加额外的免认证路径。
 *
 * <h3>自定义覆盖</h3>
 * <ul>
 *   <li>定义自己的 SecurityFilterChain Bean → 覆盖整个安全链</li>
 *   <li>定义自己的 JwtAuthenticationFilter Bean → 覆盖默认过滤器</li>
 *   <li>定义自己的 PasswordEncoder Bean → 覆盖默认加密器</li>
 * </ul>
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AuthSecurityAutoConfiguration {

    private static final List<String> PERMIT_ALL_URLS = List.of(
            "/doc.html", "/swagger-ui.html", "/swagger-ui/**",
            "/swagger-resources/*", "/webjars/**",
            "/v3/api-docs/**", "/v3/api-docs/*/**", "/druid/*"
    );

    private final String tokenSecret;
    private final RedisUtil redisUtil;

    public AuthSecurityAutoConfiguration(
            @Value("${mall.mgt.tokenSecret}") String tokenSecret,
            RedisUtil redisUtil) {
        this.tokenSecret = tokenSecret;
        this.redisUtil = redisUtil;
    }

    /**
     * 默认 JWT 认证过滤器 — 各服务可注入此 Bean 复用.
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenSecret, redisUtil);
    }

    /**
     * 默认安全过滤器链（含 JWT 认证 + 黑名单 + 白名单合并）.
     *
     * 被自定义 SecurityFilterChain Bean 自动覆盖。
     */
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    SecurityFilterChain defaultFilterChain(
            HttpSecurity httpSecurity,
            JwtAuthenticationFilter jwtAuthFilter,
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
                                        "/*.html", "/*/*.html",
                                        "/*/*.css", "/*/*.js"
                                ).permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/*").permitAll()
                                .requestMatchers(allPermitUrls.toArray(new String[0])).permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(GrantedAuthorityDefaults.class)
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("ROLE_");
    }
}
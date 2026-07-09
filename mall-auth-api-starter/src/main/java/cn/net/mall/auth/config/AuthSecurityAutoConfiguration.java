package cn.net.mall.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

/**
 * Spring Security 默认自动配置
 *
 * <p>为所有依赖 mall-auth-api-starter 的服务提供 Spring Security 保护。</p>
 *
 * <p><b>默认行为：</b>全部请求需认证（{@code anyRequest().authenticated()}）。</p>
 *
 * <p><b>自定义覆盖：</b></p>
 * <ul>
 *   <li>任何服务定义自己的 {@link SecurityFilterChain} Bean 即可覆盖默认链</li>
 *   <li>任何服务定义自己的 {@link PasswordEncoder} Bean 即可覆盖默认加密器</li>
 *   <li>任何服务定义自己的 {@link GrantedAuthorityDefaults} Bean 即可覆盖角色前缀</li>
 * </ul>
 *
 * <p><b>注意：</b>所有条件注入均使用 {@code @ConditionalOnMissingBean}，
 * 保证用户自定义 Bean 优先。</p>
 *
 * @see SpringSecurityConfig in mall-admin（自定义链的例子）
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AuthSecurityAutoConfiguration {

    /**
     * 默认安全过滤器链：全部请求需认证，无状态会话，禁用 CSRF。
     * 被任何自定义 {@link SecurityFilterChain} Bean 自动覆盖。
     */
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    SecurityFilterChain defaultFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated())
                .build();
    }

    /**
     * 默认密码编码器：BCrypt。
     * 被任何自定义 {@link PasswordEncoder} Bean 自动覆盖。
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 默认角色前缀：ROLE_（标准 Spring Security 行为）。
     * hasRole('admin') 匹配数据库中的 "ROLE_admin"。
     * 被任何自定义 {@link GrantedAuthorityDefaults} Bean 自动覆盖。
     */
    @Bean
    @ConditionalOnMissingBean(GrantedAuthorityDefaults.class)
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("ROLE_");
    }
}

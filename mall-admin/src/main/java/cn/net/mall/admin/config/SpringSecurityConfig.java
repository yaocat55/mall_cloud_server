package cn.net.mall.admin.config;

import cn.net.mall.admin.authenication.SmsAuthenticationProvider;
import cn.net.mall.admin.filter.JwtTokenFilter;
import cn.net.mall.basic.client.SmsRecordFeignClient;
import cn.net.mall.admin.mapper.auth.UserMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * Spring Security 配置
 * 
* 白名单（免登录 URL）：由 Spring Security 自身管理，不再依赖 @NoLogin 注解扫描。
 * Gateway AuthFilter 同时持有一份 Gateway 层面的白名单用于前置放行。
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SpringSecurityConfig {

    /**
     * 免登录白名单 URL 前缀列表（auth-api 内部 Spring Security 放行）
     * 注意：这些是 auth-api 控制器自身的路径（不含 Gateway 的 /api/auth 前缀）
     */
    private static final List<String> PERMIT_ALL_URLS = List.of(
            // Web 端认证
            "/v1/web/user/login",
            "/v1/web/user/loginByPhone",
            "/v1/web/user/getCode",
            "/v1/web/user/logout",

            // 移动端认证
            "/v1/mobile/user/login",
            "/v1/mobile/user/loginByPhone",
            "/v1/mobile/user/getCode",

            // 菜单、角色、部门、岗位（管理端）
            "/v1/menu/searchByPage",
            "/v1/menu/insert",
            "/v1/menu/update",
            "/v1/menu/deleteByIds",
            "/v1/role/all",
            "/v1/dept/findById",
            "/v1/dept/searchByPage",
            "/v1/dept/searchByTree",
            "/v1/job/searchByPage",
            "/v1/job/deleteByIds",

            // 测试
            "/v1/test/testOpenFeign"
    );

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter();
    }

    @Bean
    public SmsAuthenticationProvider smsAuthenticationProvider(
            UserDetailsService userDetailsService,
            UserMapper userMapper,
            SmsRecordFeignClient smsRecordFeignClient) {
        return new SmsAuthenticationProvider(userDetailsService, userMapper, smsRecordFeignClient);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            SmsAuthenticationProvider smsAuthenticationProvider,
            DaoAuthenticationProvider daoAuthenticationProvider) {
        return new ProviderManager(List.of(smsAuthenticationProvider, daoAuthenticationProvider));
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // 去除 ROLE_ 前缀
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                                    JwtTokenFilter jwtTokenFilter) throws Exception {
        return httpSecurity
                // 禁用 CSRF
                .csrf(csrf -> csrf.disable())
                // 授权异常
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
                .headers(frameOptions -> frameOptions.disable())
                // 不创建会话
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> {
                    // 静态资源
                    request.requestMatchers(HttpMethod.GET,
                                    "/*.html",
                                    "/*/*.html",
                                    "/*/*.css",
                                    "/*/*.js",
                                    "/websocket/*",
                                    "/job/*",
                                    "/init/*"
                            ).permitAll()
                            .requestMatchers("/swagger-ui.html").permitAll()
                            .requestMatchers("/swagger-ui/*").permitAll()
                            .requestMatchers("/doc.html").permitAll()
                            .requestMatchers("/swagger-resources/*").permitAll()
                            .requestMatchers("/webjars/**").permitAll()
                            .requestMatchers("/*/api-docs/**").permitAll()
                            .requestMatchers("/avatar/*").permitAll()
                            .requestMatchers("/druid/*").permitAll()
                            .requestMatchers(HttpMethod.OPTIONS, "/*").permitAll()
                            // 业务白名单
                            .requestMatchers(PERMIT_ALL_URLS.toArray(new String[0])).permitAll()
                            // 其余全部需认证
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

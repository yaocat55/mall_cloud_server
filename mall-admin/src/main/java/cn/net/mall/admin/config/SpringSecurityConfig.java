package cn.net.mall.admin.config;

import cn.net.mall.admin.authenication.SmsAuthenticationProvider;
import cn.net.mall.admin.filter.JwtTokenFilter;
import cn.net.mall.basic.client.SmsRecordFeignClient;
import cn.net.mall.admin.mapper.auth.UserMapper;
import cn.net.mall.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * Spring Security 配置（mall-admin 专有）.
 *
 * 自定义安全规则，覆盖 AuthSecurityAutoConfiguration 的默认全认证链。
 *
 * 职责：
 * 1. 管理白名单路径（登录、Swagger、静态资源、内部 Feign 调用等）
 * 2. 配置认证提供者（密码登录 + 短信登录）
 * 3. 注册 JWT 过滤器（含 Redis 黑名单校验）
 *
 * 通用 Bean（PasswordEncoder、GrantedAuthorityDefaults）由 starter 提供，
 * 本配置只关注 admin 服务特有的安全组件。
 *
 * @author system
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class SpringSecurityConfig {

    /**
     * 免登录白名单 URL 路径列表.
     *
     * 注意：这些是 auth-api 控制器自身的路径，不含 Gateway 路由前缀 /api/auth。
     * 例如：实际请求 /api/auth/v1/auth/web/user/login，匹配规则为 /v1/auth/web/user/login。
     *
     * 白名单分类：
     * - Web 端认证：登录、验证码、登出（admin 后台登录）
     * - 管理端基础数据：菜单、角色、部门、岗位（增删改查）
     * - 测试接口：OpenFeign 连通性测试
     */
    private static final List<String> PERMIT_ALL_URLS = List.of(
            // Web 端认证（admin 后台登录）
            "/v1/auth/web/user/login",
            "/v1/auth/web/user/loginByPhone",
            "/v1/auth/web/user/getCode",
            "/v1/auth/web/user/logout",

            // 菜单、角色、部门、岗位（管理端）
            "/v1/auth/menu/searchByPage",
            "/v1/auth/menu/insert",
            "/v1/auth/menu/update",
            "/v1/auth/menu/deleteByIds",
            "/v1/auth/role/all",
            "/v1/auth/dept/findById",
            "/v1/auth/dept/searchByPage",
            "/v1/auth/dept/searchByTree",
            "/v1/auth/job/searchByPage",
            "/v1/auth/job/deleteByIds",

            // 测试登录（跳过验证码）
            "/v1/auth/user/testLogin",

            // 测试
            "/v1/test/testOpenFeign"
    );

    /**
     * JWT 认证过滤器.
     *
     * 直接注入 tokenSecret 和 RedisUtil，通过 TokenUtil.parseClaimsFromToken 验签，
     * 避免依赖 TokenHelper bean（需要 SpringUtil 从容器查找，存在 bean 未注册的风险）。
     *
     * 职责：
     * 1. JWT 验签 + 过期校验
     * 2. Redis 黑名单校验（admin 接口专属，支持踢人下线）
     * 3. 验签通过后设置 SecurityContext
     *
     * @param tokenSecret JWT 签名密钥
     * @param redisUtil Redis 工具类（用于黑名单校验）
     * @return JwtTokenFilter 实例
     */
    @Bean
    public JwtTokenFilter jwtTokenFilter(@Value("${mall.mgt.tokenSecret}") String tokenSecret, RedisUtil redisUtil) {
        return new JwtTokenFilter(tokenSecret, redisUtil);
    }

    /**
     * 短信验证码认证提供者.
     *
     * 支持手机号 + 验证码登录方式。
     *
     * @param userDetailsService 用户详情服务
     * @param userMapper 用户 Mapper（用于查询用户）
     * @param smsRecordFeignClient 短信记录 Feign 客户端（用于校验验证码）
     * @return SmsAuthenticationProvider 实例
     */
    @Bean
    public SmsAuthenticationProvider smsAuthenticationProvider(
            UserDetailsService userDetailsService,
            UserMapper userMapper,
            SmsRecordFeignClient smsRecordFeignClient) {
        return new SmsAuthenticationProvider(userDetailsService, userMapper, smsRecordFeignClient);
    }

    /**
     * 用户名密码认证提供者.
     *
     * 支持用户名 + 密码登录方式。
     *
     * @param passwordEncoder 密码编码器（BCrypt）
     * @param userDetailsService 用户详情服务
     * @return DaoAuthenticationProvider 实例
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    /**
     * 认证管理器.
     *
     * 组合多个认证提供者，按顺序尝试认证：
     * 1. SmsAuthenticationProvider（短信验证码登录）
     * 2. DaoAuthenticationProvider（用户名密码登录）
     *
     * @param smsAuthenticationProvider 短信认证提供者
     * @param daoAuthenticationProvider 密码认证提供者
     * @return AuthenticationManager 实例
     */
    @Bean
    public AuthenticationManager authenticationManager(
            SmsAuthenticationProvider smsAuthenticationProvider,
            DaoAuthenticationProvider daoAuthenticationProvider) {
        return new ProviderManager(List.of(smsAuthenticationProvider, daoAuthenticationProvider));
    }

    /**
     * Spring Security 过滤器链.
     *
     * 安全策略：
     * 1. 禁用 CSRF（无状态 API 不需要）
     * 2. 无状态会话（SessionCreationPolicy.STATELESS）
     * 3. 白名单路径放行（静态资源 + 业务公开接口）
     * 4. 其余接口均需认证
     * 5. JWT 过滤器在 UsernamePasswordAuthenticationFilter 之前执行
     *
     * 过滤器顺序：
     * JwtTokenFilter（JWT 验签 + 黑名单）→ UsernamePasswordAuthenticationFilter（Spring Security 默认）
     *
     * @param httpSecurity HttpSecurity 构建器
     * @param jwtTokenFilter JWT 认证过滤器
     * @return SecurityFilterChain 实例
     * @throws Exception 配置异常
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                                    JwtTokenFilter jwtTokenFilter) throws Exception {
        return httpSecurity
                // 禁用 CSRF（无状态 API 无需防护）
                .csrf(csrf -> csrf.disable())
                // 授权异常处理（返回 403）
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
                // 禁用 frame 选项（允许 H2 等控制台使用）
                .headers(frameOptions -> frameOptions.disable())
                // 不创建会话（每次请求独立）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> {
                    // ========== 静态资源（全部放行） ==========
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

                            // ========== 业务白名单 ==========
                            .requestMatchers(PERMIT_ALL_URLS.toArray(new String[0])).permitAll()
                            .requestMatchers("/v1/internal/**").permitAll()

                            // ========== 其余全部需认证 ==========
                            .anyRequest().authenticated();
                })
                // JWT 过滤器在 UsernamePasswordAuthenticationFilter 之前执行
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
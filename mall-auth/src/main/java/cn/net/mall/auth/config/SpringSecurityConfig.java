package cn.net.mall.auth.config;

import cn.net.mall.annotation.NoLogin;
import cn.net.mall.auth.authenication.SmsAuthenticationProvider;
import cn.net.mall.auth.filter.JwtTokenFilter;
import cn.net.mall.auth.mapper.auth.UserMapper;
import cn.net.mall.auth.service.user.UserDetailsServiceImpl;
import cn.net.mall.auth.util.NoLoginMap;
import cn.net.mall.basic.client.SmsRecordFeignClient;
import cn.net.mall.redis.RedisUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.*;

/**
 * SpringSecurity配置类
 *
 * @date 2024/1/10 下午4:29
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SpringSecurityConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        jwtTokenFilter = new JwtTokenFilter();
        return jwtTokenFilter;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public SmsAuthenticationProvider smsAuthenticationProvider() {
        UserDetailsServiceImpl userDetailsService = applicationContext.getBean(UserDetailsServiceImpl.class);
        UserMapper userMapper = applicationContext.getBean(UserMapper.class);
        SmsRecordFeignClient smsRecordFeignClient = applicationContext.getBean(SmsRecordFeignClient.class);
        return new SmsAuthenticationProvider(userDetailsService, userMapper, smsRecordFeignClient);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(applicationContext.getBean(UserDetailsServiceImpl.class));
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(SmsAuthenticationProvider smsAuthenticationProvider, DaoAuthenticationProvider daoAuthenticationProvider) {
        List authenticationProviders = new ArrayList();
        authenticationProviders.add(smsAuthenticationProvider);
        authenticationProviders.add(daoAuthenticationProvider);
        ProviderManager authenticationManager = new ProviderManager(authenticationProviders);
        return authenticationManager;

    }


    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // 去除 ROLE_ 前缀
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 密码加密方式
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        initNoLogin(applicationContext);
        return httpSecurity
                // 禁用 CSRF
                .csrf(csrf -> csrf.disable())
                // 授权异常
                .exceptionHandling(exception -> exception.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
                .headers(frameOptions -> frameOptions.disable())

                // 不创建会话
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> {
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
                            .requestMatchers(NoLoginMap.getNoLoginUrlSet().toArray(new String[0])).permitAll()
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private void initNoLogin(ApplicationContext applicationContext) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class).getHandlerMethods();
        if (MapUtils.isEmpty(handlerMethodMap)) {
            return;
        }
        Set<String> noLoginUrls = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = infoEntry.getValue();
            NoLogin noLogin = handlerMethod.getMethodAnnotation(NoLogin.class);
            PathPatternsRequestCondition pathPatternsCondition = infoEntry.getKey().getPathPatternsCondition();
            if (null != noLogin && null != pathPatternsCondition) {
                Set<PathPattern> patterns = pathPatternsCondition.getPatterns();
                Iterator<PathPattern> iterator = patterns.iterator();
                if(iterator.hasNext()) {
                    PathPattern next = iterator.next();
                    noLoginUrls.add(next.getPatternString());
                }
            }
        }
        NoLoginMap.initSet(noLoginUrls);
    }

}

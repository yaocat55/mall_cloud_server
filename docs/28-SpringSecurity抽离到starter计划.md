# Spring Security 抽离到 mall-auth-api-starter 计划

## 一、现状

### 1.1 当前鉴权层分布

| 组件 | 所在位置 | 职责 |
|------|---------|------|
| `AuthApiInterceptor` | mall-auth-api-starter | 解析 JWT，设 SecurityContext |
| `FeignAuthInterceptor` | mall-auth-api-starter | Feign 透传 Authorization 头 |
| `SpringSecurityConfig` | **mall-admin（独有）** | `@EnableWebSecurity` + 权限规则 |
| `JwtTokenFilter` | **mall-admin（独有）** | Redis 黑名单 |
| `PasswordEncoder` | **各服务各写各的** | BCrypt 加密 |

### 1.2 问题

```
AuthApiInterceptor 设身份（JWT → SecurityContext）
  ↓
但安检门没开（无 @EnableWebSecurity）
  ↓
身份设了没人查，等于白设
```

10 个服务（不含 mall-admin）解析了 JWT 但不做任何安全检查。

---

## 二、改造目标

把 `SpringSecurityConfig` 中的通用部分从 `mall-admin` 抽到 `mall-auth-api-starter`，让所有依赖该 starter 的服务自动获得 Spring Security 保护。

### 2.1 移到 starter ✅

| 组件 | 说明 |
|------|------|
| `@EnableWebSecurity` | 开启 Spring Security |
| `@EnableMethodSecurity` | 支持 `@PreAuthorize` 注解 |
| `默认 SecurityFilterChain` | 全部请求需认证（`anyRequest().authenticated()`） |
| `PasswordEncoder` | BCryptPasswordEncoder |
| `GrantedAuthorityDefaults` | 标准 `ROLE_` 前缀 |
| `Http403ForbiddenEntryPoint` | 统一认证异常处理 |

### 2.2 留在 mall-admin ❌

| 组件 | 理由 |
|------|------|
| 业务白名单（PERMIT_ALL_URLS） | admin 自己的接口路径，其他服务没有 |
| Swagger/静态资源白名单 | admin 自己暴露的文档路径 |
| `JwtTokenFilter` (Redis 黑名单) | 依赖 Redis，仅 admin 需要踢人下线 |
| `AuthenticationManager` | 只有 admin 做账号密码登录 |
| `SmsAuthenticationProvider` | 只有 admin 有短信登录 |
| `DaoAuthenticationProvider` | 绑定 admin 的 UserDetailsService |

### 2.3 各服务清理 ❌

部分服务（如 mall-customer）自己定义了 `PasswordEncoder` Bean，抽到 starter 后需删除重复的定义。

| 服务 | 重复 Bean | 操作 |
|------|----------|------|
| mall-customer | `PasswordEncoder` | 删除服务内的重复定义 |
| 其他 | 无 | 无需操作 |

---

## 三、技术方案

### 3.1 默认链 + 服务覆写

利用 Spring Boot 的条件注入机制——**starter 提供默认链，服务可以自定义链自动覆盖**：

```java
// mall-auth-api-starter
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AuthSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    SecurityFilterChain defaultFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated())
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
```

```java
// mall-admin（自定义链自动覆盖 starter 的默认链）
@Configuration
public class SpringSecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                                    JwtTokenFilter jwtTokenFilter) throws Exception {
        // 自己的规则：白名单 + JwtTokenFilter + ...
        return httpSecurity
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(PERMIT_ALL_URLS).permitAll()
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
```

### 3.2 影响范围

| 服务 | 行为变化 | 风险 |
|------|---------|------|
| **mall-admin** | 不变（自定义链覆盖默认链） | 无 |
| **mall-admin-bff** | 全部请求需认证 | ✅ 原来就设了 SecurityContext |
| **mall-mobile-bff** | 全部请求需认证 | ✅ 原来就设了 SecurityContext |
| **mall-basic** | 全部请求需认证 | ✅ 原来就设了 SecurityContext |
| **mall-product** | 全部请求需认证 | ✅ 原来就设了 SecurityContext |
| **mall-order** | 全部请求需认证 | ✅ 原来就设了 SecurityContext |
| **mall-customer** | 全部请求需认证，删除重复 PasswordEncoder | ⚠️ 见下方 |
| **mall-marketing** | 全部请求需认证 | ✅ 原来就设了 SecurityContext |
| **mall-pay** | 全部请求需认证 | ✅ 原来就设了 SecurityContext |
| **mall-recommend** | 全部请求需认证 | ✅ 原来就设了 SecurityContext |
| **mall-message** | 全部请求需认证 | ✅ 原来就设了 SecurityContext |

### 3.3 潜在风险

| 风险 | 原因 | 对策 |
|------|------|------|
| `/v1/internal/**` 被拦 | 默认链要求全部请求认证 | 服务的自定义白名单在 starter 之后加，或确认 FeignAuthInterceptor 正常透传了 JWT |
| 异步/定时任务 Feign 调用失败 | 无请求上下文，FeignAuthInterceptor 不传 JWT | 这些场景本来就不该走到鉴权路径，或者需要手动传 token |
| 公开接口（如注册）被拦 | 默认链要求认证 | 这些接口在 Gateway 白名单里，不受影响；如果后端有额外公开接口需写自己的 `SecurityFilterChain` |
| 引入的依赖冲突 | `spring-boot-starter-security` 依赖 | 已有服务可能缺 jar，需确认 pom |

### 3.4 回滚

- 不改代码结构，只是一个新增类 + 条件注解
- 线上出问题直接删 `AuthSecurityAutoConfiguration` 或移除 `@EnableWebSecurity` 即可恢复
- `@ConditionalOnMissingBean` 保证任何服务自定义了 `SecurityFilterChain` 就不会用默认链

---

## 四、实施步骤

- [x] 4.1 在 `mall-auth-api-starter` 的 `pom.xml` 添加 `spring-boot-starter-security` 依赖
- [x] 4.2 创建 `AuthSecurityAutoConfiguration` 类（默认链 + `@EnableWebSecurity` + `@EnableMethodSecurity`）
- [x] 4.3 更新 `AutoConfiguration.imports` 注册新配置类
- [x] 4.4 修改 `mall-admin` 的 `SpringSecurityConfig`：移除 `@EnableWebSecurity/@EnableMethodSecurity`、`PasswordEncoder`、`GrantedAuthorityDefaults`（由 starter 提供）
- [x] 4.5 删除 `mall-customer` 的重复 `PasswordEncoder` Bean
- [x] 4.6 全项目 mvn compile 验证
- [ ] 4.7 启动全服务 + 客户端请求验证

---

## 五、补充说明

### 关于 BFF 层

BFF（admin-bff、mobile-bff）也会获得 Spring Security 保护。之前 BFF 虽然会解析 JWT、设 SecurityContext，但从不检查——现在有了 `@EnableWebSecurity`，未认证请求会被拒绝。

但 Gateway 白名单已经保证了只有带有效 JWT 的请求才能到达 BFF，所以实际不会有影响，只是多了一层后端兜底防护。

### 关于 `@PreAuthorize`

starter 启用了 `@EnableMethodSecurity`，以后任何服务可以在控制器或 Service 上写：

```java
@PreAuthorize("hasRole('admin')")
public void deleteOrder() { ... }
```

实现**方法级的精细权限控制**，不只是拦不拦的问题。

### 关于 `/v1/internal/`

当前 `mall-admin` 放行了 `/v1/internal/**`，因为 Feign 调用走这条路。移到 starter 后默认链不放过任何路径——但问题不大：
- FeignAuthInterceptor 会透传 Authorization 头
- 后端 AuthApiInterceptor 能正常解析 JWT、设 SecurityContext
- 所以 `/v1/internal/**` 也能通过认证，不需要额外 permitAll

如果某个内部接口在无身份时被调用（定时任务、MQ），才需要额外放行。按需加白名单即可。

# Spring Security 统一管理（mall-auth-api-starter）

> 此文已从"计划"更新为"现状 + 使用指南"。
> 详见 `AuthSecurityAutoConfiguration` 和 `PermitAllProvider`。

## 一、架构概览

```
┌─────────────────────────────────────────────────────────────────┐
│                    服务模块 (basic / product / order ...)         │
│  pom.xml: mall-auth-api-starter (含 common-security 传递依赖)   │
└───────────────────────┬─────────────────────────────────────────┘
                        │
          ┌─────────────┴─────────────┐
          │                           │
          ▼                           ▼
┌─────────────────────┐   ┌──────────────────────────┐
│  mall-auth-api-starter │   │    common-security       │
│  (基础设施层)          │   │   (工具库 — 传递依赖)    │
├─────────────────────┤   ├──────────────────────────┤
│ AuthApiInterceptor  │   │ TokenUtil                │
│   ↓ 调 TokenUtil    │◄──│   JWT 解析/生成/验签     │
│   ↓ 创建 JwtUserEntity│   │                          │
│   ↓ 设 SecurityContext│   │ JwtUserEntity            │
│   ↓ 调 FillUserUtil  │◄──│   implements UserDetails │
│                     │   │                          │
│ FeignAuthInterceptor│   │ FillUserUtil              │
│   ↓ 调 TokenUtil    │◄──│   ThreadLocal 用户填充    │
│   ↓ 透传 Authorization│  │                          │
│                     │   │                          │
│ AuthSecurityConfig  │   │                          │
│ PermitAllProvider   │   │                          │
└─────────────────────┘   └──────────────────────────┘
                        │
            ┌───────────┴───────────┐
            │                       │
            ▼                       ▼
┌─────────────────────────────────────────────────────┐
│  mall-admin (自定义链)                               │
│  SpringSecurityConfig                               │
│    ├─ JwtTokenFilter (Redis 黑名单)                  │
│    ├─ AuthenticationManager                         │
│    ├─ 业务白名单 (登录/菜单/角色等)                   │
│    └─ 403 认证异常处理                               │
└─────────────────────────────────────────────────────┘
```

---

## 二、当前白名单内容

### 2.1 Starter 内置白名单（所有服务共享）

来自 `AuthSecurityAutoConfiguration.PERMIT_ALL_URLS`：

| 类别 | 路径 | 说明 |
|------|------|------|
| Knife4j | `/doc.html` | 文档主页面 |
| Swagger UI | `/swagger-ui.html`, `/swagger-ui/**` | Swagger 3 页面 |
| Swagger 兼容 | `/swagger-resources/*` | Swagger 2 资源 |
| WebJars | `/webjars/**` | 前端静态资源 |
| OpenAPI 3 | `/v3/api-docs/**`, `/v3/api-docs/*/**` | API 规范文档 |
| Druid | `/druid/*` | 数据库连接池监控 |

以及过滤器链中直接放的：

| 类别 | 匹配规则 | 说明 |
|------|----------|------|
| 通用静态资源 | `GET /*.html`, `/*/*.html`, `/*/*.css`, `/*/*.js` | 前端页面依赖 |
| CORS 预检 | `OPTIONS /*` | 跨域请求放行 |

### 2.2 Admin 额外业务白名单

> 仅 `mall-admin` 有，其他服务没有。

| 类别 | 路径 |
|------|------|
| 内部 Feign 调用 | `/v1/internal/**` |
| Web 端认证 | `/v1/auth/web/user/login`, `loginByPhone`, `getCode`, `logout` |
| 移动端认证 | `/v1/mobile/user/login`, `loginByPhone`, `getCode` |
| 菜单 | `/v1/auth/menu/searchByPage`, `insert`, `update`, `deleteByIds` |
| 角色 | `/v1/auth/role/all` |
| 部门 | `/v1/auth/dept/findById`, `searchByPage`, `searchByTree` |
| 岗位 | `/v1/auth/job/searchByPage`, `deleteByIds` |
| 测试 | `/v1/test/testOpenFeign` |
| 额外资源 | `/avatar/*`, `/websocket/*`, `/job/*`, `/init/*` |

---

## 三、服务开发者使用指南

### 3.1 什么也不用做

如果服务没有公开接口（比如 basic、product、order 等内部微服务），**零配置**——starter 的默认链会自动生效，所有请求需要认证。

### 3.2 加几个公开路径（最常见场景）

只需定义一个 `PermitAllProvider` Bean，不需要写任何 Security 配置：

```java
package cn.net.mall.basic.config;

import cn.net.mall.auth.config.PermitAllProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BasicSecurityExtender {

    @Bean
    public PermitAllProvider basicPublicPaths() {
        return () -> List.of(
            "/v1/basic/open/**",       // 开放接口
            "/v1/basic/callback/**"    // 回调通知
        );
    }
}
```

多个 `PermitAllProvider` Bean 会自动合并，同时生效。

### 3.3 完全覆盖 Security 链（大改动）

> ⚠️ 代价：需要自行包含 starter 内置的所有放行路径。

```java
package cn.net.mall.xxx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import java.util.List;

@Configuration
public class CustomSecurityConfig {

    private static final List<String> PERMIT_ALL = List.of(
            "/doc.html",
            "/swagger-ui.html", "/swagger-ui/**",
            "/swagger-resources/*",
            "/webjars/**",
            "/v3/api-docs/**", "/v3/api-docs/*/**",
            "/druid/*",
            "/v1/my/public/**"
    );

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
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
                                .requestMatchers(PERMIT_ALL.toArray(new String[0])).permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(myJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
```

### 3.4 哪个服务用哪种方式？

| 服务 | 方式 | 说明 |
|------|------|------|
| **mall-admin** | ✅ 自定义链 | 有自己的 JWT 黑名单、登录认证 |
| **mall-admin-bff** | 默认链 | 无特殊需求 |
| **mall-mobile-bff** | 默认链 | 无特殊需求 |
| **mall-basic** | 默认链 | 默认即可 |
| **mall-product** | 默认链 | 默认即可 |
| **mall-order** | 默认链 | 默认即可 |
| **mall-marketing** | 默认链 | 默认即可 |
| **mall-pay** | 默认链 | 默认即可 |
| **mall-customer** | 默认链 | 默认即可 |
| **mall-recommend** | 默认链 | 默认即可 |
| **mall-message** | 默认链 | 默认即可 |

---

## 四、核心源码结构

### 4.1 `AuthSecurityAutoConfiguration.java`

位置：`mall-auth-api-starter/src/main/java/cn/net/mall/auth/config/AuthSecurityAutoConfiguration.java`

```java
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AuthSecurityAutoConfiguration {

    // 内置白名单
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

    // 过滤器链（支持 PermitAllProvider 扩展）
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    SecurityFilterChain defaultFilterChain(
            HttpSecurity httpSecurity,
            List<PermitAllProvider> permitAllProviders) throws Exception {

        List<String> allPermitUrls = new ArrayList<>(PERMIT_ALL_URLS);
        if (permitAllProviders != null) {
            for (PermitAllProvider provider : permitAllProviders) {
                List<String> urls = provider.getPermitAllUrls();
                if (urls != null) allPermitUrls.addAll(urls);
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

### 4.2 `PermitAllProvider.java`

位置：`mall-auth-api-starter/src/main/java/cn/net/mall/auth/config/PermitAllProvider.java`

```java
@FunctionalInterface
public interface PermitAllProvider {
    List<String> getPermitAllUrls();
}
```

---

## 五、common-security 与 mall-auth-api-starter 的关系

### 5.1 分工

| 模块 | 角色 | 提供什么 |
|------|------|----------|
| **common-security** | 工具库（数据模型 + 工具类） | `TokenUtil`（JWT 签验）、`JwtUserEntity`（UserDetails 实现）、`FillUserUtil`（ThreadLocal 用户填充） |
| **mall-auth-api-starter** | 基础设施（拦截器 + Security 配置） | `AuthApiInterceptor`、`FeignAuthInterceptor`、`AuthSecurityAutoConfiguration`、`PermitAllProvider` |

### 5.2 完整认证数据流

```
1. 请求到达
   ┌────────────────────────────────────────────┐
   │ Authorization: Bearer <jwt>                 │
   │ X-User-Id: 1                                │  ← Gateway 透传
   │ X-User-Name: admin                          │
   └────────────────────────────────────────────┘

2. AuthApiInterceptor.preHandle()
   ┌────────────────────────────────────────────┐
   │  TokenUtil.getTokenForAuthorization(req)   │  ← common-security
   │  TokenUtil.parseClaimsFromToken(token,sec) │  ← common-security
   │  ↓                                          │
   │  JwtUserEntity jwtUser = new JwtUserEntity()│  ← common-security
   │  authentication = new                       │
   │    UsernamePasswordAuthenticationToken(...) │
   │  SecurityContextHolder.set(authentication)  │
   │  FillUserUtil.setCurrentUser(id, name)      │  ← common-security
   └────────────────────────────────────────────┘

3. AuthSecurityAutoConfiguration 拦截未认证请求
   ┌────────────────────────────────────────────┐
   │  SecurityFilterChain                       │
   │    ├─ 白名单 → permitAll                   │
   │    ├─ 有 JWT → 步骤 2 已设认证 → 通过       │
   │    └─ 无 JWT → 401 Unauthorized            │
   └────────────────────────────────────────────┘

4. FeignAuthInterceptor (跨服务调用时)
   ┌────────────────────────────────────────────┐
   │  TokenUtil.getAuthorization(req)           │  ← common-security
   │  template.header(Authorization, token)     │
   │  template.header(INNER-REQUEST, true)      │
   └────────────────────────────────────────────┘
   → 下游服务重复步骤 2-3
```

### 5.3 依赖关系

```
服务 pom.xml 只需要写：
  <dependency>
      <groupId>cn.net.mall</groupId>
      <artifactId>mall-auth-api-starter</artifactId>
  </dependency>

mall-auth-api-starter 的 pom.xml 已经包含了：
  <dependency>
      <groupId>cn.net.mall</groupId>
      <artifactId>common-security</artifactId>
  </dependency>

所以 common-security 是传递依赖，服务不需要重复声明。
```

### 5.4 服务为什么要显式声明 common-security？

虽然 starter 已传递依赖 common-security，但多数服务**直接使用**了 common-security 的类：

| 服务 | 直接使用的 common-security 类 |
|------|------------------------------|
| mall-basic | `FillUserUtil`（9 个 Service） |
| mall-product | `FillUserUtil`（16 个 Service）、`JwtUserEntity`（2 个 Service） |
| mall-order | `FillUserUtil`（4 个 Service）、`JwtUserEntity`（2 个 Service） |
| mall-marketing | `FillUserUtil`（8 个 Service） |
| mall-customer | `FillUserUtil` |
| mall-message | `FillUserUtil` |
| mall-recommend | `FillUserUtil` |
| mall-pay | `FillUserUtil` |
| mall-gateway | `TokenUtil`（Gateway 独有，使用 ServerHttpRequest 重载） |
| mall-admin | `TokenUtil`、`FillUserUtil` |

对于这些服务，显式声明 common-security 是 **代码可读性的选择**（明确告诉读者"我用了这个库"），不是编译必须。删除显式声明也能编译通过，但 IDE 提示和代码审查时不够直观。

### 5.5 与 Gateway 的关系

```
客户端 → Gateway (AuthFilter)
            │
            ├─ JWT 验证 ← TokenUtil (common-security 的 ServerHttpRequest 版本)
            ├─ 通过 → 透传 X-User-Id / X-User-Name / X-Roles 头
            ├─ 失败 → 401
            │
            ▼
         后端服务
            │
            ├─ AuthApiInterceptor (starter)
            │   ├─ 验证 JWT claims 与请求头一致
            │   └─ 设 SecurityContext
            │
            ├─ AuthSecurityAutoConfiguration (starter)
            │   ├─ 白名单放行
            │   └─ 兜底拦截
            │
            └─ FeignAuthInterceptor (starter)
                └─ 透传 Authorization 到下游

注意：Gateway 是唯一直接依赖 common-security 但不依赖 starter 的服务
（因为 Gateway 是 WebFlux 响应式应用，starter 的 Servlet API 拦截器不适用）
```

---

## 六、常见问题

### Q: 加了 `PermitAllProvider` 后启动报错说找不到这个类？

A: 确保服务的 `pom.xml` 依赖了 `mall-auth-api-starter`：

```xml
<dependency>
    <groupId>cn.net.mall</groupId>
    <artifactId>mall-auth-api-starter</artifactId>
    <version>${mall-auth-api-starter.version}</version>
</dependency>
```

### Q: 我定义的 `PermitAllProvider` 路径没生效？

A: 检查两点：
1. 服务没有自定义 `SecurityFilterChain` Bean（自定义链会完全覆盖默认链，不再收集 `PermitAllProvider`）
2. `PermitAllProvider` 的方法返回了正确的 AntPath 模式（如 `/v1/public/**`）

### Q: 自定义 `SecurityFilterChain` 后 Knife4j 打不开了？

A: 自定义链完全替代了 starter 的默认链，需要自己包含 Swagger/Knife4j 放行路径。参考第三章 3.3 节。

### Q: 为什么 `mall-admin` 返回 403 而其他服务返回 401？

A: Admin 使用 `Http403ForbiddenEntryPoint`（见 `SpringSecurityConfig`），其他服务使用 starter 默认的 `HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)`。这是有意为之——admin 先过 JWT 校验再过权限校验，权限不够时返回 403 更准确。


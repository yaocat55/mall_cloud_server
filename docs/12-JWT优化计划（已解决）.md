# JWT 鉴权优化 —— 改动计划

## 一、当前架构

```
Redis 存储结构（每个登录用户）：
  USER_PREFIX + username → JwtUserEntity {
    id: 1,
    username: "admin",
    password: "...",
    authorities: ["ROLE_ADMIN", "system:user:list", "order:create", ...],
    roles: ["ROLE_ADMIN", "system:user:list", "order:create", ...]
  }
```

### 请求链路 Redis 调用分布

```
① 用户登录 → tokenHelper.generateToken(jwtUserEntity)
               ├─ 生成 JWT（仅 subject=username）
               └─ 存 JwtUserEntity 到 Redis  ← 写 1 次

② 每次请求 → Gateway 验签
           → AuthApiInterceptor（全部 10 个服务）
              ├─ getUsernameFromToken(token)          // JWT 解析，无 Redis
              └─ getUserDetailsFromUsername(username)  // Redis 读  ← 每个请求 1 次
           → FillUserUtil.setCurrentUser(id, username)
           
③ mall-auth 特有 → JwtTokenFilter
              └─ getUserDetailsFromUsername(username)  // Redis 读  ← 每个请求 1 次
                 → 设入 SecurityContext（含 authorities）

④ mall-message 特有 → AuthHandshakeInterceptor（WebSocket）
              └─ getUserDetailsFromUsername(username)  // Redis 读

⑤ logout / cancelAccount → delToken(token)            // Redis 删
```

### 某次请求经过 3 个服务时的 Redis 次数

```
客户端 → admin-api → product-api → order-api
         1 次 Redis    1 次 Redis   1 次 Redis  = 3 次不必要的 Redis IO
```

---

## 二、目标架构

```
JWT Claims 设计：
  sub: "admin"
  user_id: 1
  user_name: "admin"
  roles: ["ROLE_ADMIN", "system:user:list", ...]
  iat: 签发时间
  exp: 过期时间
  jti: 唯一 token ID（用于黑名单）

Redis 仅存两样东西：
  blacklist:{jti} → "1"  （TTL = token 剩余有效期，被踢的 token）
  user_token_jti:{userId} → "{jti}"  （当前登录用户的 jti，用于踢人时查）
```

### 改造后链路

```
① 用户登录 → 自行构造 JWT（含 claims），不再调 tokenHelper.generateToken()
               ├─ Jwts.builder().claim("user_id", id).claim("roles", roles)...
               ├─ 不再存 JwtUserEntity 到 Redis
               └─ 新增：SET user_token_jti:{userId} = jti EX {token有效期}

② 每次请求 → Gateway
               ├─ 验签 JWT
               ├─ 白名单直接放行
               ├─ EXISTS blacklist:{jti} → 存在则 401 踢回
               └─ 透传 X-User-Id / X-User-Name / X-Roles Header 到下游
             → AuthApiInterceptor（全部服务）
               ├─ 从 Header 或 JWT claims 直接取身份
               └─ 0 次 Redis

③ 登录态校验：全部由 Gateway 完成，业务服务完全不关心
   黑名单检查：Gateway 直连 Redis，不调 auth 的 Feign 接口

④ logout → 从 redis 查当前 jti → SET blacklist:{jti} = 1 EX {token有效期}
   踢人   → 从 user_token_jti:{userId} 查 jti → SET blacklist:{jti}
```

### 跟原方案的关键区别

| 之前方案 | 最终方案 |
|---------|---------|
| 黑名单检查在 auth 的 JwtTokenFilter | 黑名单检查在 Gateway |
| 业务服务不查 Redis，但 auth 查 | **全服务不查 Redis，包括 auth** |
| Gateway 只做验签 | Gateway 做验签 + 黑名单 + 身份透传 |
| 踢人：auth 内部查 jti | 踢人：通过 `user_token_jti` 映射查到当前 jti |

---

## 三、Auth 模块数据库表结构

所有表均在 `mall_auth` 库中，通过 MyBatis XML 映射：

| 表名 | 对应 Entity | 说明 |
|------|------------|------|
| `auth_user` | `UserEntity` | 用户表（id, avatar_id, email, password, user_name, dept_id, phone, job_id, nick_name, sex, birthday, valid_status, last_login_city, last_login_time, + BaseEntity） |
| `auth_role` | `RoleEntity` | 角色表（id, name, remark, data_scope, level, permission, + BaseEntity） |
| `auth_menu` | `MenuEntity` | 菜单表（id, name, pid, sort, icon, path, hidden, is_link, permission, type, component, url, + BaseEntity） |
| `auth_dept` | `DeptEntity` | 部门表（id, name, pid, valid_status, role_id, + BaseEntity） |
| `auth_job` | `JobEntity` | 岗位表（id, name, sort, dept_id, valid_status, + BaseEntity） |
| `auth_user_role` | `UserRoleEntity` | 用户角色关联（id, user_id, role_id, + BaseEntity） |
| `auth_role_menu` | `RoleMenuEntity` | 角色菜单关联（id, role_id, menu_id, + BaseEntity） |
| `auth_role_dept` | `RoleDeptEntity` | 角色部门关联（id, role_id, dept_id, + BaseEntity） |
| `auth_user_avatar` | `UserAvatarEntity` | 用户头像（id, file_name, path, file_size, + BaseEntity） |

**roles/authorities 数据来源链路：**
```
用户登录/请求鉴权
  → UserDetailsServiceImpl.loadUserByUsername(username)
    → RoleMapper.findRoleByUserId(userId)                    // 查 auth_role
    → MenuMapper.findMenuByRoleIdList(roleIdList)             // 查 auth_menu
    → 合并 RoleEntity.permission + MenuEntity.permission      // 生成 authorities 列表
    → 存入 JwtUserEntity.authorities 和 JwtUserEntity.roles
```

---

## 四、改动文件清单

### A. JWT 工具类辅助（mall-common）

**文件：** `mall-common/src/main/java/cn/net/mall/util/TokenUtil.java`

新增方法，统一 JWT 构建和解析，避免各文件直接依赖 `Jwts.builder()`：

```java
public class TokenUtil {
    // 已有：getTokenForAuthorization() 等

    /**
     * 从请求中解析 JWT claims
     */
    public static Claims parseClaims(HttpServletRequest request, String secret) {
        String token = getTokenForAuthorization(request);
        if (!StringUtils.hasLength(token)) return null;
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 生成带身份 claims 的 JWT
     */
    public static String generateToken(Long userId, String username, List<String> roles,
                                        String secret, int expireSeconds) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(username)
                .claim("user_id", userId)
                .claim("user_name", username)
                .claim("roles", roles)
                .setId(UuidUtil.getUuid())       // jti，用于黑名单
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(expireSeconds)))
                .signWith(key)
                .compact();
    }
}
```

**⚠️ 注意：** `TokenHelper`/`UserTokenHelper` 来自 `mall-redis-spring-boot-starter` JAR，不可修改。`TokenUtil` 自行实现 JWT 构建，使用 `tokenHelper.getTokenSecret()` 获取密钥保持一致。

---

### B. Token 生成改造（mall-auth）

**文件：** `mall-auth/src/main/java/cn/net/mall/auth/service/auth/UserService.java`

改动 3 处 token 生成 + 2 处 token 删除，**对外接口不变**。

#### B1. `login()` 方法（第 369 行）

```java
// 当前：
String token = tokenHelper.generateToken(jwtUserEntity);

// 改为：
String token = TokenUtil.generateToken(
    userEntity.getId(),
    jwtUserEntity.getUsername(),
    roles,
    tokenHelper.getTokenSecret(),
    tokenExpireTimeInRecord
);
```

#### B2. `loginByPhone()` 方法（第 334 行）

```java
// 当前：
String token = tokenHelper.generateToken(jwtUserEntity);

// 改为：
String token = TokenUtil.generateToken(
    userEntity.getId(),
    jwtUserEntity.getUsername(),
    roles,
    tokenHelper.getTokenSecret(),
    tokenExpireTimeInRecord
);
```

#### B3. `register()` 方法（第 543 行）

```java
// 当前：
String token = userTokenHelper.generateToken(userDTO.getUserName(), JSONUtil.toJsonStr(userDTO));

// 改为：
UserDTO userDTO = doRegister(registerDTO);
List<String> roles = new ArrayList<>();  // 注册用户无角色
String token = TokenUtil.generateToken(
    userDTO.getId(),
    userDTO.getUserName(),
    roles,
    userTokenHelper.getTokenSecret(),
    tokenExpireTimeInRecord  // 需要注入此值或默认值
);
userDTO.setToken(token);
```

**问题：** `register()` 用的是 `userTokenHelper`（父类，有 `getTokenSecret()`），且当前 `TokenHelper` 是 `UserTokenHelper` 的子类，需要确认 `userTokenHelper.getTokenSecret()` 是否可见（protected → 同包或子类可访问）。

**备选：** 在 `UserService` 中注入 `@Value("${mall.mgt.tokenSecret}")` 直接获取密钥。

#### B4. `logout()` 方法（第 391 行）

```java
// 当前：
tokenHelper.delToken(token);

// 改为：
// 从 token 中解析 jti 写入黑名单
Claims claims = TokenUtil.parseClaims(token, tokenHelper.getTokenSecret());
if (claims != null) {
    redisUtil.setex("blacklist:" + claims.getId(), 1, tokenExpireTimeInRecord);
}
```

#### B5. `cancelAccount()` 方法（第 412 行）

```java
// 当前：
tokenHelper.delToken(token);

// 改为：
Claims claims = TokenUtil.parseClaims(token, tokenHelper.getTokenSecret());
if (claims != null) {
    redisUtil.setex("blacklist:" + claims.getId(), 1, tokenExpireTimeInRecord);
}
```

---

### C. Gateway AuthFilter 改造（拦人入口）

**文件：** `mall-gateway/src/main/java/cn/net/mall/gateway/filter/AuthFilter.java`

Gateway 增加黑名单检查 + 身份透传 Header：

```java
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // 白名单路径直接放行
    if (isNoAuth(uri.getPath())) return chain.filter(exchange);

    String token = TokenUtil.getToken(request);
    if (StringUtils.hasLength(token)) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

            // 查黑名单（Gateway 直连 Redis，不调 auth）
            String jti = claims.getId();
            if (redisTemplate.hasKey("blacklist:" + jti)) {
                return unauthorized("token 已被踢下线");
            }

            // 透传身份到下游服务
            ServerHttpRequest mutated = request.mutate()
                .header("X-User-Id", String.valueOf(claims.get("user_id")))
                .header("X-User-Name", (String) claims.get("user_name"))
                .header("X-Roles", String.join(",", (List<String>) claims.get("roles")))
                .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        } catch (JwtException e) {
            // 验签失败，交给下游或直接返回 401
            return unauthorized("token 无效");
        }
    }
    return chain.filter(exchange);
}
```

> **注意：** Gateway 需要依赖 `mall-redis-spring-boot-starter` 来操作 Redis。
> 在 `mall-gateway/pom.xml` 中添加该依赖（只加这一个 starter，不引入整个 auth）。

---

### D. AuthApiInterceptor 改造（全部服务）

**文件：** `mall-auth-api-starter/src/main/java/cn/net/mall/auth/interceptor/AuthApiInterceptor.java`

改从 Gateway 透传的 Header 取身份，完全不碰 Redis：

```java
public boolean preHandle(HttpServletRequest request, ...) {
    String userIdStr = request.getHeader("X-User-Id");
    if (StringUtils.hasLength(userIdStr)) {
        Long userId = Long.parseLong(userIdStr);
        String username = request.getHeader("X-User-Name");
        FillUserUtil.setCurrentUser(userId, username);
    }
    return true;
}
```

> 如果 Gateway 没有透传（兼容旧链路），回退到 JWT 解析：
> ```java
> if (userIdStr == null) {
>     String token = TokenUtil.getTokenForAuthorization(request);
>     if (StringUtils.hasLength(token)) {
>         TokenHelper tokenHelper = SpringUtil.getBean("tokenHelper", TokenHelper.class);
>         Claims claims = tokenHelper.getClaimsFromToken(token);
>         ... // 从 claims 取
>     }
> }
> ```

---

### E. JwtTokenFilter 改造（mall-auth）

**文件：** `mall-auth/src/main/java/cn/net/mall/auth/filter/JwtTokenFilter.java`

黑名单检查移到 Gateway 后，auth 自身不再需要检查黑名单。直接从 claims 解析身份：

```java
Claims claims = tokenHelper.getClaimsFromToken(token);
if (claims != null) {
    Long userId = claims.get("user_id", Long.class);
    String username = claims.get("user_name", String.class);
    @SuppressWarnings("unchecked")
    List<String> roles = (List<String>) claims.get("roles");
    JwtUserEntity jwtUserEntity = new JwtUserEntity();
    jwtUserEntity.setId(userId);
    jwtUserEntity.setUsername(username);
    jwtUserEntity.setRoles(roles);
    jwtUserEntity.setAuthorities(roles.stream()
        .map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(jwtUserEntity, null, jwtUserEntity.getAuthorities());
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
    SecurityContextHolder.getContext().setAuthentication(authentication);
}
```

---

### F. AuthHandshakeInterceptor 改造（mall-message）

**文件：** `mall-message/src/main/java/cn/net/mall/message/websocket/AuthHandshakeInterceptor.java`

与 E 相同，从 `userTokenHelper.getClaimsFromToken(token)` 解析 claims，
不再调 `tokenHelper.getUserDetailsFromUsername(username)`。

```java
Claims claims = userTokenHelper.getClaimsFromToken(token);
if (claims != null) {
    Long userId = claims.get("user_id", Long.class);
    String username = claims.get("user_name", String.class);
    // ... 恢复上下文
}
```

> WebSocket 不走 Gateway，所以这里还是需要手动解析 JWT。

---

### G. 踢人下线流程

```java
// mall-auth 新增踢人接口
public void kickOut(Long userId) {
    String jti = redisUtil.get("user_token_jti:" + userId);
    if (jti != null) {
        // 写入黑名单，有效期 = token 剩余有效期
        long ttl = redisUtil.getTtl("user_token_jti:" + userId);
        redisUtil.setex("blacklist:" + jti, 1, ttl);
        redisUtil.del("user_token_jti:" + userId);
    }
}
```

被踢用户的后续请求直接在 Gateway 被拦住，进不了业务服务。

---

## 五、兼容旧 token 策略

在切换期间，存在旧 token（无 `user_id` / `user_name` / `roles` claims）。

### 方案：两阶段过渡

**阶段一（切换后 1 个 token 有效期内）：**

所有消费端增加 fallback：

```java
Claims claims = tokenHelper.getClaimsFromToken(token);
Long userId = claims.get("user_id", Long.class);

if (userId == null) {
    // 旧 token → 走原来的 Redis 查询（兼容过渡）
    String username = claims.getSubject();
    JwtUserEntity userEntity = tokenHelper.getUserDetailsFromUsername(username);
    if (userEntity != null) {
        FillUserUtil.setCurrentUser(userEntity.getId(), userEntity.getUsername());
    }
} else {
    // 新 token → 从 claims 直接取
    ...
}
```

**阶段二（所有旧 token 过期后）：**

删除 fallback 代码，纯 JWT claims 解析。

> 旧 token 过期时间取决于 `tokenExpireTimeInRecord` 配置，一般为 7 天或 30 天。

---

## 六、执行步骤

| 步骤 | 文件 | 内容 | 验证方式 |
|------|------|------|---------|
| 1 | `TokenUtil.java` | 新增 `generateToken()` 和 `parseClaims()` 辅助方法 | 编译通过 |
| 2 | `UserService.java` | login/loginByPhone/register 三处改为 `TokenUtil.generateToken()`，加存 `user_token_jti` | 启动 auth，登录返回新 token |
| 3 | `UserService.java` | logout/cancelAccount 改为写 Redis 黑名单 | 登出后旧 token 被拒 |
| 4 | `AuthFilter.java`（Gateway） | 加黑名单检查 + 透传 Header；加 `mall-redis-spring-boot-starter` 依赖 | Gateway 启动正常 |
| 5 | `AuthApiInterceptor.java` | 从 Gateway Header 取身份；不再调 `getUserDetailsFromUsername` | 全部服务请求正常 |
| 6 | `JwtTokenFilter.java` | 从 claims 解析，Gateway 已拦黑名单，auth 不再重复检查 | auth 接口权限校验正常 |
| 7 | `AuthHandshakeInterceptor.java` | 从 claims 解析 | WebSocket 连接正常 |
| 8 | 旧 token fallback 清理 | 周期过后删除兼容代码 | 线上稳定运行后操作 |

---

## 七、影响范围与风险

| 风险 | 说明 | 应对 |
|------|------|------|
| JWT 体积增大 | roles 列表可能包含较多权限字符串 | 权限数 < 100，影响忽略 |
| roles 变更不及时 | 管理员修改角色权限后，用户需重新登录才能生效 | 接受；关键操作可强制用户重新登录 |
| 黑名单穿透 | Gateway 直连的 Redis 宕机 | Gateway 降级为放行（黑名单非核心路径），业务服务继续工作 |
| Gateway 依赖 Redis | Gateway 引入 redis 依赖，比现在重了 | 相比整条链路省掉的 Redis 查询，Gateway 加一个 EXISTS 是划算的 |
| 旧 token 兼容 | 切换期间存量的旧 token 无新 claims | 加 fallback 查 Redis |
| 密钥一致性 | 各服务 `mall.mgt.tokenSecret` 必须一致 | Nacos common.yaml 统一配置 |

---

## 八、预期收益

| 指标 | 改造前 | 改造后 |
|------|--------|--------|
| 每次请求 Redis 查询次数 | 1 次 × 链路上服务数 | 0 次 |
| 链路 3 个服务时 Redis 次数 | 3 次 | 0 次 |
| 登录产生 Redis 写入 | 1 次（存 JwtUserEntity） | 0 次 |
| 登出产生 Redis 操作 | 1 次 DEL | 1 次 SET（黑名单，TTL 自动过期） |
| 踢人下线产生 Redis 操作 | 1 次 DEL | 1 次 SET（黑名单） |

# JWT 鉴权优化方案

## 现状问题

### 当前流程

```
① 用户登录 → auth 服务生成 JWT（subject=username，无其他 claims）

② 每次请求 → Gateway 验 JWT 签名（不拦截，全放行）
           → 下游服务 AuthApiInterceptor 拦截
              ├─ JWT 解析 → 取出 username
              ├─ Redis 查询 → 根据 username 查出完整 JwtUserEntity
              │               （含 id / username / roles / authorities）
              └─ 设入 Spring Security 上下文
           → 业务代码执行（@PreAuthorize 鉴权、FillUserUtil 取 userId）
```

### 问题

| 问题 | 影响 |
|------|------|
| 每次请求查 Redis | 每个服务多一次网络开销（约 1-5ms） |
| JWT 只存 username | 浪费了 JWT 存 claims 的能力 |
| Gateway 不鉴权 | 安全边界薄弱，全依赖下游服务自行校验 |
| 角色/权限也走 Redis | 虽然这保证了实时性，但和 userId 这种不变数据混在一起查 |

---

## 改造方案

### 目标

**不变的数据放 JWT，可变的数据走 Redis**，减少 Redis 查询次数，同时保持角色/权限的实时性。

### JWT Claims 设计

登录生成 token 时，在 JWT 中放入以下 claims：

| Claim | 来源 | 变更频率 | 说明 |
|-------|------|---------|------|
| `sub` | 登录参数 | 永不 | JWT 标准 subject，存 username |
| `user_id` | 数据库 | 永不 | 用户 ID，目前每次查 Redis 才拿到 |
| `user_name` | 数据库 | 永不 | 用户名（冗余，方便直接取） |
| `iat` | 当前时间 | — | 签发时间 |
| `exp` | 当前时间 + 有效期 | — | 过期时间 |

**不放入 JWT 的**：roles、authorities（权限列表）→ 这些可能随时变化，继续走 Redis。

### 改造后的流程

```
① 用户登录 → auth 服务生成 JWT
              ├─ sub: username
              ├─ user_id: 12345        ← 新增
              └─ user_name: admin      ← 新增

② 每次请求 → Gateway 验 JWT 签名 + 基础鉴权（可选）
           → 下游服务 AuthApiInterceptor 拦截
              ├─ JWT 解析 → 从 claims 直接取出 user_id + username
              │             （不再查 Redis）
              ├─ 设入 Spring Security 上下文
              │   （id/username 直接可用）
              └─ 按需查询 Redis → 仅在需要 @PreAuthorize 鉴权时
                                 才查 roles/authorities（懒加载）
           → 业务代码执行
```

### 改动范围

#### 1. mall-auth：JWT 生成增加 claims

**文件：** `mall-auth/src/main/java/cn/net/mall/auth/service/auth/UserService.java`

在生成 token 的方法中，增加 claims：

```java
// 当前：Jwts.builder().setSubject(username)
// 改为：
Jwts.builder()
    .setSubject(username)
    .claim("user_id", userEntity.getId())
    .claim("user_name", userEntity.getUsername())
```

#### 2. mall-common：TokenUtil 增加 claims 解析方法

**文件：** `mall-common/src/main/java/cn/net/mall/util/TokenUtil.java`

新增方法：

```java
public static Long getUserIdFromToken(String token) {
    Claims claims = parseClaims(token);
    return claims.get("user_id", Long.class);
}

public static String getUserNameFromToken(String token) {
    Claims claims = parseClaims(token);
    return claims.get("user_name", String.class);
}
```

#### 3. mall-auth-api-starter：AuthApiInterceptor 去掉 Redis 查询

**文件：** `mall-auth-api-starter/src/main/java/cn/net/mall/auth/interceptor/AuthApiInterceptor.java`

```java
// 当前：
String username = tokenHelper.getUsernameFromToken(token);
JwtUserEntity userEntity = (JwtUserEntity) tokenHelper.getUserDetailsFromUsername(username);
FillUserUtil.setCurrentUser(userEntity.getId(), userEntity.getUsername());

// 改为：
Long userId = TokenUtil.getUserIdFromToken(token);
String username = TokenUtil.getUserNameFromToken(token);
FillUserUtil.setCurrentUser(userId, username);
// roles/authorities 需要时才查 Redis（懒加载）
```

#### 4. 懒加载 roles/authorities（可选优化）

对于需要 `@PreAuthorize` 鉴权的接口，在鉴权时按需查 Redis 获取 roles/authorities，而不是每次请求都查。

---

## 风险和注意事项

| 风险 | 说明 | 应对 |
|------|------|------|
| 旧 token 没有 user_id claim | 已签发的 token 不包含新 claims | 解析不到时回退到查 Redis（兼容过渡） |
| 多服务 tokenSecret 一致性 | 各服务的 `mall.mgt.tokenSecret` 必须相同 | 已在注意事项中说明，确保 Nacos 统一配置 |
| 懒加载 roles 可能引入首次鉴权延迟 | 第一次鉴权时需要额外查一次 Redis | 影响极小，秒级缓存即可忽略 |

---

## 实施步骤

1. **修改 JWT 生成**（mall-auth）— 增加 user_id / user_name claims
2. **新增 claims 解析方法**（mall-common/TokenUtil）
3. **修改 AuthApiInterceptor**（mall-auth-api-starter）— 从 claims 直接拿 userId/username
4. **兼容旧 token** — 解析不到 claims 时回退查 Redis
5. **编译测试** — 验证全链路正常
6. **可选：懒加载 roles** — 按需查 Redis 替代每次请求都查

预期收益：每次请求省 1 次 Redis 查询，全链路减少 n×1ms 延迟（n = 链路中经过的服务数）。

# JWT 鉴权优化 —— 链路对比分析

## 一、完整请求链路对比

### 旧链路（改前）

以一次涉及 3 个服务的请求为例：

```
                                      ┌─ Redis GET user:admin ─ 完整 JwtUserEntity ─┐
                                      │  (id + username + roles + authorities)      │
客户端 → Gateway ────→ AuthApiInterceptor ──→ mall-product ── Feign ──→ mall-order
         (放行)        (mall-product)                         │
                          │                                   │
                          │                              AuthApiInterceptor
                          │                              (mall-order)
                          │                                   │
                          └───────────────────────────────────┘
                                                  Redis GET user:admin
```

**每多一个服务就多一次 Redis GET，每个 GET 返回完整的 JwtUserEntity JSON。**

### 当前链路（2026-07-07 修复后）

```
Gateway (精确白名单 → JWT 验签 → 透传 Header)
  │
  ├─ AuthApiInterceptor: 优先从 Header 取身份，无 Header 则 TokenUtil 纯 JWT 解析
  │   （不再依赖 TokenHelper / SpringUtil / Redis）
  │
  └─ JwtTokenFilter（仅 admin）: TokenUtil 验签 + Redis 黑名单检查（踢人下线）
```

**各服务独立的 JWT 解析，无需额外 Redis 查询。**

## 二、修复要点

| # | 问题 | 修复 |
|---|------|------|
| 1 | `AuthApiInterceptor` 用 `SpringUtil.getBean("tokenHelper")`，但 SpringUtil 不是 bean | 改为构造器注入 `tokenSecret`，用 `TokenUtil.parseClaimsFromToken()` 直接解析 |
| 2 | Gateway 白名单前缀匹配过宽，保护接口也被放行 | 改为精确路径 + 前缀混合匹配，无 token 保护接口返回 401 |
| 3 | Admin `JwtTokenFilter` 同样依赖 SpringUtil | 改为直接注入 `tokenSecret` + `RedisUtil`，不再使用 SpringUtil |
| 4 | `ArithmeticCaptcha` 依赖 Nashorn，JDK17 移除 | 添加 `nashorn-core` 依赖 |

## 三、当前鉴权链路

```
客户端 → Gateway
  ├─ 白名单路径 → 直接放行（login、getCode、首页数据等）
  └─ 非白名单 → 验证 JWT
                  ├─ 无 token → 401
                  ├─ 无效/过期 token → 401
                  └─ 有效 token → 透传 Header → 下游服务
                                     │
                                     ├─ AuthApiInterceptor: Header → SecurityContext
                                     └─ JwtTokenFilter（admin only）: JWT + Redis 黑名单 → SecurityContext
```

各环节不再依赖 Redis（admin 的 Redis 黑名单除外），TokenHelper 仅在 admin 的踢人下线场景使用。

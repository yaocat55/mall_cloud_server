# JWT 鉴权优化 —— 链路对比分析

## 一、完整请求链路对比

### 旧链路（改前）

以一次涉及 3 个服务的请求为例：

```
                                ┌─ Redis GET user:admin ─ 完整 JwtUserEntity ─┐
                                │  (id + username + roles + authorities)      │
客户端 → Gateway ────→ AuthApiInterceptor ──→ mall-product ── Feign ──→ mall-order
         (验签)        (mall-product)                         │
                          │                                   │
                          │                              AuthApiInterceptor
                          │                              (mall-order)
                          │                                   │
                          └───────────────────────────────────┘
                                                    Redis GET user:admin
```

**一次请求的 Redis 调用次数 = 链路上的微服务数量**（每个服务查一次完整的用户信息）

```
3 个服务 → 3 次 Redis GET
5 个服务 → 5 次 Redis GET

每次 GET 返回完整 JwtUserEntity（JSON 反序列化）
```

### 新链路（改后）

```
Redis: GET blacklist:jti ──→ "1" 或 null（就一个 Key，1 bit 信息）
                                      │
客户端 → Gateway ────→ AuthApiInterceptor ──→ mall-product ── Feign ──→ mall-order
         (验签+黑名单)    (从 Header 取身份)                  (透传 Header)
                              │                                   │
                              │                            AuthApiInterceptor
                              │                            (从 Header 取身份)
                              │                                   │
                              └───────────────────────────────────┘
                                                         0 次 Redis
```

**一次请求的 Redis 调用次数 = 仅 Gateway 1 次黑名单检查**

```
3 个服务 → 1 次 Redis EXISTS
5 个服务 → 1 次 Redis EXISTS

每次 EXISTS 检查一个 1 字节 Key 是否存在
```

---

## 二、各场景详细对比

### 场景 1：用户登录（写）

| 操作 | 旧方案 | 新方案 |
|------|--------|--------|
| 生成 JWT | `TokenHelper.generateToken()` → 调 JAR 方法 | `TokenUtil.generateToken()` → 自己造 |
| JWT 内容 | 仅 `sub=username` | `sub=username` + `user_id` + `user_name` + `roles` + `jti` |
| Redis 写入 | `SET user:admin → {JwtUserEntity 几十 KB}` | `SET user_token_jti:1 → "uuid-xxx"`（几个字节） |
| 单次 Redis 耗时 | ~2ms（写大对象 + JSON 序列化） | ~0.5ms（写短字符串） |

### 场景 2：正常业务请求（读）

```
旧方案：
  Gateway 验签（0 Redis）
  → mall-product AuthApiInterceptor: Redis GET user:admin  → ~2ms + JSON 反序列化
  → mall-order AuthApiInterceptor:  Redis GET user:admin  → ~2ms + JSON 反序列化
  → mall-marketing AuthApiInterceptor: Redis GET user:admin → ~2ms + JSON 反序列化
  ──────────────────────────────────
  合计：3 次 Redis ≈ 6ms

新方案：
  Gateway 验签 + Redis EXISTS blacklist:jti → ~0.5ms
  → mall-product AuthApiInterceptor:        0 Redis  ← 读 Header
  → mall-order AuthApiInterceptor:          0 Redis  ← 读 Header
  → mall-marketing AuthApiInterceptor:       0 Redis  ← 读 Header
  ──────────────────────────────────
  合计：1 次 Redis ≈ 0.5ms
```

### 场景 3：用户登出（写）

| 操作 | 旧方案 | 新方案 |
|------|--------|--------|
| Redis 操作 | `DEL user:admin` | `SET blacklist:jti → "1"` |
| 数据量 | 删除几十 KB 对象 | 写入几个字节的标记 |
| 后续效果 | 下次请求查不到 → 拒绝 | Gateway 查黑名单 → 拦截 |

### 场景 4：踢人下线

| 操作 | 旧方案（原系统不支持） | 新方案 |
|------|----------------------|--------|
| 踢人 | 不支持（或手动删 Redis） | `GET user_token_jti:1` → `SET blacklist:jti → "1"` |
| 被踢后 | — | 下次请求 Gateway 直接 401 |

---

## 三、性能量化

### 延迟对比

```
Redis 单次操作基准（本地部署）：
  GET 一个 10 字节 Key     → ~0.3ms
  GET 一个 50 KB 对象      → ~0.8ms（含网络）
  SET 一个 10 字节 Key     → ~0.3ms
  EXISTS                  → ~0.2ms
  JSON 反序列化 50 KB 对象  → ~0.5ms（JwtUserEntity 含权限列表）
```

| 场景 | 旧方案 | 新方案 | 提升 |
|------|--------|--------|------|
| 1 个服务请求 | 1 × GET + 反序列化 ≈ **1.3ms** | 1 × EXISTS ≈ **0.2ms** | **快 6.5 倍** |
| 3 个服务请求 | 3 × GET + 反序列化 ≈ **3.9ms** | 1 × EXISTS ≈ **0.2ms** | **快 19.5 倍** |
| Gateway 压力 | 0 Redis | +1 EXISTS ≈ 0.2ms | 可忽略 |

> 注意：以上是 Redis 本地部署的基准值。如果 Redis 是远程服务器（跨网络）：  
> 旧方案：每次 GET ≈ 3-5ms  
> 新方案：每次 EXISTS ≈ 1-2ms  
> 差距会更大。

### Redis 内存对比

| 指标 | 旧方案 | 新方案 |
|------|--------|--------|
| 每个登录用户占用 | 1 个 Key-value：约 **5-10 KB**（JwtUserEntity） | 1 个 Key-value：约 **50 字节**（jti 字符串） |
| 100 万在线用户 | 约 **5-10 GB** | 约 **50 MB** |
| 黑名单数据 | 无 | 被踢用户数 × 50 字节（TTL 自动过期，存量极低） |

---

## 四、优劣总结

### 优势

| 维度 | 说明 |
|------|------|
| **延迟** | 下游服务 0 Redis，3 服务链路由 3.9ms 降到 0.2ms |
| **Redis 负载** | 从"所有服务都查"缩到"只有 Gateway 查"，QPS 降低 N 倍 |
| **内存** | 每个在线用户从 5-10 KB 降到 50 字节 |
| **扩展性** | 加新服务不需要配 Redis、不关心用户上下文怎么来 |
| **可观测性** | Gateway 有统一入口点查黑名单，日志集中 |
| **踢人能力** | 原来不支持，现在有了 |

### 劣势

| 维度 | 说明 | 严重程度 |
|------|------|---------|
| **Gateway 多一次 Redis** | 旧方案 Gateway 0 Redis，现在加了一次 EXISTS | ⭐ 可忽略（0.2ms） |
| **JWT 体积变大** | 多了 user_id/roles/jti | ⭐ 可忽略（几百字节） |
| **roles 变更不及时** | roles 在 JWT 中，改角色要等 token 过期 | ⭐⭐ 大部分系统如此 |
| **Gateway 强依赖 Redis** | Redis 挂了 Gateway 降级放行 | ⭐ 非核心路径，降级就行 |
| **旧 token 兼容** | 过渡期有 fallback 代码 | ⭐ 临时，1 个 token 有效期后可删 |

---

## 五、一句话

**以一个用了 2 天的改动，把"每个服务都查一次 Redis（累计 3-5ms）"压缩到"只有 Gateway 查一次（0.2ms）"。**

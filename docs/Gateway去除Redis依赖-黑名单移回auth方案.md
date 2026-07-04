# Gateway 去除 Redis 依赖 & 黑名单检查移回 mall-auth

## 一、背景

### 当前架构（JWT 优化后的设计）

```
请求 → Gateway AuthFilter
         ├─ JWT 验签
         ├─ Redis 查黑名单（EXISTS blacklist:{jti}）
         └─ 透传 X-User-Id / X-User-Name / X-Roles Header
     → 下游服务 AuthApiInterceptor
         ├─ 从 Header 取身份（无需 Redis）
         └─ 设入 SecurityContext
```

Gateway 引入了 `mall-redis-spring-boot-starter`，通过 `RedisUtil` 直连 Redis 查黑名单。

### 问题

| 问题 | 说明 |
|------|------|
| Gateway 是响应式网关（WebFlux） | 引入非响应式 Redis 连接（StringRedisTemplate）与 Gateway 架构冲突 |
| Nacos 上 Gateway 没有 Redis 配置 | 启动时会因 `StringRedisTemplate` 无法创建而报错 |
| 黑名单实际只服务 admin 场景 | 踢人、改角色、账号禁用都是后台管理需求 |
| C 端不需要踢人下线 | 异地登录应发通知提醒（mall-message），而非强制踢下线 |

## 二、设计原则

1. **C 端（消费者）**：不需要黑名单。异地登录场景由 mall-message 做通知提醒（后续实现）。
2. **Admin 端**：需要黑名单。踢人、改角色、禁用账号需要即时生效。黑名单检查放在 auth 服务自身，不扩散到其他服务。
3. **Gateway**：职责缩减为**验签 + 透传 Header**，不再碰 Redis。

## 三、改动后架构

```
请求 → Gateway AuthFilter
         ├─ JWT 验签（签名 + 过期）
         └─ 透传 X-User-Id / X-User-Name / X-Roles Header
     → 下游服务 AuthApiInterceptor
         ├─ 从 Header 取身份（无需 Redis）
         └─ 设入 SecurityContext

     → mall-auth JwtTokenFilter（仅 mall-auth 自身）
         ├─ JWT 解析 + 旧 token fallback
         ├─ Redis 黑名单检查 ← 从 Gateway 移回此处
         └─ 设入 SecurityContext
```

### 关键区别

| 维度 | 改前 | 改后 |
|------|------|------|
| Gateway Redis 依赖 | `mall-redis-spring-boot-starter` | 无 |
| 黑名单检查位置 | Gateway `AuthFilter` | mall-auth `JwtTokenFilter` |
| Gateway 职责 | 验签 + 黑名单 + 透传 | 验签 + 透传 |
| Gateway 启动条件 | 需要 Redis 可用（虽然降级放行） | 完全不需要 Redis |
| C 端适用性 | 黑名单对所有请求生效 | 黑名单仅 admin 接口生效 |
| 异地登录处理 | 可能被误踢 | 由 mall-message 通知（TODO） |

## 四、改动文件

### 4.1 mall-gateway

| 文件 | 改动 |
|------|------|
| `pom.xml` | 删除 `mall-redis-spring-boot-starter` 依赖 |
| `AuthFilter.java` | 删除 `RedisUtil` 字段 + 构造函数注入 + 黑名单检查代码段。保留 JWT 验签 + Header 透传 |
| `application.yml` | 恢复 `RedisAutoConfiguration` 排除（保持原样） |

### 4.2 mall-auth

| 文件 | 改动 |
|------|------|
| `JwtTokenFilter.java` | 在解析 claims 后、设 SecurityContext 前，增加黑名单检查：通过 `SpringUtil.getBean("redisUtil", RedisUtil.class)` 查 `blacklist:{jti}`。Redis 不可用时降级放行 |

### 4.3 不受影响的部分

- `UserService.logout()` / `cancelAccount()` — 已在写黑名单，逻辑不变
- `AuthApiInterceptor`（全部服务）— 不碰 Redis，不变
- `AuthHandshakeInterceptor`（mall-message WebSocket）— 不碰黑名单，不变
- 各业务服务（product、order、pay 等）— 不受影响

## 五、黑名单检查的合理性

为什么黑名单只放在 mall-auth 的 `JwtTokenFilter` 就够了？

Admin 调用的 API 链路：

```
管理后台 → Gateway →  mall-admin-api（BFF） → Feign → mall-auth（JWT 过滤）
                                                    → mall-product
                                                    → mall-order
```

- 所有 admin 的**认证操作**（登录、登出、改角色、查菜单）都经过 `mall-auth`
- `mall-auth` 的 `JwtTokenFilter` 会拦截这些请求做黑名单检查
- admin 如果 token 被踢，在 auth 层面就被拒了，无法操作认证相关功能
- 业务操作（查商品、改订单）虽然可以通过 BFF 直接调 product/order，但没有 auth 的认证能力配合这些操作也有限

如果要做到**全链路黑名单拦截**（踢了 admin 一个接口都调不了），后续可以在 `AuthApiInterceptor` 中通过 Feign 调 auth 服务的校验接口（而非所有服务直连 Redis）。

## 六、异地登录通知（后续 TODO）

C 端消费者场景：用户在不同设备登录时，不踢下线，但需要通知。

```
用户 B 在手机登录
  → auth 服务检测到该账号已有活跃会话
  → 不踢旧会话，但发送 MQ 消息
  → mall-message 消费 MQ 消息
  → 推送站内信 / 短信："你的账号在 XX 设备登录"
```

此功能不在本次改动范围内，仅做记录。

## 七、实施步骤

1. `mall-gateway/pom.xml` — 删 `mall-redis-spring-boot-starter`
2. `mall-gateway AuthFilter.java` — 删 RedisUtil，保留验签 + 透传
3. `mall-gateway application.yml` — 恢复 Redis 排除
4. `mall-auth JwtTokenFilter.java` — 加黑名单检查（Redis 不可用时降级放行）
5. 编译验证：`mvn compile -pl mall-gateway,mall-auth -am`
6. 测试验证：`mvn test -pl mall-auth`
7. Gateway 启动验证

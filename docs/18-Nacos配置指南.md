# Nacos 配置指南

## 命名空间

```
ID: 7af60364-a045-4561-85a3-3f7a69de938d
名称: mall-cloud
```

> 注意：配置推送到 Nacos 时务必指定 namespace 为此 ID，否则会落到 `public` 命名空间下，导致服务读取不到配置。

## 配置架构

所有服务通过 `config.import` 加载 Nacos 配置，采用 **1 份共享 + 1 份独立** 的模式：

```yaml
# application.yml（模板）
spring:
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        namespace: 7af60364-a045-4561-85a3-3f7a69de938d
        group: mall-cloud
        file-extension: yaml
        username: nacos
        password: nacos
  config:
    import:
      - nacos:common.yaml?group=mall-cloud       # 共享配置
      - nacos:mall-xxx-api-dev.yaml               # 服务独立配置
```

### 共享配置（common.yaml）

所有服务自动继承，无需重复声明：

| 配置项 | 说明 |
|--------|------|
| `spring.data.redis` | Redis 连接（database 0 / localhost:6379） |
| `mall.mgt.tokenSecret` | JWT 签名密钥（所有服务共用） |
| `spring.devtools.restart.enabled` | 关闭热重启 |
| `spring.main.allow-bean-definition-overriding` | 允许 Bean 覆盖 |
| `spring.cloud.openfeign.circuitbreaker.enabled` | 开启 Feign 断路器 |
| `resilience4j.circuitbreaker` | 熔断阈值（窗口 10 / 50% 熔断） |
| `resilience4j.retry` | 重试策略（最多 3 次 / 500ms 间隔） |
| `resilience4j.timelimiter` | 超时控制（API 5s / BFF 10s） |

### 服务独立配置

各服务 yml 只保留自身特有的配置（数据库、中间件、业务参数），**不再重复声明 Redis、JWT、devtools、bean-override 等公共项**。

## 配置列表

所有配置在 `mall-cloud` 分组下，`mall-xxx-api-dev.yaml` 命名模式。

| Data ID | 服务 | 主要配置 |
|---------|------|---------|
| `common.yaml` | 共享 | Redis、JWT 密钥、Resilience4j、全局 Spring 行为 |
| `mall-gateway-dev.yaml` | Gateway | 路由规则、Sentinel 限流、白名单 |
| `mall-gateway-flow-rules` | Gateway | Sentinel 流控规则 |
| `mall-admin-api-dev.yaml` | Admin | 数据库、MyBatis、登录过期时间 |
| `mall-admin-bff-dev.yaml` | Admin BFF | timelimiter 10s（BFF 超时放宽） |
| `mall-mobile-bff-dev.yaml` | Mobile BFF | timelimiter 10s（BFF 超时放宽） |
| `mall-basic-api-dev.yaml` | Basic | 数据库、MongoDB、MinIO、RocketMQ、短信密钥 |
| `mall-customer-api-dev.yaml` | Customer | 数据库、MyBatis |
| `mall-product-api-dev.yaml` | Product | 数据库、MongoDB、ES、RocketMQ |
| `mall-marketing-api-dev.yaml` | Marketing | 数据库、Caffeine 缓存、ES |
| `mall-order-api-dev.yaml` | Order | ES、RocketMQ、延迟消息等级 |
| `mall-pay-api-dev.yaml` | Pay | 支付宝沙箱配置 |
| `mall-message-api-dev.yaml` | Message | MyBatis |
| `mall-recommend-api-dev.yaml` | Recommend | RocketMQ |

## 注意事项

1. **`gateway.filter.noAuth`** 白名单使用单行逗号分隔格式，支持前缀匹配（末尾带 `/`）和精确匹配（末尾不带 `/`）
2. 服务独立配置如需覆盖 common.yaml 的值，直接在自己 yml 写相同 key 即可（独立配置优先级更高）
3. 新增服务时，只需在 Nacos 建自己的 yml，common.yaml 无需改动
4. 修改全局 R4 参数（熔断阈值、超时时间等）只需改 common.yaml 一处，重启后所有服务生效

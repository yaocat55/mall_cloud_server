# Nacos 配置指南

## 命名空间

```
ID: 7af60364-a045-4561-85a3-3f7a69de938d
名称: mall-cloud
```

> 注意：配置推送到 Nacos 时务必指定 namespace 为此 ID，否则会落到 `public` 命名空间下，导致服务读取不到配置。

## 配置管理

所有服务的业务配置托管在 Nacos 配置中心，本地 `application.yml` 只保留 Nacos 连接信息和通用设置。

```yaml
# application.yml（模板）
spring:
  application:
    name: mall-xxx-api
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        namespace: 7af60364-a045-4561-85a3-3f7a69de938d
        group: mall-cloud
        file-extension: yaml
        username: nacos
        password: nacos
      discovery:
        server-addr: localhost:8848
        namespace: 7af60364-a045-4561-85a3-3f7a69de938d
        group: mall-cloud
        username: nacos
        password: nacos
  config:
    import: nacos:mall-xxx-api-dev.yaml
```

## 配置列表

所有配置在 `mall-cloud` 分组下，`mall-cloud-dev.yaml` 命名模式。

| Data ID | 服务 | 主要配置 |
|---------|------|---------|
| `mall-gateway-dev.yaml` | Gateway | 路由规则、Sentinel 限流、白名单、JWT 密钥 |
| `mall-admin-api-dev.yaml` | Admin | 数据库、Redis、MyBatis mapper、JWT 密钥 |
| `mall-admin-bff-dev.yaml` | Admin BFF | JWT 密钥 |
| `mall-mobile-bff-dev.yaml` | Mobile BFF | JWT 密钥 |
| `mall-basic-api-dev.yaml` | Basic | 数据库、Redis、MongoDB、MinIO、RocketMQ、短信密钥 |
| `mall-customer-api-dev.yaml` | Customer | 数据库、Redis、MyBatis |
| `mall-product-api-dev.yaml` | Product | 数据库、Redis、MongoDB、ES、RocketMQ |
| `mall-marketing-api-dev.yaml` | Marketing | 数据库、Redis、Caffeine、ES |
| `mall-order-api-dev.yaml` | Order | Redis、ES、RocketMQ |
| `mall-pay-api-dev.yaml` | Pay | 支付宝沙箱配置 |
| `mall-message-api-dev.yaml` | Message | Redis |
| `mall-recommend-api-dev.yaml` | Recommend | Redis、RocketMQ |

## 注意事项

1. **common.yaml 已废弃** — 不再被任何服务引用，内容空置即可
2. `gateway.filter.noAuth` 白名单使用单行逗号分隔格式，支持前缀匹配（末尾带 `/`）和精确匹配（末尾不带 `/`）
3. 数据库连接默认使用 localhost:33081，可通过 Nacos 配置直接修改
4. `application.yml` 中 shared-configs 不再引用 common.yaml

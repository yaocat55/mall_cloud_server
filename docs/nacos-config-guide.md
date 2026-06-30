# Nacos 配置指南

## 配置管理

所有服务的业务配置托管在 Nacos 配置中心，本地 `application.yml` 只保留 Nacos 连接信息。

```yaml
# application.yml（已 gitignored，从 template 复制）
server:
  port: 8021

spring:
  application:
    name: mall-auth-api
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        namespace: your_namespace_id
        group: mall-cloud
        file-extension: yaml
        username: nacos
        password: nacos
        shared-configs:
          - data-id: common.yaml
            group: mall-cloud
            refresh: true
      discovery:
        server-addr: localhost:8848
        namespace: your_namespace_id
        group: mall-cloud
        username: nacos
        password: nacos
  config:
    import: nacos:mall-auth-api-dev.yaml    # 每个服务各自的配置文件
```

### Nacos 配置结构

```
Namespace: your_namespace_id（如 7af60364-a045-4561-85a3-3f7a69de938d）
Group:     mall-cloud

配置文件清单：
  common.yaml                  ← 公共配置，所有服务通过 shared-configs 引用
  mall-auth-api-dev.yaml       ← 认证服务
  mall-basic-api-dev.yaml      ← 基础服务
  mall-product-api-dev.yaml    ← 商品服务
  mall-order-api-dev.yaml      ← 订单服务
  mall-pay-api-dev.yaml        ← 支付服务
  mall-marketing-api-dev.yaml  ← 营销服务
  mall-recommend-api-dev.yaml  ← 推荐服务
  mall-message-api-dev.yaml    ← 消息推送
  mall-gateway-dev.yaml        ← 网关（路由、白名单、Sentinel）
```

> 注意：dataId 带 `-dev` 后缀是当前现状。建议后续统一去掉后缀，使用 namespace 隔离环境。

## 公共配置（common.yaml）

所有服务共享，通过 `shared-configs` 自动引用：

```yaml
# Redis 缓存
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
      timeout: 50000

# JWT 密钥（所有服务共用同一个）
mall:
  mgt:
    tokenSecret: your_jwt_secret_here

# Actuator / Prometheus 监控
management:
  endpoints:
    web:
      exposure:
        include: health,prometheus
  endpoint:
    health:
      show-details: always
```

## 各服务 Nacos 配置清单

### mall-auth-api-dev.yaml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mall_auth?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false
    username: root
    password: your_mysql_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
```

### mall-basic-api-dev.yaml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mall_basic?serverTimezone=Asia/Shanghai
    username: root
    password: your_mysql_password
  data:
    mongodb:
      host: your_mongo_host
      port: 27017
      username: mongodb
      password: your_mongo_password

aliyun:
  sms:
    access-key-id: your_aliyun_key
    access-key-secret: your_aliyun_secret
    sign-name: Java突击队

minio:
  endpoint: http://your_minio_host
  port: 9002
  bucketName: mall-dev
  accessKey: your_minio_key
  secretKey: your_minio_secret

rocketmq:
  name-server: your_rocketmq_host:9876
  producer:
    group: mall-basic-api-producer

spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        model: deepseek-r1:8b
```

### mall-product-api-dev.yaml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mall_product?serverTimezone=Asia/Shanghai
    username: root
    password: your_mysql_password
  data:
    mongodb:
      host: your_mongo_host
      port: 27017
      username: mongodb
      password: your_mongo_password
  elasticsearch:
    host: your_es_host
    port: 9200
    username: elastic
    password: your_es_password
  cache:
    type: caffeine
    caffeine:
      spec: initialCapacity=50,maximumSize=500,expireAfterWrite=60s

rocketmq:
  name-server: your_rocketmq_host:9876
  producer:
    group: mall-product-api-producer
```

### mall-order-api-dev.yaml

```yaml
spring:
  data:
    mongodb:
      host: your_mongo_host
      port: 27017
      username: mongodb
      password: your_mongo_password
  elasticsearch:
    uris: http://your_es_host:9200
    username: elastic
    password: your_es_password

rocketmq:
  name-server: your_rocketmq_host:9876
  producer:
    group: mall-order-api-producer

mall:
  order:
    orderTimeoutDelayLevel: 15    # RocketMQ 延迟等级
```

### mall-pay-api-dev.yaml

```yaml
mall:
  pay:
    alipay:
      protocol: https
      gatewayHost: openapi-sandbox.dl.alipaydev.com
      signType: RSA2
      appId: your_alipay_app_id
      privateKey: your_alipay_private_key
      publicKey: your_alipay_public_key
      notifyUrl: http://your-domain.com/notify
```

### mall-marketing-api-dev.yaml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mall_marketing?serverTimezone=Asia/Shanghai
    username: root
    password: your_mysql_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
```

### mall-recommend-api-dev.yaml / mall-message-api-dev.yaml

较简单，仅含应用名、端口、MyBatis 配置和 ShardingSphere 配置。

### mall-gateway-dev.yaml

网关配置较特殊，含路由规则、CORS、Sentinel 和 JWT 白名单，建议直接从 Nacos 导出。

## 创建指南

```
① 创建命名空间
   命名空间ID: 自动生成（如 7af60364-a045-4561-85a3-3f7a69de938d）
   命名空间名称: mall
   描述: 本地开发环境

② 创建 group
   group: mall-cloud（不需要在 Nacos 上创建，在配置中指定即可）

③ 创建配置
   先创建 common.yaml（公共配置）
   再依次创建各服务的 xxx-api-dev.yaml

④ 复制 template → application.yml
   填入 Nacos 地址、命名空间ID、密码
```

## 本地 application.yml

所有服务的本地 `application.yml` 已被 `.gitignore` 排除，从 `application.yml.template` 复制：

```bash
# 复制模板
for dir in mall-gateway mall-auth mall-basic mall-product mall-order mall-pay \
    mall-marketing mall-recommend mall-message mall-admin-api mall-mobile-api; do
  cp "$dir/src/main/resources/application.yml.template" "$dir/src/main/resources/application.yml"
done
```

> 然后将 `application.yml` 中的 `your_*` 替换为实际的 Nacos 地址、命名空间ID 和密码。所有业务配置（数据库、Redis、ES 等）从 Nacos 拉取，无需写在本地。

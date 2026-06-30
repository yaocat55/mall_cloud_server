# Nacos 配置指南

## 配置管理

所有服务的业务配置托管在 Nacos 配置中心，本地只保留 Nacos 连接信息。通过环境变量切换环境。

```yaml
# application.yml.template — 提交到仓库，CI/CD 使用
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}         # 环境变量切换 dev/prod
  cloud:
    nacos:
      config:
        server-addr: ${NACOS_ADDR:your_nacos_host:8848}
        namespace: ${NACOS_NAMESPACE:mall}         # 不同环境不同命名空间
  config:
    import: nacos:${spring.application.name}.yaml   # dataId 固定，namespace 隔离
```

### 环境切换

| 变量 | 本地 dev（不设） | 生产 |
|------|----------------|------|
| `SPRING_PROFILES_ACTIVE` | `dev`（默认） | `prod` |
| `NACOS_NAMESPACE` | `mall`（默认） | `mall-prod` |
| Nacos dataId | `mall-auth-api.yaml`（不变） | `mall-auth-api.yaml`（不变） |

### 创建指南

在 Nacos 控制台先建 namespace、再建配置：

```
① 创建命名空间
   命名空间ID: mall（自动生成，不填则由 Nacos 分配）
   命名空间名称: mall
   描述: 本地开发环境

② 创建 group
   默认使用 mall-cloud，不需要在 Nacos 上创建，在配置中指定即可

③ 创建配置（每个服务一条）
   以 mall-auth 为例：
     Data ID:    mall-auth-api.yaml
     Group:      mall-cloud
     配置格式:   YAML
     配置内容:   根据下面每张表复制

④ 多环境方案（企业级做法）
   生产环境再建一个 namespace：mall-prod（命名空间ID 不同）
   同一份 dataId，不同 namespace，配置内容不同
   启动时设 NACOS_NAMESPACE=mall-prod 完成切换
```

> 强烈建议 dataId 不加 `-dev`/`-prod` 后缀，用 namespace 隔离环境。

**本地开发：** 复制 template → `application.yml`（gitignored），填入 Nacos 地址和密码即可。所有数据库、Redis 等配置从 Nacos 拉取，无需写在本地。

> ⚠️ 如果本地 Nacos 中已有 `mall-xxx-api-dev.yaml`（带 `-dev` 后缀），在复制 template 后将 `config.import` 改回 `mall-xxx-api-dev.yaml` 保持兼容，后续再迁移到无后缀命名。

## Nacos 配置清单

本地 `application.yml` 使用 `spring.config.import` 直连 Nacos 拉取配置。公共配置（Redis、JWT密钥、Actuator）抽取到 `common.yaml` 统一管理，各服务通过 `shared-configs` 自动引用，无需重复配置。

本地 Nacos 建议统一 namespace 为 `mall`，Group 为 `mall-cloud`。每个服务一个 dataId，内容根据模板中的 `your_*` 占位符配置：

| 服务 | 服务说明 | Nacos dataId | 需配置的内容 |
|------|---------|-------------|-------------|
| — | **公共配置（所有服务共享）** | `common.yaml` | Redis、JWT tokenSecret、Actuator/Prometheus 监控 |
| mall-gateway | 统一网关入口：路由转发、CORS、Sentinel 流控 | `mall-gateway-dev.yaml` | 无（仅网关特有配置） |
| mall-auth | 用户与权限管理：登录注册、RBAC 权限、收货地址管理、鉴权 Starter | `mall-auth-api-dev.yaml` | 数据源(datasource) |
| mall-basic | 基础服务：字典、区域、文件(MinIO)、短信、敏感词、定时任务(Quartz)、Ollama AI | `mall-basic-api-dev.yaml` | 数据源、MongoDB、MinIO、阿里云短信 |
| mall-product | 商品中心：商品CRUD、分类/品牌/属性、购物车、MySQL+ES双写、首页管理 | `mall-product-api-dev.yaml` | 数据源、ES、RocketMQ |
| mall-order | 订单交易：下单→支付→发货→收货→评价 全生命周期、ShardingSphere(8库)、ES搜索 | `mall-order-api-dev.yaml` | ES、RocketMQ、ShardingSphere |
| mall-pay | 支付服务：支付宝沙箱对接、二维码生成(ZXing)。**无数据库** | `mall-pay-api-dev.yaml` | 支付宝参数(沙箱appId/密钥) |
| mall-marketing | 营销中心：优惠券发放/核销、秒杀商品、优惠金额试算 | `mall-marketing-api-dev.yaml` | 数据源 |
| mall-recommend | 智能推荐：商品推荐(Mahout)、收藏管理、浏览历史、ShardingSphere(8库) | `mall-recommend-api-dev.yaml` | RocketMQ、ShardingSphere |
| mall-message | 消息推送：WebSocket + STOMP 实时推送、站内通知、ShardingSphere(8库) | `mall-message-api-dev.yaml` | ShardingSphere |

## 配置文件示例

```yaml
# Nacos → mall 命名空间 → mall-cloud group → dataId: mall-auth-api-dev.yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mall_auth?useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

> 公共配置（Redis、JWT密钥、Actuator）已统一在 `common.yaml` 中通过 `shared-configs` 引用，各服务的 dataId 中不再需要重复填写。每个 `application.yml.template` 里标记为 `your_*` 的占位符，均在 Nacos 上配置。

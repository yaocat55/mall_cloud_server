# 多仓库拆分方案

当前为单仓库（Monorepo），适合 1-2 个团队协作。若团队规模扩大，可按以下方案拆分。

## 方案一：10 仓库（细粒度）

每个团队独立负责一个服务 + 其 client JAR，通过 Nexus/Artifactory 发布版本。

| 仓库 | 包含模块 | 团队 |
|------|---------|------|
| `mall-common` | mall-common | 基建团队 |
| `mall-gateway` | mall-gateway | 基建团队 |
| `mall-auth` | mall-auth + mall-auth-client + mall-auth-api-starter | 权限团队 |
| `mall-basic` | mall-basic + mall-basic-client | 基础服务团队 |
| `mall-product` | mall-product + mall-product-client | 商品团队 |
| `mall-marketing` | mall-marketing + mall-marketing-client | 营销团队 |
| `mall-order` | mall-order + mall-order-client | 订单团队 |
| `mall-pay` | mall-pay + mall-pay-client | 支付团队 |
| `mall-recommend` | mall-recommend | 推荐团队 |
| `mall-message` | mall-message | 消息团队 |

### 版本管理规则

- **加字段** → 小版本升级（1.0.0 → 1.1.0），消费方无需改代码
- **改/删字段** → 大版本升级（1.0.0 → 2.0.0），消费方必须配合

### 协作成本

```
mall-product-client v1.0.1 ──→ Nexus
                                    │
                    ┌───────────────┴───────────────┐
                    ▼                               ▼
          mall-order（更新依赖版本）        mall-recommend（保持旧版本）
```

每个团队独立发布 client 版本，消费方按需升级。`mall-order-client` 依赖 3 个上游 client（product、auth、marketing），订单团队需要关注 3 个上游的版本变更。

## 方案二：3 仓库（折中）

将耦合紧密的服务放在同一仓库，降低版本协调成本。

| 仓库 | 包含模块 | 说明 |
|------|---------|------|
| `mall-foundation` | mall-common + mall-gateway + mall-auth | 基础设施，变动少，稳定版本 |
| `mall-business` | mall-basic + mall-product + mall-order + mall-pay + mall-marketing + 各自 client | 核心业务，联动频繁，放一起省去跨仓库版本协调 |
| `mall-enhancement` | mall-recommend + mall-message | 边缘业务，独立迭代 |

## 版本管理规则

| 方案 | 适用阶段 |
|------|---------|
| 当前 Monorepo | 1-2 个团队，快速迭代 |
| 3 仓库 | 2-4 个团队（推荐过渡方案） |
| 10 仓库 | 5 个以上独立团队 |

> 建议先走 3 仓库，等团队扩张到自然边界时再拆 10 仓库。过早拆分会增加版本管理和 CI/CD 成本，而非提高效率。

# Resilience4j Nacos 配置指南

> 熔断降级（Circuit Breaker）+ 重试（Retry）+ 超时控制（TimeLimiter）
> 配合 `common-web` 中的 `FeignErrorDecoder` + `FallbackFactory` 使用

---

## 1. 总览

| 服务 | 注册名 | 需配熔断 | 需配限流 |
|------|--------|---------|---------|
| `mall-mobile-bff` | `mall-mobile-bff` | ✅ | ⏳ 按需 |
| `mall-admin-bff` | `mall-admin-bff` | ✅ | ❌ |
| `mall-product-api` | `mall-product-api` | ✅ | ❌ |
| `mall-order-api` | `mall-order-api` | ✅ | ❌ |
| `mall-pay-api` | `mall-pay-api` | ✅ | ❌ |
| `mall-customer-api` | `mall-customer-api` | ✅ | ❌ |
| `mall-marketing-api` | `mall-marketing-api` | ✅ | ❌ |
| `mall-basic-api` | `mall-basic-api` | ✅ | ❌ |
| `mall-admin-api` | `mall-admin-api` | ✅ | ❌ |
| `mall-message-api` | `mall-message-api` | ✅ | ❌ |
| `mall-recommend-api` | `mall-recommend-api` | ✅ | ❌ |

> `mall-gateway` 不做 Feign 调用，无需配置。

---

## 2. 统一配置（所有服务通用）

所有服务都用同一套配置。差别只有一个：**`timeout-duration` 按需调整**——BFF 层（需要聚合多个 Feign 调用）给长一点，内部 API 可以短一点。

```yaml
# Nacos — 以下每个服务独立的 yml 都配同一份
# mall-product-api.yml / mall-order-api.yml / mall-pay-api.yml
# mall-customer-api.yml / mall-marketing-api.yml / mall-basic-api.yml
# mall-admin-api.yml / mall-message-api.yml / mall-recommend-api.yml
# mall-mobile-bff.yml / mall-admin-bff.yml

spring:
  cloud:
    openfeign:
      circuitbreaker:
        enabled: true

resilience4j:
  circuitbreaker:
    configs:
      default:
        sliding-window-size: 10            # 统计最近 10 次调用
        minimum-number-of-calls: 5           # 最少调用次数
        permitted-number-of-calls-in-half-open-state: 3
        wait-duration-in-open-state: 10s    # 熔断后 10s 进入半开
        failure-rate-threshold: 50          # 50% 失败则熔断
        slow-call-duration-threshold: 2s
        slow-call-rate-threshold: 50

  retry:
    configs:
      default:
        max-attempts: 3                     # 含首次调用，即重试 2 次
        wait-duration: 500ms
        retry-exceptions:
          - feign.RetryableException

  timelimiter:
    configs:
      default:
        # ⚠️ 根据服务类型调整
        # API 服务（product/order/pay/...）：5s
        # BFF 服务（mobile-bff / admin-bff）：10s（需聚合多个调用）
        timeout-duration: 5s
```

### timeout-duration 参考

| 服务类型 | 推荐值 | 原因 |
|---------|--------|------|
| 内部 API（product / order / pay 等） | `5s` | 单个 Feign 调用，不需要等太久 |
| BFF（mobile-bff / admin-bff） | `10s` | 需要聚合多个 Feign 调用，预留缓冲 |

---

## 3. 限流（仅 mobile-bff 按需开启）

```yaml
# Nacos — mall-mobile-bff.yml（按需追加）

resilience4j:
  ratelimiter:
    configs:
      default:
        limit-for-period: 100
        limit-refresh-period: 1s
        timeout-duration: 0
```

> ⚠️ **限流的本质是拒绝请求**，确认前端/用户体验能接受"请求太频繁"的响应后再开启。

---

## 4. FeignClient 实例级调优（可选）

默认配置对所有 Feign 调用生效。如需对特定 FeignClient 调整：

```yaml
resilience4j:
  circuitbreaker:
    instances:
      productFeignClient:
        base-config: default
        failure-rate-threshold: 30   # 商品链更敏感
      orderFeignClient:
        base-config: default
        wait-duration-in-open-state: 30s
```

FeignClient 的 `contextId` 即实例名：

| FeignClient | contextId | 所在模块 |
|------------|-----------|---------|
| `UserFeignClient` | `userFeignClient` | mall-admin-client |
| `OrderFeignClient` | `orderFeignClient` | mall-order-client |
| `ProductFeignClient` | `productFeignClient` | mall-product-client |
| `DictFeignClient` | `dictFeignClient` | mall-basic-client |
| `MemberFeignClient` | `memberFeignClient` | mall-customer-client |
| `MarketingFeignClient` | `marketingFeignClient` | mall-marketing-client |
| `SmsFeignClient` | `smsFeignClient` | mall-basic-client |
| `SmsRecordFeignClient` | `smsRecordFeignClient` | mall-basic-client |

---

## 5. 生效验证

```bash
# 1. 模拟调用失败
curl http://localhost:8030/v1/auth/user/findById?id=-1

# 2. 查看日志，确认 FallbackFactory 生效
# 预期输出：[降级] findById 返回 null
```

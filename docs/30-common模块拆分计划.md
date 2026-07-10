# mall-common 模块拆分方案

> 本文档记录 mall-common 的现状分析、服务依赖关系、以及模块化拆分计划。
> 编写日期：2026-07-10

---

## 一、现状与问题

`mall-common` 当前是一个**上帝模块**，所有微服务都直接依赖它。它包含了从基础实体、工具类、安全认证到敏感词过滤、ID 生成等各色功能，共 **66 个 Java 文件**，分布在 18 个子包中。

**核心问题：** 任何一个微服务只要想用 `BaseEntity`，就必须引入整个 mall-common，连带拉入它不需要的 `RedisUtil`、`FillUserUtil`、敏感词拦截器等。导致编译耗时增长、依赖关系模糊、模块边界不清。

---

## 二、mall-common 现有子包一览

| 子包 | 核心类 | 功能 |
|------|--------|------|
| `entity` | `BaseEntity`, `ResponsePageEntity`, `RequestPageEntity`, `JwtUserEntity` | 基础实体 |
| `util` | `ApiResult`, `AssertUtil`, `TokenUtil`, `FillUserUtil`, `DateUtil` 等 17 个 | 通用工具 |
| `exception` | `BusinessException` | 业务异常 |
| `redis` | `RedisUtil`, `TokenHelper`, `UserTokenHelper` | Redis 操作 + Token 管理 |
| `workid` | `IdGenerateHelper`, `SnowFlakeIdWorker`, `WorkIdAllocator` | 分布式 ID 生成 |
| `annotation` | `MaxMoney`, `MinMoney`, `ValidPhone`, `Sensitive` | 自定义注解 |
| `mapper` | `BaseMapper` | MyBatis 基类 Mapper |
| `service` | `BaseService` | 通用 Service 基类 |
| `json` | `JacksonMapper` | JSON 工具 |
| `handler` | `GlobalApiResultHandler`, `GlobalExceptionHandler` | 全局响应用于处理/异常处理 |
| `filter` | `RequestLogFilter` | 请求日志过滤器 |
| `interceptor` | `ValidSensitiveWordAspect` | 敏感词拦截 AOP |
| `constant` | `KeyConstant`, `NumberConstant`, `TraceConstant` | 常量定义 |
| `enums` | `SmsTypeEnum`, `PhotoTypeEnum`, `SensitiveWordTypeEnum` | 枚举 |
| `sensitive` | `CustomMaskService`, `ICustomMaskService` | 敏感词脱敏 SPI |
| `valid` | `PhoneValidator`, `MaxMoneyConstraintValidator` | 参数校验器 |
| `decoder` | `FeignResultDecoder` | Feign 结果解码 |
| `config` | 多个 `AutoConfiguration` | 自动装配配置 |
| `mybatis` | `UserInterceptor` | MyBatis 拦截器 |

---

## 三、各微服务对 mall-common 的实际依赖

```
                 entity util exception redis workid anno mapper service json filter enums constant
mall-basic        ✅    ✅    ✅     ✅    ✅    ✅    ✅     ✅     ✅    ✅    ✅
mall-customer     ✅    ✅    ✅     ✅    ✅                        ✅
mall-admin        ✅    ✅    ✅     ✅    ✅    ✅    ✅     ✅     ✅    ✅    ✅
mall-product      ✅    ✅    ✅     ✅    ✅    ✅    ✅     ✅     ✅    ✅    ✅    ✅
mall-order        ✅    ✅    ✅     ✅    ✅    ✅    ✅     ✅     ✅
mall-marketing    ✅    ✅    ✅     ✅          ✅    ✅     ✅     ✅         ✅
mall-message      ✅    ✅           ✅          ✅    ✅     ✅     ✅
mall-pay          ✅    ✅
mall-recommend    ✅    ✅           ✅          ✅
mall-gateway            ✅
mall-admin-bff    ✅    ✅
mall-mobile-bff   ✅    ✅
```

### 依赖分析

- **`entity` + `util`** — 12 个微服务全部依赖 → **核心依赖，必须保留在 base 层**
- **`exception`** — 8 个后端业务服务依赖 → 核心依赖
- **`redis`** — 8 个服务依赖（BFF 层不依赖）→ 可独立
- **`workid`** — 6 个服务依赖 → 可独立
- **`annotation`** — 6 个服务依赖 → 与 validator 绑定
- **`mapper` + `service`** — 后端数据库服务依赖 → 与 mybatis 绑定，可独立
- **`json`** — 后端服务间接依赖 → 依附于 config/decoder
- **`filter` + `handler`** — 后端 HTTP 服务依赖 → 属于 Web 层
- **`enums`** — 6 个服务依赖 → 但部分枚举是领域相关的（`SmsTypeEnum` 属于 basic，`PhotoTypeEnum` 属于 basic）
- **`constant`** — 仅 mall-product 依赖

---

## 四、目标模块划分

按功能域拆分，所有模块以 `common-` 为前缀（不带 `mall`），使其不限于本项目使用。

### 4.1 模块定义

| 新模块 | 职责 | 包含内容 |
|--------|------|----------|
| **common-core** | 纯基础 — 任何项目都可引 | `BaseEntity`, `ResponsePageEntity`, `RequestPageEntity`, `BusinessException`, `ApiResult`, `ApiResultUtil`, `AssertUtil`, 通用工具类（`DateFormatUtil`, `UuidUtil`, `IpUtil`, `BigDecimalUtil`, `SpringUtil`, `RandomUtil` 等）, 常量（`NumberConstant`, `KeyConstant`）, 基础注解 + 校验器 |
| **common-mybatis** | MyBatis 底座 | `BaseMapper`, `BaseService`, `UserInterceptor` |
| **common-web** | Web 层基础设施 | `GlobalExceptionHandler`, `GlobalApiResultHandler`, `RequestLogFilter`, `WebAutoConfiguration` |
| **common-security** | 认证授权工具 | `JwtUserEntity`, `FillUserUtil`, `TokenUtil`, `TokenHelper`, `UserTokenHelper` |
| **common-redis** | Redis 操作 | `RedisUtil`（可考虑留在 core 或独立） |
| **common-workid** | 分布式 ID | `SnowFlakeIdWorker`, `WorkIdAllocator`, `IdGenerateHelper` |
| **common-sensitive** | 敏感词过滤 | `ValidSensitiveWordAspect`, `CustomMaskService`, `ICustomMaskService`, `SensitiveAutoConfiguration` |

### 4.2 领域枚举处理

当前的 `SmsTypeEnum`、`PhotoTypeEnum` 等是领域相关的枚举，不应放在 common 中：

| 枚举 | 归属 | 说明 |
|------|------|------|
| `SmsTypeEnum` | `mall-basic-client` | 短信类型 |
| `PhotoTypeEnum` | `mall-basic-client` | 图片类型 |
| `SensitiveWordTypeEnum` | `common-sensitive` | 敏感词类型 |
| `SensitiveTypeEnum` | `common-sensitive` | 脱敏类型 |
| `JobResult` | `mall-basic-client` | 任务结果 |

---

## 五、拆分计划（分阶段执行）

### Phase 1 — 新建模块 + 迁移静态无依赖类

> 风险最低的阶段：只迁纯工具类、不含任何自动配置和 bean。

**新建 `common-core`，从 mall-common 迁入：**
- `entity/BaseEntity.java`, `RequestPageEntity`, `ResponsePageEntity`
- `util/ApiResult.java`, `ApiResultUtil.java`, `AssertUtil.java`
- `util/DateFormatUtil.java`, `UuidUtil.java`, `IpUtil.java`, `BigDecimalUtil.java`
- `util/SpringUtil.java`, `RandomUtil.java`, `Md5Util.java`, `BetweenTimeUtil.java`
- `util/TraceIdUtil.java`, `OrderCodeUtil.java`, `SmsKeyUtil.java`
- `util/SignUtil.java`, `ExcelUtil.java`
- `constant/NumberConstant.java`, `KeyConstant.java`, `TraceConstant.java`
- `exception/BusinessException.java`
- `annotation/*`, `valid/*`

**更新各服务 pom.xml：**
- 所有服务把 `mall-common` 依赖改为 `common-core`

### Phase 2 — 拆分 common-mybatis + common-web

**新建 `common-mybatis`：** `BaseMapper`, `BaseService`, `UserInterceptor`
- 影响：所有后端业务服务（basic, admin, product, order, marketing, message, customer）

**新建 `common-web`：** `GlobalExceptionHandler`, `GlobalApiResultHandler`, `RequestLogFilter`, `FeignResultDecoder`
- 影响：有 HTTP 接口的服务

### Phase 3 — 拆分 common-security + common-redis

**新建 `common-security`：** `JwtUserEntity`, `FillUserUtil`, `TokenUtil`, `TokenHelper`, `UserTokenHelper`
- 注意：`FillUserUtil` 强依赖 Spring Security 的 `SecurityContextHolder`
- 影响：mall-admin, mall-customer, mall-product, mall-order, mall-marketing

**新建 `common-redis`：** `RedisUtil`
- 影响：所有使用 Redis 的服务

### Phase 4 — 拆分 common-workid + common-sensitive + 清理

**新建 `common-workid`：** `SnowFlakeIdWorker`, `WorkIdAllocator`, `IdGenerateHelper`
- 影响：6 个服务

**新建 `common-sensitive`：** `ValidSensitiveWordAspect`, `CustomMaskService` 等
- 影响：mall-basic（当前唯一使用敏感词的服务）

**最后清理：** 删除 `mall-common` 模块

---

## 六、迁移后的依赖关系示例

```
mall-basic（Phase 2 后）
├── common-core
├── common-mybatis
├── common-web
├── common-workid
└── mall-basic-client（自有的 client）

mall-customer（Phase 2 后）
├── common-core
├── common-mybatis
├── common-security（因为用了 FillUserUtil / TokenUtil）
└── mall-customer-client

mall-admin-bff（Phase 2 后）
├── common-core
├── common-web
└── 各业务 client（Feign 调用）
```

> BFF 不需要 common-mybatis、common-security，因为它只做转发，不直连数据库、不处理本地认证。

---

## 七、注意事项

1. **`mall-common` 的 AutoConfiguration** — 需要逐个检查是否要在新模块中重建，还是可以直接删除（部分配置可能已被 starter 替代）
2. **`JacksonMapper` / `FeignResultDecoder`** — 两者耦合，需一起迁入 common-web
3. **`SmsKeyUtil`** — 虽然工具类，但语义上属于短信领域，可考虑迁入 mall-basic-client
4. **`ExcelUtil`** — 通用工具，留在 core 或迁入 common-web 均可
5. **兼容性** — Phase 1 完成后，旧 `mall-common` 可保留作为桥接模块，内部直接引用 `common-core`，避免一次性改所有服务的 pom

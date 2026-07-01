# Mall Cloud 架构审查报告

> 基于项目 README.md、模块 POM 依赖树及 mall-common 源码分析

---

## 核心结论

**mall-common 已退化为"上帝模块"（God Module）**，这是本项目从"单体应用"向"微服务"迁移过程中最大的架构债务。它的存在导致 8 个微服务在 JAR 包层面、依赖层面、业务实体层面全部耦合在一起，使得本项目的"微服务"实际上沦为了同一份代码的不同进程副本。

---

## 一、mall-common 的"罪行清单"

### 1.1 它装了什么？（按严重程度排序）

| 类别 | 内容 | 问题 |
|------|------|------|
| **业务实体** | `AuthUserEntity`, `JwtUserEntity`, `CaptchaEntity`, `TokenEntity`, `ESSeckillProductEntity`, `SeckillProductDetailEntity`, `UserSeckillProductTradeEntity` | 业务实体泄漏到所有服务 |
| **业务枚举** | `OrderStatusEnum`, `PayStatusEnum`, `SensitiveTypeEnum`, `SensitiveWordTypeEnum` | 业务知识污染全局命名空间 |
| **框架配置** | `RedisConfig`, `FeignDecoderConfig`, `JacksonMapper` | 强制所有服务加载统一配置 |
| **AOP 拦截器** | `ValidSensitiveWordAspect`, `UserInterceptor`, `RequestLogFilter` | 全局拦截器无差别生效 |
| **安全相关** | `@NoLogin`, `TokenHelper`, `UserTokenHelper`, `FillUserUtil`, `spring-security-core` | 安全模型耦合进每个服务 |
| **持久化基类** | `BaseMapper`, `BaseService`, `BaseEntity`, `EsBaseEntity`, `mybatis-spring-boot-starter`, `mysql-connector-j` | 每个服务都被迫携带数据库驱动 |
| **重型中间件** | `redisson-spring-boot-starter`, `spring-cloud-starter-openfeign` | 即使不需要也要加载 |

### 1.2 依赖膨胀全景

```
mall-common/pom.xml 引入的依赖：
├── spring-boot-starter-web          ← Web 容器（每个服务都带 Tomcat）
├── spring-boot-starter-aop           ← AOP
├── mybatis-spring-boot-starter       ← ORM 框架
├── mysql-connector-j                 ← MySQL 驱动
├── redisson-spring-boot-starter      ← 分布式锁/缓存客户端
├── spring-security-core              ← 安全框架
├── spring-cloud-starter-openfeign    ← Feign HTTP 客户端
├── feign-okhttp                      ← HTTP 连接池
├── easyexcel                         ← Excel 处理
├── hutool-all / hutool-core          ← 工具包（两个版本并存）
├── fastjson                          ← JSON 库
├── jjwt                              ← JWT 库
├── springdoc-openapi                 ← API 文档
├── jackson-datatype-jsr310           ← 时间序列化
├── apm-toolkit-trace                 ← SkyWalking APM
├── apm-toolkit-logback-1.x           ← SkyWalking 日志
├── apm-toolkit-opentracing           ← SkyWalking 链路追踪
└── jakarta.validation-api            ← 校验
```

**每个微服务的 fat JAR 都完整携带了上面所有依赖。**

---

## 二、具体案例：mall-pay 的悲剧

`mall-pay` 在 README 中被标注为 **"无数据库"** 的服务——它只需要处理支付宝回调，通过 Feign 调用 order 服务更新订单状态。但它的 POM 因为依赖了 mall-common，实际携带了：

```
mall-pay 实际依赖链：
mall-pay
  ├── mall-common
  │   ├── spring-boot-starter-web         ← 带了嵌入 Tomcat
  │   ├── mybatis-spring-boot-starter     ← 带了 MyBatis（它根本不用）
  │   ├── mysql-connector-j               ← 带了 MySQL 驱动（它不需要数据库）
  │   ├── redisson-spring-boot-starter    ← 带了 Redis 客户端（它不需要 Redis）
  │   ├── spring-security-core            ← 带了 Security 框架（它不需要）
  │   ├── easyexcel                       ← 带了 Excel 库
  │   └── ...
  └── mall-order-client（这才是它真正需要的）
```

**后果：一个不需要数据库的服务，JAR 包中塞满了数据库驱动和 ORM 框架。** 这是依赖管理失控的典型症状。

---

## 三、网关的"排异反应"

`mall-gateway` 基于 Spring Cloud Gateway（WebFlux 响应式栈），与 `spring-boot-starter-web`（Servlet 栈）**冲突**。

网关的 POM 不得不这样做：

```xml
<dependency>
    <groupId>cn.net.mall</groupId>
    <artifactId>mall-common</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>   <!-- 必须排除 -->
        </exclusion>
        <exclusion>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId> <!-- 必须排除 -->
        </exclusion>
    </exclusions>
</dependency>
```

**一个"公共模块"的依赖居然会和网关框架冲突，这说明它根本不适合作为公共模块。**

---

## 四、版本管理的失控信号

| 问题 | 位置 | 详情 |
|------|------|------|
| 版本不一致 | hutool | 根 POM 管理 `hutool-all 5.8.7`，但 common 和 auth 中硬编码 `hutool-core 5.8.4` |
| 版本覆盖 | fastjson | 根 POM 管理 `1.2.83`，但 auth 中硬编码 `1.2.76` |
| 依赖重复 | hutool | common 中同时依赖了 `hutool-core 5.8.4` 和 `hutool-all`（通过父 POM 管理版本） |
| 未管理版本 | springdoc, jjwt | 在 common 中直接声明版本号，未通过父 POM 的 dependencyManagement 统一管理 |

---

## 五、为什么这对分布式微服务是致命的

你的直觉完全正确。问题不在于"common 模块大"，而在于：

### 5.1 服务边界被击穿

在真正的微服务架构中，每个服务的领域模型（Entity、Enum、VO）应该封装在服务内部。但 mall-common 将 `OrderStatusEnum`、`PayStatusEnum`、`SeckillProductDetailEntity` 等业务实体暴露给了所有服务。

这意味着：
- product 服务可以直接引用 `OrderStatusEnum` → 订单和商品的领域边界消失
- 修改 `AuthUserEntity` → 所有服务都需要重新构建和部署
- 任何服务的代码都可能被其他服务的实体"不经意"地耦合

### 5.2 独立部署能力归零

微服务的核心价值是独立部署、独立扩展。但 mall-common 的依赖捆绑导致：

- 升级 MySQL 驱动版本 → 8 个服务全部重建
- 修复安全漏洞 → 8 个服务全部重建
- 添加一个新依赖 → 8 个服务全部重建

### 5.3 异构技术栈被锁死

你永远无法用另一个语言或框架重写某个服务，因为所有服务的公共代码都锁死在 Java + Spring Boot + MyBatis 这个技术栈里。

### 5.4 启动时间和资源浪费

每个服务启动时都会加载大量不需要的 Bean：
- MyBatis 自动配置 → 尝试连接数据库（mall-pay 会报数据源找不到）
- Redisson 自动配置 → 尝试连接 Redis
- Security 自动配置 → 加载安全过滤器链

---

## 六、推荐的拆分方案

### 将 mall-common 拆解为 4 个独立模块

```
mall-common 现状                     拆分方案
─────────────────                   ─────────
                                    mall-common-core
├── ApiResult                          ├── ApiResult / ApiResultUtil
├── BaseEntity / EsBaseEntity          ├── BaseEntity / EsBaseEntity
├── 工具类 (SnowFlake, MD5, Ip...)     ├── 纯工具类（无第三方依赖）
├── AssertUtil / DateFormatUtil        ├── AssertUtil / DateFormatUtil
├── BusinessException                  └── BusinessException
│
├── RedisConfig / RedisUtil           mall-common-redis
├── IdGenerateHelper/IdGenerateConfig  ├── Redis 配置 + 工具类
├── Redisson                           └── Redisson 依赖
│
├── BaseMapper / BaseService          mall-common-mybatis
├── MyBatis 拦截器                      ├── MyBatis 基类
├── mysql-connector-j                  └── MySQL 驱动
│
├── TokenHelper / TokenUtil           mall-common-security
├── FillUserUtil                       ├── JWT Token 工具
├── @NoLogin                           ├── 用户上下文
├── spring-security-core               ├── 安全注解
└── 认证相关 Entity                     └── 认证 SDK 集成
│
                                     回归到各微服务内部
├── OrderStatusEnum                    └── → mall-order
├── PayStatusEnum                           → mall-pay（或 mall-order）
├── SeckillProductDetailEntity              → mall-marketing
├── ESSeckillProductEntity                  → mall-marketing
├── UserSeckillProductTradeEntity           → mall-marketing
```

### 各服务按需引入

```
mall-pay（无数据库服务）           mall-order（数据库服务）          mall-gateway（网关）
├── mall-common-core               ├── mall-common-core             ├── mall-common-core
├── mall-common-security           ├── mall-common-security         └── mall-common-security
├── mall-order-client              ├── mall-common-mybatis
└── 支付宝 SDK                     ├── mall-common-redis（可选）
                                   ├── ShardingSphere
                                   ├── RocketMQ
                                   └── Elasticsearch
```

---

## 七、总结

| 维度 | 当前状态 | 目标状态 |
|------|---------|---------|
| 模块耦合 | 所有服务依赖同一个"上帝模块" | 每个服务按需选择细粒度模块 |
| 服务边界 | 业务实体/枚举全局可见 | 业务实体封闭在各自服务内 |
| 依赖管理 | 通用依赖层层携带、版本混乱 | 仅携带本服务真正需要的依赖 |
| 独立部署 | 升级一个依赖需要全量重建 | 单个服务独立演进 |
| 技术栈 | 所有服务锁定 Java + Spring | 为未来异构服务留出空间 |
| 打包体积 | 无数据库服务携带 MySQL 驱动 | 每个服务 JAR 最小化 |
| 框架兼容 | Gateway 被迫排除 Web 依赖 | 无兼容性问题 |

**一句话总结：mall-common 让这 8 个"微服务"变成了同一个单体应用的 8 个进程。真正的微服务架构中，common 模块应该极度克制——只放纯接口定义（DTO）和零依赖的工具方法，其他的都回归到各自服务中。**

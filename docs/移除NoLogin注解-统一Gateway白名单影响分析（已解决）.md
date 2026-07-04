# 移除 `@NoLogin` 注解 & 统一 Gateway 白名单 — 影响分析

## 一、背景

项目由单体架构向微服务架构演进的过程中，鉴权体系从"每个服务自己管"变成了"Gateway 统一入口校验"。但 `@NoLogin` 注解 + `NoLoginMap` + `initNoLogin()` 反射扫描这套单体时代的白名单机制仍然保留在 `mall-auth` 中，与 Gateway `AuthFilter` 的白名单形成**两套平行体系**。

**当前状态：** 一个免登录 URL 需要同时在两处维护，漏掉一处即产生 403 误拦截。Gateway 的 `gateway.filter.noAuth` 配置在 Nacos 中未设置（空字符串），实际白名单全靠 `@NoLogin` 在运行时扫描，Gateway `AuthFilter` 对所有路径放行后由下游服务自己拦截。

## 二、需要删除的组件

| 组件 | 位置 | 说明 |
|------|------|------|
| `@NoLogin` 注解 | `mall-common/.../annotation/NoLogin.java` | 注解定义，整文件删除 |
| `NoLoginMap` 工具类 | `mall-auth/.../util/NoLoginMap.java` | 静态集合 + 工具方法，整文件删除 |
| `initNoLogin()` 方法 | `mall-auth/.../config/SpringSecurityConfig.java` | 启动时反射扫描所有 `@NoLogin` 并注册到 permitAll |
| `NoLoginMap.notExist()` 调用 | `mall-auth/.../filter/JwtTokenFilter.java` | `JwtTokenFilter.doFilter()` 中绕过免登录 URL 的校验逻辑 |
| `NoLoginMap.getNoLoginUrlSet()` 调用 | `SpringSecurityConfig.filterChain()` | `.requestMatchers(NoLoginMap.getNoLoginUrlSet()...)` |
| 所有 `@NoLogin` 标注 | 16 个文件中的 controller/Feign 方法 | 逐一删除注解行 |

## 三、`@NoLogin` 使用现状（16 个文件）

### 3.1 mall-auth 内部（8 个文件）

这些接口由 `mall-auth` 自身提供，请求路径为 `/v1/xxx`。Gateway 转发到 `mall-auth` 后，`SpringSecurityConfig` 通过 `@NoLogin` 放行。

| 文件 | 路径 | 说明 |
|------|------|------|
| `WebUserController.java` | `GET /getCode` | 获取图形验证码 |
| | `POST /login` | 账号密码登录 |
| | `POST /loginByPhone` | 手机号登录 |
| | `POST /logout` | 退出登录 |
| `MobileUserController.java` | `GET /getCode` | 移动端验证码 |
| | `POST /login` | 移动端登录 |
| | `POST /loginByPhone` | 移动端手机号登录 |
| `MenuController.java` | `POST /searchByPage` | 查询菜单列表 |
| | `POST /insert` | 添加菜单 |
| | `POST /update` | 修改菜单 |
| | `POST /deleteByIds` | 删除菜单 |
| `RoleController.java` | `GET /all` | 查询所有角色 |
| `JobController.java` | `POST /searchByPage` | 查询岗位列表 |
| | `POST /deleteByIds` | 删除岗位 |
| `DeptController.java` | `GET /findById` | 通过 ID 查询部门 |
| | `POST /searchByPage` | 查询部门列表 |
| | `POST /searchByTree` | 查询部门树 |
| `TestController.java` | 不涉及生产 | 测试接口 |
| `UserFeignClient.java`（Feign 接口） | `POST /login` / `POST /loginByPhone` | **Feign 上的 `@NoLogin` 不会被 `initNoLogin()` 扫描到** |

> **注意：** `UserFeignClient` 和 `CategoryFeignClient` 等 Feign 接口上的 `@NoLogin` 注解是伪标记——`RequestMappingHandlerMapping` 只扫描 `@Controller` 类，不会扫描 `@FeignClient` 接口，所以 `initNoLogin()` 在实际运行时收集不到这些 URL。这些 `@NoLogin` 对系统没有实际作用，可以直接删除。

### 3.2 跨服务（8 个文件，涉及 mall-basic、mall-product、mall-pay）

这些服务**没有自己的 `SpringSecurityConfig`**，`@NoLogin` 在这里实际上没有 Spring Security 层面的效果（因为只有 `mall-auth` 的 `initNoLogin()` 能收集到它们——但也只限于 `mall-auth` 本服务的 controller 方法，跨服务的 controller 上的 `@NoLogin` 同样不会被收集到）。

| 文件 | 路径 | 说明 |
|------|------|------|
| `UploadController.java` | `POST /file/upload` | 文件上传 |
| | `POST /image/upload` | 图片上传 |
| `CommonSensitiveWordController.java` | `POST /checkSensitiveWord` | 校验敏感词 |
| `MobileIndexController.java` | `GET /getIndexCarouselImageList` | 首页轮播图 |
| `MobileCategoryController.java` | `GET /getCategoryByParentId` | 分类列表 |
| `MobileProductController.java` | `POST /searchProduct` | 商品搜索 |
| | `GET /getDetail` | 商品详情 |
| | `POST /searchProductComment` | 商品评论列表 |
| `CategoryController.java` | `POST /searchByTree` | 分类树 |
| `PayController.java` | 支付回调 | |
| `CategoryFeignClient.java`（Feign 接口） | `GET /getCategoryByParentId` | **不会被 `initNoLogin()` 扫描到** |

## 四、蝴蝶效应分析

### 4.1 影响范围表

| 改动项 | 影响服务 | 影响类型 | 风险等级 |
|--------|----------|----------|----------|
| 删 `@NoLogin` 注解定义 | mall-common | 编译：引用该注解的 16 个文件全部报错 | 🔴 必须同步删 |
| 删 `NoLoginMap` | mall-auth | 编译：2 个文件引用 | 🔴 必须同步改 |
| 删 `initNoLogin()` | mall-auth | 运行时：SpringSecurityConfig 更新 | 🟡 需确认 permissionAll 覆盖 |
| 删 `JwtTokenFilter` 对 `NoLoginMap` 的引用 | mall-auth | 运行时：filter 逻辑简化 | 🟢 删掉 if 分支即可 |
| 删 Controller 上的 `@NoLogin` | mall-auth/mall-basic/mall-product/mall-pay | 编译：删除注解行不影响业务逻辑 | 🟢 纯删除 |
| 删 Feign 上的 `@NoLogin` | mall-auth-client/mall-product-client | 编译：删除注解行 | 🟢 纯删除，且原注解无实际效果 |

### 4.2 需要 Gateway 补充的白名单

删掉 `@NoLogin` 后，Gateway 必须在 `gateway.filter.noAuth` 中补全以下路径（否则这些接口会被 `mall-auth` 自己的 Spring Security 拦截，返回 403）：

```
POST /auth/v1/web/user/login
POST /auth/v1/web/user/loginByPhone
POST /auth/v1/mobile/user/login
POST /auth/v1/mobile/user/loginByPhone
GET  /auth/v1/web/user/getCode
GET  /auth/v1/mobile/user/getCode
POST /auth/v1/web/user/logout
GET  /auth/v1/auth/menu/searchByPage
POST /auth/v1/auth/menu/insert
POST /auth/v1/auth/menu/update
POST /auth/v1/auth/menu/deleteByIds
GET  /auth/v1/auth/role/all
GET  /auth/v1/auth/dept/findById
POST /auth/v1/auth/dept/searchByPage
POST /auth/v1/auth/dept/searchByTree
POST /auth/v1/auth/job/searchByPage
POST /auth/v1/auth/job/deleteByIds
POST /basic/v1/file/upload                 # ← 注意：跨服务场景
POST /basic/v1/image/upload                #     Gateway 直接转发，
POST /basic/v1/common/checkSensitiveWord   #     mall-basic 没有自己的
GET  /product/v1/mobile/index/getIndexCarouselImageList  #     Spring Security
GET  /product/v1/mobile/category/getCategoryByParentId   #     因此 @NoLogin 删除后
POST /product/v1/mobile/product/searchProduct            #     如果 Gateway 不放行，
GET  /product/v1/mobile/product/getDetail                #     请求根本不会进入这些服务
POST /product/v1/mobile/product/searchProductComment
POST /product/v1/product/category/searchByTree
POST /pay/v1/mobile/pay/xxx               # 支付回调
```

### 4.3 不会被影响的部分

| 不影响 | 原因 |
|--------|------|
| 前端调用的 BFF 接口（`/admin/v1/xxx`、`/mobile/v1/xxx`） | Gateway 透传后由 BFF 自己验证 |
| 已有 token 的已登录用户请求 | `JwtTokenFilter` 登录逻辑不受影响 |
| 其他微服务（mall-order、mall-marketing、mall-recommend、mall-message） | 这些服务没有 `@NoLogin` 引用 |
| Feign 的 `@NoLogin` 删除 | 这些注解本身即无效果，删除无感知 |

## 五、实施步骤

### 阶段一：Gateway 先加白名单（零风险）

```yaml
# Nacos：mall-gateway-api-dev.yaml
gateway:
  filter:
    noAuth: /auth/v1/web/user/login,/auth/v1/web/user/loginByPhone,...
```

上线验证：Gateway 白名单生效后，已有 `@NoLogin` 仍然工作，双向冗余期。

### 阶段二：删除 @NoLogin 相关代码（本次改动）

```
Step 1: 删 mall-common 中的 @NoLogin 注解定义
Step 2: 删 mall-auth 中的 NoLoginMap
Step 3: 删 mall-auth 中的 initNoLogin() + 简化 SpringSecurityConfig
Step 4: 删 mall-auth JwtTokenFilter 中的 NoLoginMap 引用
Step 5: 删 16 个文件中的 @NoLogin 注解行
```

### 阶段三：清理验证

```bash
# 确认无残留引用
grep -r "NoLogin" src/               # 应无匹配
grep -r "NoLoginMap" src/            # 应无匹配
grep -r "initNoLogin" src/           # 应无匹配

# 编译验证
mvn compile -DskipTests               # 零错误

# 运行认证相关的测试
mvn test -pl mall-auth -Dsurefire.skip=false

# 启动验证（本地）
# 1. 启动 Nacos + MySQL + Redis
# 2. 启动 mall-gateway
# 3. 启动 mall-auth
# 4. curl POST /auth/v1/web/user/login  → 返回 token（免登录路径）
# 5. curl GET /auth/v1/auth/menu/searchByPage  → 返回 200（需手动添加鉴权头或由 Gateway 决定）
```

## 六、风险点汇总

| 风险 | 概率 | 缓解措施 |
|------|------|----------|
| Gateway 白名单漏配一个路径 | 中 | 阶段一先配白名单，验收后再删代码 |
| 旧的 `@NoLogin` 在 Feign 上被误认为有用 | 低 | 确认 `initNoLogin()` 只扫描 `@Controller` |
| 支付回调路径不确定 | 低 | `PayController` 上的 `@NoLogin` 单独确认完整 URL |
| 删除后启动阶段扫描不到白名单导致 403 | 低 | `SpringSecurityConfig` 中保留通用 permitAll 兜底 |

## 七、总结

整个迁移的核心逻辑是：**Gateway 一次校验，下游服务信任**。`@NoLogin` 是单体时代的遗产，在分布式架构下已经名存实亡（跨服务的 `@NoLogin` 实际上没有任何效果）。删除它不会产生新的风险，反而消除了两套平行白名单不一致的隐患。

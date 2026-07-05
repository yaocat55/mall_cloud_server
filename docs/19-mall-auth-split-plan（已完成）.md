# mall-auth 瘦身方案：从微服务到 JWT 基础设施

## 背景

当前 `mall-auth` 模块承载了过多职责。在 `mall-member` 已剥离 C 端认证后，`mall-auth` 仍然混杂着两层：

| 层级 | 载体 | 数据库 |
|------|------|--------|
| ① **JWT 基础设施** | JWT 签发/验证 + Redis 黑名单 | Redis（无数据库） |
| ② **Admin 业务** | 登录/注册、用户 CRUD、RBAC（角色/菜单/部门/岗位）、收货地址 | `auth_user` + 6 张 RBAC 表 |

## 目标

剥离后的架构：

```
                         ┌──────────────────┐
                         │     Gateway      │
                         │  JWT 解析 + Header│
                         └────────┬─────────┘
                                  │
                ┌─────────────────┼──────────────────┐
                ▼                 ▼                   ▼
         ┌──────────────┐  ┌────────────┐    ┌──────────────┐
         │  mall-auth   │  │ mall-admin │    │ mall-member  │
         │ JWT + Redis  │  │ 用户管理    │    │ C 端认证      │
         │ 黑名单       │  │ RBAC       │    │ 会员信息      │
         │ (无数据库)    │  │ 收货地址    │    │ (已有)        │
         └──────────────┘  └────────────┘    └──────────────┘
```

## 具体拆分方案

### 新建模块：`mall-admin`

从 `mall-auth` 拆分出的 admin 业务模块，逻辑与 `mall-member` 对等。

**POM 坐标：**
```xml
<artifactId>mall-admin</artifactId>
<name>mall-admin</name>
<description>Admin 后台管理服务</description>
```

**实体与数据库（从 mall-auth 迁移）：**

| 实体 | 数据表 | 说明 |
|------|--------|------|
| `UserEntity` | `auth_user` | 管理员用户 |
| `RoleEntity` | `auth_role` | 角色 |
| `MenuEntity` | `auth_menu` | 菜单/权限 |
| `DeptEntity` | `auth_dept` | 部门 |
| `JobEntity` | `auth_job` | 岗位 |
| `UserRoleEntity` | `auth_user_role` | 用户-角色关联 |
| `DeliveryAddressEntity` | `auth_delivery_address` | 收货地址 |

**接口清单：**

| 分组 | 路径 | 说明 |
|------|------|------|
| Web | `/v1/web/user/login` | 管理员登录 |
| Web | `/v1/web/user/loginByPhone` | 手机号登录 |
| Web | `/v1/web/user/register` | 创建管理员 |
| Web | `/v1/web/user/logout` | 登出 |
| Web | `/v1/web/user/getCode` | 验证码 |
| Web | `/v1/web/user/info` | 当前用户信息 |
| Web | `/v1/web/user/getUserDetail` | 用户详情 |
| Web | `/v1/web/user/updateUser` | 更新用户 |
| Web | `/v1/web/user/resetPassword` | 重置密码 |
| Web | `/v1/web/user/bindPhone` | 绑定手机号 |
| Internal | `/v1/internal/user/findByIds` | 批量查用户 |
| Internal | `/v1/internal/user/findByPhone` | 手机号查用户 |
| Internal | `/v1/internal/user/updateAvatar` | 更新头像 |
| RBAC | `/v1/web/role/*` | 角色 CRUD |
| RBAC | `/v1/web/menu/*` | 菜单 CRUD |
| RBAC | `/v1/web/dept/*` | 部门 CRUD |
| RBAC | `/v1/web/job/*` | 岗位 CRUD |
| Address | `/v1/mobile/deliveryAddress/*` | C 端收货地址（保留） |

### 瘦身后的 `mall-auth`

只负责 JWT 基础设施，不再有数据库。

**保留内容：**

| 组件 | 说明 |
|------|------|
| `JwtTokenFilter` | Redis 黑名单校验（`blacklist:{jti}`） |
| `TokenUtil` 相关（已在 `mall-common`） | JWT 解析/生成 |
| `UserTokenHelper`（外部 starter） | JWT 助手 |

**删除内容：** 所有实体、Mapper、Service、Controller（除 Filter 外）

**部署形态问题：** `mall-auth` 是否还需要作为独立服务启动？

| 选项 | 方案 | 评价 |
|------|------|------|
| A | `mall-auth` 仍作为独立服务，只跑 Filter | 简单、过渡友好，但杀鸡用牛刀 |
| B | 内嵌到 `mall-admin`，`mall-auth` 退化为 Starter | 更轻量，但 JwtTokenFilter 的包扫描范围要调整 |

**推荐：** 阶段二先走选项 A，`mall-auth` 保留独立服务形态，把业务代码搬走后它自然变轻。后续再评价是否合并进 `mall-admin`。

### 新建模块：`mall-admin-client`

从 `mall-auth-client` 拆分出 admin 专有 Feign 接口。

| 组件 | 去向 |
|------|------|
| `UserFeignClient` | `mall-admin-client`（改名为 `AdminUserFeignClient`） |
| `MenuFeignClient` | `mall-admin-client` |
| `RoleFeignClient` | `mall-admin-client` |
| `DeptFeignClient` | `mall-admin-client` |
| `JobFeignClient` | `mall-admin-client` |
| `DeliveryAddressFeignClient` | `mall-admin-client` |
| DTO（UserDTO/TokenDTO/RoleDTO/...） | `mall-admin-client` |

`mall-auth-client` 在阶段二中逐渐无人引用后可废弃。

### 启动顺序调整

```
① 基础层
  ├── mall-gateway
  ├── mall-auth        ← 瘦身后，启动更快
  └── mall-basic

② 业务层
  ├── mall-member      ← C 端（已有）
  ├── mall-admin       ← 新增（取代 mall-auth 的业务角色）
  ├── mall-product
  ├── mall-order
  └── ...

③ BFF 层
  ├── mall-admin-api   ← 改为依赖 mall-admin-client
  └── mall-mobile-api  ← 已依赖 mall-member-client
```

### 连锁修改

当前 `mall-auth-client` 被 9 个模块引用，每个模块使用的 Feign 接口和 DTO 各不相同：

| 模块 | 引用 Feign | 引用的 DTO | 实际调用的方法 |
|------|-----------|-----------|--------------|
| **mall-admin-api** | UserFeignClient<br>MenuFeignClient<br>RoleFeignClient<br>DeptFeignClient<br>JobFeignClient<br>DeliveryAddressFeignClient | UserDTO, RoleDTO, MenuTreeDTO,<br>DeptTreeDTO, JobDTO,<br>DeliveryAddressDTO | `login`, `loginByPhone`, `register`, `getCode`<br>`getUserInfo`, `getUserDetail`, `findByIds`<br>`findByPhone`, `updateUser`, `resetPassword`<br>`bindPhone`, `updateAvatar`<br>+ 全部 RBAC 接口<br>+ 全部收货地址接口 |
| **mall-mobile-api** | UserFeignClient<br>DeliveryAddressFeignClient | UserDTO, UpdateUserDTO<br>UserAvatarDTO,<br>DeliveryAddressDTO,<br>DeliveryAddressDefaultDTO | `getUserDetail`, `updateUser`, `updateAvatar`<br>+ 全部收货地址接口 |
| **mall-order** | DeliveryAddressFeignClient | DeliveryAddressDTO | `getDetail`, `getUserDeliveryAddressList` |
| **mall-order-client** | — | DeliveryAddressDTO | 仅 DTO 定义引用 |
| **mall-product** | UserFeignClient | UserDTO | `findByIds` |
| **mall-marketing** | UserFeignClient | UserDTO | `findByIds` |
| **mall-message** | UserFeignClient | UserDTO | `findByIds` |
| **mall-basic** | UserFeignClient | UserDTO | 注入但未调用（死代码） |
| **mall-basic-client** | — | — | 无（JobFeignClient 是 basic 自有的，无关 auth） |

**关键观察：**

1. **`UserFeignClient.findByPhone`** → 只被 `mall-admin-api` 调用
2. **`UserFeignClient.findByIds`** → 被 `mall-admin-api`、`mall-product`、`mall-marketing`、`mall-message` 调用（最广泛使用的方法）
3. **`DeliveryAddressFeignClient`** → 被 `mall-mobile-api`、`mall-admin-api`、`mall-order` 调用，纯业务功能
4. **RBAC 相关**（MenuFeignClient、RoleFeignClient、DeptFeignClient、JobFeignClient）→ 只被 `mall-admin-api` 调用
5. **`mall-basic`** 虽注入了 UserFeignClient 但未实际调用，切换时可直接删除

**改动的模块和范围：**

1. 所有 `mall-auth-client` 依赖 → `mall-admin-client`
2. 所有 `UserFeignClient` 引用 → `AdminUserFeignClient`
3. 所有 `import cn.net.mall.auth.dto.*` → `cn.net.mall.admin.client.dto.*`
4. `@EnableFeignClients` 中的包扫描路径
5. Gateway 路由：`/api/auth/**` → `lb://mall-auth` + `/api/admin-svc/**` → `lb://mall-admin`
6. Nacos 配置：新增 `mall-admin-dev.yaml`

## 实施步骤（按顺序）

1. **创建 `mall-admin` 模块** — 拷贝 mall-auth 的业务代码（UserService、RoleService、控制层等），调整包名
2. **创建 `mall-admin-client` 模块** — 拷贝 Feign 接口 + DTO，包名 `cn.net.mall.admin.client`
3. **瘦身 `mall-auth`** — 删除业务代码，只保留 Filter + 基础设施
4. **迁移 `mall-auth-client` 调用方** — 逐一替换各模块的引用
5. **Nacos 配置** — 新建 `mall-admin-dev.yaml`
6. **Gateway 路由** — 新增 `/api/admin-svc/**` + 移除旧 auth 业务路径
7. **验证编译** — 全量编译 + 启动测试
8. **清理** — 废弃 `mall-auth-client`，删除不再引用的旧 DTO

## 注意事项

1. **数据库共享：** 阶段二 `mall-admin` 和原始 `mall-auth` 共用同一库（susan_mall_auth），不做数据迁移
2. **短期双写：** 过渡期间 `mall-auth` 的 Filter 和 `mall-admin` 的服务共存，互不干扰
3. **`mall-auth-client` 逐步淘汰：** 一周内所有调用方切换完成后再删除
4. **`mall-auth` Nacos 配置：** 瘦身后的 `mall-auth` 不再需要 DataSource 配置，只需 Redis

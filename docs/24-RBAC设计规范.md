# RBAC 设计规范 — 现状分析与重构方案

## 一、数据库表结构

所有 admin 权限相关表统一 `auth_*` 前缀，共 7 张表：

### 1.1 核心表

| 表名 | 实体类 | 说明 |
|------|--------|------|
| `auth_user` | `UserEntity` | 用户（字段见 1.2） |
| `auth_role` | `RoleEntity` | 角色 |
| `auth_menu` | `MenuEntity` | 菜单/按钮 |
| `auth_dept` | `DeptEntity` | 部门 |
| `auth_job` | `JobEntity` | 岗位 |

### 1.2 关联表

| 表名 | 实体类 | 关系 |
|------|--------|------|
| `auth_user_role` | `UserRoleEntity` | 用户-角色 N:N |
| `auth_role_menu` | `RoleMenuEntity` | 角色-菜单 N:N |
| `auth_role_dept` | `RoleDeptEntity` | 角色-部门 N:N |

### 1.3 辅助表

| 表名 | 实体类 | 说明 |
|------|--------|------|
| `auth_user_avatar` | `UserAvatarEntity` | 用户头像文件信息 |
| `auth_captcha`（推测） | `CaptchaEntity` | 图形验证码 |

---

## 二、实体字段详情

### UserEntity（`auth_user`）

```yaml
基础字段: id, createUserId, createUserName, createTime, updateUserId, updateUserName, updateTime, isDel  # 继承 BaseEntity
主要字段:
  userName:            String   # 用户名，唯一
  password:            String   # BCrypt 加密
  phone:               String   # 手机号
  email:               String   # 邮箱
  nickName:            String   # 别名/花名
  sex:                 Integer  # 1男 2女
  birthday:            String   # 生日
  avatarId:            Long     # 头像（关联 auth_user_avatar）
  deptId:              Long     # 所属部门（关联 auth_dept）
  jobId:               Long     # 岗位ID（关联 auth_job）
  validStatus:         Boolean  # 1有效 0无效
  lastLoginTime:       Date     # 最后登录时间
  lastLoginCity:       String   # 最后登录城市
  lastChangePasswordTime: Date  # 最后改密日期
关联对象:
  dept:        DeptEntity        # 部门对象
  jobs:        List<JobEntity>   # ?? 岗位列表（与 jobId 矛盾）
  roles:       List<RoleEntity>  # 角色列表（通过 UserRoleEntity）
```

### RoleEntity（`auth_role`）

```yaml
基础字段: BaseEntity
主要字段:
  name:        String    # 角色名称（如"管理员"）
  remark:      String    # 备注
  dataScope:   String    # 数据权限范围（无人使用❌）
  level:       Integer   # 角色级别（数值越小等级越高？）
  permission:  String    # 权限标识（如 "admin", "user:manager", 直接作为 GrantedAuthority）
关联对象:
  menus:       List<MenuEntity>  # 菜单列表（通过 RoleMenuEntity）
```

### MenuEntity（`auth_menu`）

```yaml
基础字段: BaseEntity
主要字段:
  name:        String    # 菜单名称（如"用户管理"）
  pid:         Long      # 上级菜单ID（树形结构，0=根）
  type:        Integer   # 1目录 2菜单 3按钮
  sort:        Integer   # 排序号
  path:        String    # 前端路由路径
  component:   String    # 前端组件路径
  icon:        String    # 图标名称
  hidden:      Boolean   # 是否隐藏
  isLink:      Integer   # 是否外链 1是 0否
  url:         String    # 外链URL
  permission:  String    # 按钮权限标识（逗号分隔，如 "user:add,user:edit"）
```

### DeptEntity（`auth_dept`）

```yaml
基础字段: BaseEntity
主要字段:
  name:        String    # 部门名称
  pid:         Long      # 上级部门ID（树形结构）
  validStatus: Boolean   # 1有效 0无效
  roleId:      Long      # ?? 部门直接绑角色？（与 auth_role_dept 冗余❌）
```

### JobEntity（`auth_job`）

```yaml
基础字段: BaseEntity
主要字段:
  name:        String    # 岗位名称
  sort:        Integer   # 排序
  deptId:      Long      # 所属部门
  validStatus: Boolean   # 1有效 0无效
```

---

## 三、关联关系图

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────┐
│  auth_user   │────→│  auth_user_role  │────→│  auth_role   │
│  (dept_id)   │     └──────────────────┘     │  (permission)│
│  (job_id)    │                               └──────┬───────┘
└──────┬───────┘                                      │
       │                                               │
       ▼                                               ▼
┌──────────────┐                               ┌──────────────┐     ┌──────────────┐
│  auth_dept   │                               │ auth_role_menu│────→│  auth_menu   │
│  (pid 树形)  │                               └──────────────┘     │  (permission)│
│  (roleId?)   │                                                    │  (pid 树形)  │
└──────────────┘                                                    │  (type)      │
       │                                                           └──────────────┘
       ▼
┌──────────────┐
│  auth_job    │
│  (dept_id)   │
└──────────────┘

另外：auth_role_dept —— 角色-部门 N:N（数据权限用）
```

---

## 四、现有 Bug 清单

### 🐛 B1 — MenuMapper JOIN 条件写错

**文件**: `mall-admin/src/main/resources/cn/net/mall/admin/mapper/admin/MenuMapper.xml`

```sql
-- 第 189 行，当前写法（错）：
INNER JOIN auth_role_menu srm ON sm.id = srm.role_id
--                                    ^^^^^^^^^^^^^^^^
-- sm.id 是 菜单ID，srm.role_id 是 角色ID，永远对不上

-- 正确写法：
INNER JOIN auth_role_menu srm ON sm.id = srm.menu_id
```

**后果**: `findMenuByRoleIdList` 永远查不到菜单 → 用户登录后拿不到菜单权限 → `SimpleGrantedAuthority` 为空 → 所有 `@PreAuthorize("hasRole('xxx')")` 都会拒绝。

**严重程度**: 🔴 致命。菜单权限功能完全不可用。

### 🐛 B2 — GrantedAuthorityDefaults("") 去除了 ROLE_ 前缀

**文件**: `mall-admin/src/main/java/cn/net/mall/admin/config/SpringSecurityConfig.java:112`

```java
@Bean
public GrantedAuthorityDefaults grantedAuthorityDefaults() {
    return new GrantedAuthorityDefaults("");
}
```

Spring Security 标准行为:
- `hasRole('admin')` → 检查 `ROLE_admin`
- `hasAuthority('admin')` → 检查 `admin`

这里把前缀置空后：
- `hasRole('admin')` → 检查 `admin`（和 hasAuthority 行为一致了）

这意味着 `@PreAuthorize("hasRole('admin')")` 匹配的是 `RoleEntity.permission = "admin"`，但所有文档和规范都变得不标准。

### 🐛 B3 — 全系统只有 1 个 `@PreAuthorize`

**文件**: `mall-admin/src/main/java/cn/net/mall/admin/controller/auth/UserController.java:45`

```java
@PreAuthorize("hasRole('User')")
```

其他 **31+ 个 controller 接口无任何角色校验**。系统实际上只有"有 token / 无 token"的区别，没有角色权限控制。

涉及的无权限接口（不完整列举）：

| 模块 | 接口 | 问题 |
|------|------|------|
| 用户管理 | `/v1/web/user/onlineUsers` | 任意token用户可查在线列表 |
| 用户管理 | `/v1/web/user/updateUser` | 任意token用户可改他人信息 |
| 菜单管理 | `/v1/menu/*` | 任意token用户可增删改菜单 |
| 角色管理 | `/v1/role/*` | 任意token用户可管理角色 |
| 基础数据 | `/v1/dept/*`, `/v1/job/*` | 同上 |

### 🐛 B4 — `UserEntity.jobId` 与 `UserEntity.jobs` 冗余

```java
private Long jobId;               // 单一岗位ID
private List<JobEntity> jobs;     // 岗位列表（从未被 MyBatis ResultMap 映射）
```

字段 `jobs` 在 Mapper XML 的 ResultMap 中没有对应映射，属于死代码。

### 🐛 B5 — `DeptEntity.roleId` 与 `auth_role_dept` 冗余

部门直接持有 `roleId`，但同时又存在 `auth_role_dept` 多对多关联表。两者的语义从未被统一解释过。

### 🐛 B6 — `RoleEntity.dataScope` 字段空转

设计了数据权限范围字段，但：
- 没有任何 Mapper 查询使用它
- 没有任何 `@PreFilter` / `@PostFilter` 使用它
- 没有任何 MyBatis SQL 动态拼接它

---

## 五、核心流程分析

### 5.1 用户登录 → 权限加载流程

```
UserService.login()
  ↓ loadUserByUsername(username)
UserDetailsServiceImpl.loadUserByUsername()
  ├── userMapper.findByUserName(username)                         → 查用户
  ├── roleMapper.findRoleByUserId(user.id)                        → 查角色
  │     SQL: SELECT * FROM auth_role r
  │          INNER JOIN auth_user_role ur ON r.id = ur.role_id
  │          WHERE ur.user_id = ?
  │
  ├── menuMapper.findMenuByRoleIdList(roleIds)                    → 查菜单权限（有 BUG!）
  │     SQL: SELECT * FROM auth_menu m
  │          INNER JOIN auth_role_menu rm ON m.id = rm.role_id    ← 这里应该是 rm.menu_id
  │          WHERE rm.role_id IN (?)
  │
  ├── 角色 permission → SimpleGrantedAuthority (如 "admin")
  └── 菜单 permission → SimpleGrantedAuthority (如 "user:add")
       ↓
  JwtUserEntity({ authorities, roles })
       ↓
  TokenHelper 将 roles 写入 JWT claims["roles"]
       ↓
  JwtTokenFilter 从 JWT 恢复 authorities → UsernamePasswordAuthenticationToken → SecurityContext
```

### 5.2 鉴权流程

```
Gateway AuthFilter
  ├── 白名单? → 放行
  ├── 有 JWT? → 验证 → 透传 X-Roles header
  └── 无 JWT? → 401

AuthApiInterceptor（所有服务）
  ├── 有 X-Roles header? → 设入 SecurityContext
  └── 无 header? → 自己解析 JWT

JwtTokenFilter（仅 admin 服务）
  ├── 从 JWT 恢复 authorities
  ├── Redis 黑名单检查
  └── 设入 SecurityContext

Controller @PreAuthorize  →  检查 SecurityContext 中的 authorities
（但全系统只有 1 处使用）
```

---

## 六、重构方案

### 原则

1. **不动数据库表结构**——现有表设计没大问题
2. **修 Bug 优先**——菜单权限 JOIN 错是最致命的
3. **加规范但不加代码量**——`@PreAuthorize` 逐条加，不要引入新的注解框架
4. **权限标识统一**——Role 的 `permission` 和 Menu 的 `permission` 使用相同的命名规范

### 6.1 立即修复（Bug 修复）

| 优先级 | Bug | 改动 |
|--------|-----|------|
| 🔴 P0 | B1 MenuMapper JOIN | `srm.role_id` → `srm.menu_id` |
| 🟡 P1 | B2 GrantedAuthorityDefaults | 决策：保留空前缀，但明确告知所有 `@PreAuthorize` 使用 `hasAuthority` 而非 `hasRole`，避免歧义 |
| 🟡 P1 | B4 UserEntity.jobs | 删除无用字段 |
| 🟢 P2 | B5 DeptEntity.roleId | 明确语义：是默认角色还是冗余字段？清理 |
| 🟢 P2 | B6 dataScope | 要么实现，要么删除字段 |

### 6.2 权限标识命名规范

建议统一使用冒号分隔的多级命名：

```
# 角色级（RoleEntity.permission）
admin                  # 超级管理员
system:manager         # 系统管理员
order:manager          # 订单管理员
product:manager        # 商品管理员
marketing:manager      # 营销管理员

# 按钮/操作级（MenuEntity.permission，逗号分隔）
user:list,user:add,user:edit,user:del       # 用户管理
order:list,order:detail,order:edit          # 订单管理
product:list,product:add,product:edit       # 商品管理
role:list,role:add,role:edit,role:del       # 角色管理
menu:list,menu:add,menu:edit,menu:del       # 菜单管理
dept:list,dept:add,dept:edit,dept:del       # 部门管理
```

### 6.3 `@PreAuthorize` 逐接口添加计划

按模块分组：

#### Phase 1 — 核心权限（`mall-admin`）

| 接口路径 | 所需权限 | 拦截方式 |
|----------|----------|----------|
| `/v1/web/user/**` | 除 login/logout/getCode 外需要 `user:*` | 类级别 `@PreAuthorize` + method 级别细化 |
| `/v1/menu/**` | `menu:*` | 类级别 |
| `/v1/role/**` | `role:*` | 类级别 |

#### Phase 2 — 业务权限

| 接口路径 | 所需权限 | 拦截方式 |
|----------|----------|----------|
| `/v1/dept/**` | `dept:*` | 类级别 |
| `/v1/job/**` | `job:*` | 类级别 |
| `/v1/shopping/deliveryAddress/**` | `order:address:*` | method 级别 |

#### Phase 3 — 数据权限（dataScope）

在 Phase 1/2 完成后实现，逻辑：

```
role.dataScope = "ALL"        → 可查看所有部门数据
role.dataScope = "DEPT"       → 可查看本部门数据
role.dataScope = "DEPT_CHILD" → 可查看本部门及子部门数据
role.dataScope = "SELF"       → 仅查看自己数据
```

通过在 Mapper SQL 中动态拼接 `dept_id` 过滤条件实现。

### 6.4 配置变更

`SpringSecurityConfig.PERMIT_ALL_URLS` 白名单需要收窄：

```java
// 当前白名单（太宽了）
"/v1/web/user/logout",       // 任何人都能 logout —— 这个可以保留
"/v1/menu/searchByPage",     // 不需要认证就能查菜单？不应该
"/v1/menu/insert",           // 不需要认证就能增删改菜单？不应该
"/v1/menu/update",
"/v1/menu/deleteByIds",

// 建议收紧：只有真正不需要登录的才在白名单
"/v1/web/user/login",
"/v1/web/user/loginByPhone",
"/v1/web/user/getCode",
"/v1/web/user/logout",
"/v1/mobile/user/login",
"/v1/mobile/user/loginByPhone",
"/v1/mobile/user/getCode",
```

---

## 七、实施时间线估计

| 阶段 | 内容 | 预估工时 |
|------|------|----------|
| Phase 0 | 修 B1 MenuMapper JOIN Bug | 10 分钟 |
| Phase 0 | 修 B4/B5/B6 清理冗余字段 | 20 分钟 |
| Phase 1 | 核心权限 `@PreAuthorize` + 测试 | 2 小时 |
| Phase 2 | 业务权限 `@PreAuthorize` + 测试 | 1 小时 |
| Phase 3 | 数据权限 SQL 过滤 + 测试 | 3 小时 |
| | **合计** | **约 6.5 小时** |

> 注：不含前端菜单权限页面开发。

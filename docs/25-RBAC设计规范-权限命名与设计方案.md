# RBAC 设计规范 — 权限命名与设计方案

> 本文件定义 mall-admin 模块的 RBAC 权限命名规范、Spring Security 集成方案以及 `@PreAuthorize` 使用守则。
> 现状分析见 [24-RBAC设计规范.md](24-RBAC设计规范.md)。

---

## 一、权限模型概览

```
用户 ── N:N ──> 角色 ── N:N ──> 菜单/按钮
                   │
                   └──> RoleEntity.permission ──> ROLE_ 前缀 ──> hasRole('xxx')
                   │
                   └──> MenuEntity.permission ──> 原样保留 ──> hasAuthority('xxx:xx')
```

### 两层权限互不干扰

| 层级 | 来源 | 最终 authority | 匹配方式 |
|------|------|---------------|----------|
| 角色级 | `RoleEntity.permission` | `ROLE_admin` | `@PreAuthorize("hasRole('admin')")` |
| 操作级 | `MenuEntity.permission` | `user:add` | `@PreAuthorize("hasAuthority('user:add')")` |

---

## 二、权限命名规范

### 2.1 角色命名（`RoleEntity.permission`）

命名采用**单段小写英文**，一个角色对应一个标识。

| 角色 | permission | authority | 说明 |
|------|-----------|-----------|------|
| 超级管理员 | `admin` | `ROLE_admin` | 不限权限，能管理所有角色 |
| 系统管理员 | `system:manager` | `ROLE_system:manager` | 系统配置管理 |
| 订单管理员 | `order:manager` | `ROLE_order:manager` | 订单管理 |
| 商品管理员 | `product:manager` | `ROLE_product:manager` | 商品管理 |
| 营销管理员 | `marketing:manager` | `ROLE_marketing:manager` | 营销活动管理 |
| 客服 | `support` | `ROLE_support` | 售后客服，只读+回复 |

> 注：`role` 字段存入数据库时不带 `ROLE_` 前缀，前缀在 `UserDetailsServiceImpl` 中自动拼装。

### 2.2 操作权限命名（`MenuEntity.permission`）

命名采用 **`模块:操作`** 多段格式，多个权限逗号分隔。

#### 通用 CRUD 操作标识

| 操作 | 标识 | 说明 |
|------|------|------|
| 列表/分页 | `xxx:list` | 查看列表 |
| 查看详情 | `xxx:detail` | 查看单条详情 |
| 新增 | `xxx:add` | 创建 |
| 修改 | `xxx:edit` | 更新 |
| 删除 | `xxx:del` | 删除 |
| 导入 | `xxx:import` | 批量导入 |
| 导出 | `xxx:export` | 批量导出 |

#### 各模块操作权限清单

| 模块 | 菜单名称 | 按钮权限（`MenuEntity.permission`） |
|------|---------|-----------------------------------|
| 用户管理 | 用户列表 | `user:list,user:add,user:edit,user:del,user:import,user:export` |
| 角色管理 | 角色列表 | `role:list,role:add,role:edit,role:del` |
| 菜单管理 | 菜单列表 | `menu:list,menu:add,menu:edit,menu:del` |
| 部门管理 | 部门列表 | `dept:list,dept:add,dept:edit,dept:del` |
| 岗位管理 | 岗位列表 | `job:list, job:add, job:edit, job:del` |
| 商品管理 | 商品列表 | `product:list,product:add,product:edit,product:del,product:import,product:export` |
| 商品分组 | 分组管理 | `category:list,category:add,category:edit,category:del` |
| 商品属性 | 属性管理 | `attr:list,attr:add,attr:edit,attr:del` |
| 订单管理 | 订单列表 | `order:list,order:detail,order:edit,order:del,order:export` |
| 营销管理 | 优惠券 | `coupon:list,coupon:add,coupon:edit,coupon:del` |
| 营销管理 | 秒杀活动 | `seckill:list,seckill:add,seckill:edit,seckill:del` |
| 内容管理 | 轮播图 | `banner:list,banner:add,banner:edit,banner:del` |
| 内容管理 | 公告 | `notice:list,notice:add,notice:edit,notice:del` |
| 内容管理 | 推荐商品 | `recommend:list,recommend:add,recommend:edit,recommend:del` |
| 系统管理 | 定时任务 | `task:list,task:add,task:edit,task:del` |
| 系统管理 | 通知记录 | `notify:list,notify:detail` |
| 系统管理 | 图片管理 | `image:list,image:add,image:del` |
| 系统管理 | 文件管理 | `file:list,file:add,file:del` |
| 系统管理 | 敏感词 | `sensitive:list,sensitive:add,sensitive:edit,sensitive:del` |
| 系统管理 | 短信记录 | `sms:list,sms:detail` |
| 系统管理 | 字典管理 | `dict:list,dict:add,dict:edit,dict:del` |

---

## 三、Spring Security 集成方案

### 3.1 `GrantedAuthorityDefaults` 保留标准前缀

```java
// SpringSecurityConfig.java
@Bean
public GrantedAuthorityDefaults grantedAuthorityDefaults() {
    return new GrantedAuthorityDefaults("ROLE_");
}
```

> ⚠️ 现有代码中此项被设为 `""`（空前缀），需要改回标准行为。

### 3.2 UserDetailsServiceImpl 权限拼装规则

```java
// UserDetailsServiceImpl.fillUserAuthority()

// 第1步：角色权限 — 加 ROLE_ 前缀
roleEntities.forEach(role -> {
    if (StringUtils.hasLength(role.getPermission())) {
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getPermission()));
    }
});

// 第2步：菜单按钮权限 — 保持原样，逗号拆开
menuList.forEach(menu -> {
    if (StringUtils.hasLength(menu.getPermission())) {
        String[] perms = menu.getPermission().split(",");
        for (String perm : perms) {
            perm = perm.trim();
            if (StringUtils.hasLength(perm)) {
                authorities.add(new SimpleGrantedAuthority(perm));
            }
        }
    }
});
```

### 3.3 最终 SecurityContext 中的 authorities 示例

**admin 用户（角色 `admin`，关联所有菜单）：**

```
ROLE_admin
user:list, user:add, user:edit, user:del
role:list, role:add, role:edit, role:del
menu:list, menu:add, menu:edit, menu:del
dept:list, dept:add, dept:edit, dept:del
product:list, product:add, product:edit, product:del
order:list, order:detail, order:edit, order:del
...
```

**订单管理员（角色 `order:manager`，仅关联订单菜单）：**

```
ROLE_order:manager
order:list, order:detail, order:edit, order:del
```

---

## 四、`@PreAuthorize` 使用规范

### 4.1 基本规则

| 场景 | 表达式 | 说明 |
|------|--------|------|
| 超管专属 | `hasRole('admin')` | 仅超级管理员能调 |
| 有操作权限即可 | `hasAuthority('role:add')` | 分配了该按钮权限的角色都能调 |
| 超管 或 有操作权限 | `hasRole('admin') or hasAuthority('role:add')` | 超管兜底 |
| 同时有多个权限 | `hasAuthority('user:list') and hasAuthority('user:add')` | 较少使用 |

### 4.2 `@PreAuthorize` 放置策略

**策略A：类级别 + 方法级别（推荐）**

```java
@RestController
@RequestMapping("/v1/web/user")
@PreAuthorize("hasAuthority('user:list')")     // 类级别：该 controller 至少需要 user:list
public class UserController {

    @GetMapping("/searchByPage")
    @PreAuthorize("hasAuthority('user:list')")  // 显式声明（与类级别一致，可省略但建议保留）
    public ApiResult<PageResult<UserDTO>> searchByPage(...) { ... }

    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('user:add')")   // 方法级别：细化到具体操作
    public ApiResult<Void> insert(...) { ... }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('user:edit')")
    public ApiResult<Void> update(...) { ... }

    @GetMapping("/detail")
    @PreAuthorize("hasAuthority('user:detail')")
    public ApiResult<UserDTO> detail(...) { ... }

    @PostMapping("/deleteByIds")
    @PreAuthorize("hasAuthority('user:del')")
    public ApiResult<Void> deleteByIds(...) { ... }
}
```

**策略B：仅方法级别（灵活但冗余）**

适用于一个 controller 里有多个模块接口混在一起的情况（如 `AdminBasicController` 混合了文件/图片/短信等）。

### 4.3 白名单规则

仅以下接口不校验权限（在 `SpringSecurityConfig.PERMIT_ALL_URLS` 中声明）：

```
# 认证相关
/v1/web/user/login
/v1/web/user/loginByPhone
/v1/web/user/getCode
/v1/web/user/logout
/v1/mobile/user/login
/v1/mobile/user/loginByPhone
/v1/mobile/user/getCode

# 内部 Feign 调用
/v1/internal/**

# 测试
/v1/test/testOpenFeign
```

> ⚠️ 当前白名单中的 `/v1/menu/*`、`/v1/role/*`、`/v1/dept/*`、`/v1/job/*` 接口需要从白名单移除，改为 `@PreAuthorize` 控制。

---

## 五、数据权限方案（Phase 3）

### 5.1 `RoleEntity.dataScope` 字段含义

| dataScope 值 | 含义 | SQL 过滤逻辑 |
|-------------|------|-------------|
| `ALL` | 查看全部数据 | 不加部门过滤 |
| `DEPT_CHILD`（默认） | 查看本部门及子部门数据 | `dept_id IN (当前部门及所有子部门ID)` |
| `DEPT` | 仅查看本部门数据 | `dept_id = 当前部门ID` |
| `SELF` | 仅查看自己创建的数据 | `create_user_id = 当前用户ID` |

### 5.2 实现方式

通过 MyBatis 拦截器或 `BaseService` 中的公共方法动态拼接：

```java
// BaseService 中提供数据权限辅助方法
protected <T> T applyDataScope(T query) {
    JwtUserEntity user = FillUserUtil.getCurrentUserInfo();
    if (user == null) return query;

    // 超管不受数据权限限制
    if (user.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_admin"))) {
        return query;
    }

    // 根据用户角色查询 dataScope
    String dataScope = getUserDataScope(user.getId());
    // 根据 dataScope 设置查询条件...
    return query;
}
```

---

## 六、实施步骤

### Phase 0 — Bug 修复（约 30 分钟）

1. 修 `MenuMapper.xml` JOIN 条件（`srm.role_id` → `srm.menu_id`）
2. `GrantedAuthorityDefaults("")` → `GrantedAuthorityDefaults("ROLE_")`
3. `UserDetailsServiceImpl` 中角色权限加 `ROLE_` 前缀
4. 清理 `UserEntity.jobs` 冗余字段
5. 清理 `DeptEntity.roleId` 冗余字段

### Phase 1 — 核心权限 @PreAuthorize（约 2 小时）

1. `UserController` — 用户管理 CRUD
2. `RoleController` — 角色管理 CRUD
3. `MenuController` — 菜单管理 CRUD
4. `DeptController` — 部门管理 CRUD
5. `JobController` — 岗位管理 CRUD
6. 收紧 `PERMIT_ALL_URLS` 白名单

### Phase 2 — 业务权限 @PreAuthorize（约 1 小时）

1. `ProductController` / `CategoryController` / `AttrController`
2. `OrderController` / `DeliveryAddressController`
3. `CouponController` / `SeckillController`
4. `BannerController` / `NoticeController` / `RecommendController`
5. `TaskController` / `FileController` / `ImageController` / `SmsController`

### Phase 3 — 数据权限 dataScope（约 3 小时）

1. 实现 dataScope SQL 动态拼接
2. 各查询接口增加数据权限过滤
3. 角色管理页面增加 dataScope 配置

---

## 七、权限检查清单

每次新增接口时，对照以下问题检查：

- [ ] 这个接口需要登录吗？（不需要 → 加入 `PERMIT_ALL_URLS`）
- [ ] 这个接口谁可以用？所有登录用户 / 特定角色 / 超管专属
- [ ] 对应哪个操作权限标识？（对照 2.2 表格）
- [ ] 是否需要数据权限过滤？（限制查看本部门/全部数据）
- [ ] 加 `@PreAuthorize` 了没有？

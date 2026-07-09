# 电商企业组织架构与 RBAC 设计方案

## 一、企业背景

本架构针对的是一个 **B2C 电商平台**，自营为主、兼有商家入驻。技术层面已拆分出商品、订单、营销、支付、消息等微服务，组织架构与权限体系需要与之匹配。

---

## 二、部门架构（auth_dept）

### 2.1 部门树

```
Cloud Mall 集团（总公司）
│
├── 总裁办（决策层）
│   ├── 总经办
│   └── 战略发展部
│
├── 技术中心（产研）
│   ├── 后端研发部
│   │   ├── 订单组
│   │   ├── 商品组
│   │   ├── 支付组
│   │   └── 营销组
│   ├── 前端研发部
│   ├── 测试部
│   ├── 运维部（SRE）
│   └── 数据分析部
│
├── 运营中心
│   ├── 类目运营部
│   ├── 活动运营部
│   ├── 内容运营部
│   └── 新媒体运营部
│
├── 供应链中心
│   ├── 采购部
│   │   ├── 国内采购组
│   │   └── 海外采购组
│   ├── 仓储物流部
│   │   ├── 华东仓储中心
│   │   ├── 华南仓储中心
│   │   ├── 华北仓储中心
│   │   └── 物流调度组
│   └── 品控部
│
├── 营销中心
│   ├── 品牌策划部
│   ├── 渠道推广部
│   └── 会员运营部
│
├── 客服中心
│   ├── 售前客服部
│   ├── 售后客服部
│   └── 客诉处理部
│
├── 财务中心
│   ├── 会计核算部
│   ├── 预算管理部
│   ├── 税务管理部
│   └── 资金管理部
│
├── 人力资源中心
│   ├── 招聘部
│   ├── 薪酬绩效部
│   └── 员工关系部
│
└── 法务合规部（直属总裁办）
```

### 2.2 部门数据示例

```sql
-- 顶级
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (1, 0, 'Cloud Mall 集团', 1);

-- 一级部门
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (2, 1, '技术中心', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (3, 1, '运营中心', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (4, 1, '供应链中心', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (5, 1, '营销中心', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (6, 1, '客服中心', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (7, 1, '财务中心', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (8, 1, '人力资源中心', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (9, 1, '总裁办', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (10, 1, '法务合规部', 1);

-- 技术中心下级
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (21, 2, '后端研发部', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (22, 2, '前端研发部', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (23, 2, '测试部', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (24, 2, '运维部', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (25, 2, '数据分析部', 1);

-- 供应链下级 - 仓储按地域分（为 auth_role_dept 数据权限打基础）
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (41, 4, '仓储物流部', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (411, 41, '华东仓储中心', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (412, 41, '华南仓储中心', 1);
INSERT INTO auth_dept (id, pid, name, valid_status) VALUES (413, 41, '华北仓储中心', 1);
```

---

## 三、岗位定义（auth_job）

岗位不直接关联权限，但决定用户在部门内的职能定位，页面展示使用。

| 岗位 | 所属部门 | 说明 |
|------|---------|------|
| 部门总监 | 各一级部门 | 部门负责人 |
| 技术经理 | 技术中心下属各组 | 技术团队管理 |
| 高级开发工程师 | 后端研发部 | 技术骨干 |
| 开发工程师 | 后端研发部 | 普通开发 |
| 初级开发工程师 | 后端研发部 | 新人 |
| 测试经理 | 测试部 | 测试团队管理 |
| 测试工程师 | 测试部 | 功能/自动化测试 |
| 运维工程师 | 运维部 | 线上运维 |
| 运营经理 | 运营中心下属各组 | 运营团队管理 |
| 运营专员 | 运营中心 | 日常运营 |
| 采购经理 | 采购部 | 采购管理 |
| 采购专员 | 采购部 | 供应商对接 |
| 仓储主管 | 各仓储中心 | 仓储现场管理 |
| 仓储管理员 | 各仓储中心 | 出入库操作 |
| 客服主管 | 客服中心下属各组 | 客服团队管理 |
| 客服专员 | 客服中心 | 售前/售后处理 |
| 财务经理 | 财务中心 | 财务团队管理 |
| 会计 | 会计核算部 | 账务处理 |
| HRBP | 人力资源中心 | 对接各业务线 |

---

## 四、角色定义（auth_role）

### 4.1 角色清单

每个角色附带 `permission` 标识，用于 `@PreAuthorize("hasRole('xxx')")`。

| 角色名 | permission | 级别 | 数据范围 | 所属部门 | 说明 |
|--------|:---------:|:---:|:--------:|---------|------|
| **超级管理员** | `admin` | 1 | 全部 | 总裁办 | 系统初始，不受权限约束 |
| 技术总监 | `tech_director` | 2 | 技术中心 | 技术中心 | 管理整个技术线 |
| 后端经理 | `backend_mgr` | 3 | 后端研发部 | 后端研发部 | |
| 前端经理 | `frontend_mgr` | 3 | 前端研发部 | 前端研发部 | |
| 测试经理 | `qa_mgr` | 3 | 测试部 | 测试部 | |
| 运维经理 | `ops_mgr` | 3 | 运维部 | 运维部 | |
| 开发工程师 | `dev` | 4 | 本部门 | — | 普通开发者 |
| 运营总监 | `ops_director` | 2 | 运营中心 | 运营中心 | |
| 类目运营 | `category_op` | 4 | 本部门 | 类目运营部 | 商品类目管理 |
| 活动运营 | `campaign_op` | 4 | 本部门 | 活动运营部 | 创建/管理活动 |
| 供应链总监 | `supply_director` | 2 | 供应链中心 | 供应链中心 | |
| 采购经理 | `purchase_mgr` | 3 | 采购部 | 采购部 | |
| 采购专员 | `purchaser` | 4 | 本部门 | 采购部 | |
| 华东仓储主管 | `wh_east_mgr` | 3 | 华东仓储中心 | 华东仓储中心 | 只能管华东的数据 |
| 华南仓储主管 | `wh_south_mgr` | 3 | 华南仓储中心 | 华南仓储中心 | 只能管华南的数据 |
| 仓储管理员 | `warehouser` | 4 | 本部门 | 各仓储中心 | |
| 营销总监 | `mkt_director` | 2 | 营销中心 | 营销中心 | |
| 品牌策划 | `brand_planner` | 4 | 本部门 | 品牌策划部 | |
| 会员运营 | `member_op` | 4 | 本部门 | 会员运营部 | |
| 客服总监 | `cs_director` | 2 | 客服中心 | 客服中心 | |
| 售前客服 | `presales_cs` | 4 | 本部门 | 售前客服部 | |
| 售后客服 | `aftersales_cs` | 4 | 本部门 | 售后客服部 | |
| 财务总监 | `finance_director` | 2 | 财务中心 | 财务中心 | |
| 会计 | `accountant` | 4 | 本部门 | 会计核算部 | |
| HR 总监 | `hr_director` | 2 | 人力资源中心 | 人力资源中心 | |
| HR | `hr` | 4 | 本部门 | 人力资源中心 | |

### 4.2 角色与部门对应关系（auth_role_dept）

数据权限的核心：**一个角色能管哪些部门的数据。**

```
角色                    可管理的部门范围（auth_role_dept）
─────                   ──────────────────────────────
超级管理员              全部（无视数据权限）
技术总监                [2, 21, 22, 23, 24, 25]  ← 技术中心及其所有下级
供应链总监              [4, 41, 411, 412, 413]  ← 供应链中心及其所有下级
华东仓储主管            [411]                    ← 只华东仓储中心
华南仓储主管            [412]                    ← 只华南仓储中心
开发工程师              [21]                     ← 只后端研发部
```

> 超级管理员不受数据权限限制（`AuthApiInterceptor` 检查 `permission = 'admin'` 时跳过数据过滤）。
> 其他角色在查询订单、商品等列表时，需额外 JOIN `auth_role_dept` 过滤部门范围。

---

## 五、菜单与页面权限（auth_menu + auth_role_menu）

### 5.1 菜单树与权限标识

```
系统管理（目录）                    ← 仅 admin / 技术中心可见
├── 用户管理（菜单）              ← user:list
│   ├── 新增（按钮）              ← user:add
│   ├── 编辑（按钮）              ← user:edit
│   ├── 删除（按钮）              ← user:delete
│   └── 重置密码（按钮）          ← user:resetPwd
├── 角色管理（菜单）              ← role:list
│   ├── 新增（按钮）              ← role:add
│   ├── 编辑（按钮）              ← role:edit
│   ├── 删除（按钮）              ← role:delete
│   └── 分配菜单（按钮）          ← role:assignMenu
├── 菜单管理（菜单）              ← menu:list
│   ├── 新增（按钮）              ← menu:add
│   ├── 编辑（按钮）              ← menu:edit
│   └── 删除（按钮）              ← menu:delete
├── 部门管理（菜单）              ← dept:list
│   ├── 新增（按钮）              ← dept:add
│   └── 编辑（按钮）              ← dept:edit
├── 岗位管理（菜单）              ← job:list
│   ├── 新增（按钮）              ← job:add
│   └── 编辑（按钮）              ← job:edit

商品管理（目录）
├── 商品列表（菜单）              ← product:list
│   ├── 新增（按钮）              ← product:add
│   ├── 编辑（按钮）              ← product:edit
│   ├── 上架（按钮）              ← product:publish
│   ├── 下架（按钮）              ← product:unpublish
│   └── 删除（按钮）              ← product:delete
├── 类目管理（菜单）              ← category:list
│   ├── 新增（按钮）              ← category:add
│   └── 编辑（按钮）              ← category:edit
├── 品牌管理（菜单）              ← brand:list
├── 商品评价（菜单）              ← product:comment

订单管理（目录）
├── 订单列表（菜单）              ← order:list
│   ├── 查看详情（按钮）          ← order:detail
│   ├── 修改价格（按钮）          ← order:modifyPrice
│   ├── 发货（按钮）              ← order:ship
│   └── 退款处理（按钮）          ← order:refund
├── 售后管理（菜单）              ← order:afterSale
│   ├── 退货审核（按钮）          ← order:returnApprove
│   └── 退款审核（按钮）          ← order:refundApprove

营销管理（目录）
├── 优惠券管理（菜单）            ← coupon:list
│   ├── 新增（按钮）              ← coupon:add
│   └── 发放（按钮）              ← coupon:issue
├── 活动管理（菜单）              ← campaign:list
│   ├── 新增（按钮）              ← campaign:add
│   └── 编辑（按钮）              ← campaign:edit
├── 首页配置（菜单）              ← home:config
└── 积分商城（菜单）              ← points:list

供应链管理（目录）
├── 采购单（菜单）                ← purchase:list
│   ├── 新增（按钮）              ← purchase:add
│   └── 入库（按钮）              ← purchase:stockIn
├── 库存管理（菜单）              ← stock:list
│   ├── 库存调整（按钮）          ← stock:adjust
│   └── 库存盘点（按钮）          ← stock:check
├── 仓储管理（菜单）              ← warehouse:list
│   └── 入库单（按钮）            ← warehouse:stockIn
└── 物流管理（菜单）              ← logistics:list

客服管理（目录）
├── 工单列表（菜单）              ← ticket:list
│   ├── 分配（按钮）              ← ticket:assign
│   └── 关闭（按钮）              ← ticket:close
├── 售后工单（菜单）              ← ticket:afterSale
└── 常见问题（菜单）              ← faq:list

财务管理（目录）
├── 对账管理（菜单）              ← finance:reconciliation
├── 结算管理（菜单）              ← finance:settlement
├── 发票管理（菜单）              ← finance:invoice
├── 退款流水（菜单）              ← finance:refundLog

数据分析（目录）
├── 销售报表（菜单）              ← report:sales
├── 商品分析（菜单）              ← report:product
├── 用户分析（菜单）              ← report:user
├── 经营概览（菜单）              ← report:dashboard

系统监控（目录）                  ← 仅 admin / 运维部
├── 在线用户（菜单）              ← monitor:onlineUser
├── 操作日志（菜单）              ← monitor:operLog
├── 服务监控（菜单）              ← monitor:service
└── 数据监控（菜单）              ← monitor:data
```

### 5.2 角色 → 菜单权限矩阵

| 角色 | 可见菜单 | 说明 |
|------|---------|------|
| 超级管理员 | **全部** |  |
| 技术总监 | 系统管理、系统监控 | 管人、管服务器 |
| 开发工程师 | 订单管理（只读）、系统监控（只读） | 排查问题用 |
| 运营总监 | 商品管理、营销管理、数据分析 | 核心业务线 |
| 类目运营 | 商品管理 → 类目管理 | 只管类目 |
| 活动运营 | 营销管理 → 活动管理、优惠券管理 | |
| 供应链总监 | 供应链管理、订单管理 → 订单列表 | 看供应全链路 |
| 华东仓储主管 | 供应链管理 → 仓储管理（仅华东数据） | 数据范围受限 |
| 客服总监 | 客服管理、订单管理 → 售后管理 | |
| 售前客服 | 客服管理 → 工单列表 | |
| 财务总监 | 财务管理、订单管理 → 订单列表（只读） | |
| HR | 系统管理 → 用户管理、部门管理、岗位管理 | 管人事 |

---

## 六、用户与角色分配示例

### 6.1 典型用户

| 用户名 | 姓名 | 部门 | 岗位 | 角色 |
|--------|------|------|------|------|
| admin | 系统管理员 | 总裁办 | — | 超级管理员 |
| zhang_san | 张三 | 后端研发部 | 技术经理 | 后端经理 |
| li_si | 李四 | 后端研发部 | 高级开发工程师 | 开发工程师 |
| wang_wu | 王五 | 类目运营部 | 运营专员 | 类目运营 |
| zhao_liu | 赵六 | 华东仓储中心 | 仓储主管 | 华东仓储主管 |
| sun_qi | 孙七 | 华南仓储中心 | 仓储管理员 | 仓储管理员 |
| chen_ba | 陈八 | 财务中心 | 财务经理 | 财务总监 |

### 6.2 用户-角色分配（auth_user_role）

```sql
-- admin 是超级管理员
INSERT INTO auth_user_role (id, user_id, role_id) VALUES (1, 1, 1);

-- 张三（user_id=2）有角色 后端经理
INSERT INTO auth_user_role (id, user_id, role_id) VALUES (2, 2, 5);

-- 赵六（user_id=5）有角色 华东仓储主管
INSERT INTO auth_user_role (id, user_id, role_id) VALUES (5, 5, 17);

-- 一个人也可以有多个角色
-- 张三同时是 后端经理 + 开发工程师（角色可叠加）
```

---

## 七、数据权限实现方案

### 7.1 原理

核心表 `auth_role_dept`：

```sql
auth_role_dept
  id       BIGINT   PRIMARY KEY
  role_id  BIGINT   NOT NULL  -- 角色ID
  dept_id  BIGINT   NOT NULL  -- 可管理的部门ID
```

用户在查询数据时，通过**用户角色 → auth_role_dept → 部门 ID 列表 → 过滤数据**：

```
登录用户
  ↓
user_id → auth_user_role → role_ids
  ↓
role_ids → auth_role_dept → dept_ids（可管理的部门范围）
  ↓
dept_ids → WHERE dept_id IN (...)  → 只返回该部门的数据
```

### 7.2 数据范围级别

`RoleEntity` 的 `dataScope` 字段控制数据权限等级：

| dataScope | 含义 | 说明 |
|:---------:|------|------|
| `1` | 仅本人数据 | 只能看自己创建的 |
| `2` | 本部门数据 | 只能看自己部门的数据 |
| `3` | 本部门及下级 | 能看本部门和下级部门的数据 |
| `4` | 自定义 | 通过 auth_role_dept 精确指定 |
| `5` | 全部 | 不看任何数据权限限制 |

### 7.3 示例：仓储主管的数据权限

```sql
-- 华东仓储主管 角色 → 只关联 华东仓储中心
INSERT INTO auth_role_dept (id, role_id, dept_id) VALUES (1, 17, 411);

-- 供应链总监 角色 → 关联供应链中心及其所有下级
INSERT INTO auth_role_dept (id, role_id, dept_id) VALUES (2, 13, 4);    -- 供应链中心
INSERT INTO auth_role_dept (id, role_id, dept_id) VALUES (3, 13, 41);   -- 仓储物流部
INSERT INTO auth_role_dept (id, role_id, dept_id) VALUES (4, 13, 411);  -- 华东
INSERT INTO auth_role_dept (id, role_id, dept_id) VALUES (5, 13, 412);  -- 华南
INSERT INTO auth_role_dept (id, role_id, dept_id) VALUES (6, 13, 413);  -- 华北
```

查询库存时：

```java
// 伪代码：仓储管理员查库存列表
List<Long> deptIds = getDataScopeDeptIds(currentUser);
// deptIds = [411]  ← 华东仓储主管
// deptIds = [4, 41, 411, 412, 413]  ← 供应链总监

warehouseMapper.selectByPage(condition, deptIds);
```

```sql
-- Mapper XML
SELECT * FROM product_stock
WHERE warehouse_dept_id IN
<foreach collection="deptIds" item="deptId" open="(" close=")" separator=",">
    #{deptId}
</foreach>
```

---

## 八、角色分级管理

### 8.1 谁可以创建角色

`RoleEntity` 的 `level` 字段控制角色的层级，**低级别角色不能创建/编辑高级别角色**：

| level | 可管理范围 | 可创建/分配给谁 |
|:-----:|-----------|---------------|
| 1 | 超级管理员 | 不受约束 |
| 2 | 一级部门总监级 | 可创建 3-4 级角色，分配给本部门及下级 |
| 3 | 二级部门经理级 | 可创建 4 级角色，分配给本部门 |
| 4 | 普通岗位 | 不能创建角色 |

### 8.2 部门负责人创建角色流程

```
超级管理员
  ├── 创建部门树（技术中心、运营中心...）
  ├── 任命部门负责人（张三 → 技术经理）
  │     └── 张三（技术总监角色，level=2）
  │           ├── 在他所属部门（技术中心）范围内
  │           ├── 创建 level>=3 的角色：开发工程师、测试工程师...
  │           └── 分配角色给本部门的人
  │
  └── 超级管理员只直接管理：
        ├── 部门架构
        ├── 一级角色（总监级）
        └── 系统层面配置（菜单、API）

部门负责人 看不到其他部门的角色和人员
```

### 8.3 代码校验

```java
// 创建角色时的校验
@PreAuthorize("hasRole('admin')")
public void createRole(RoleEntity role) {
    // admin 可以创建任何级别的角色
}

// 部门负责人创建角色
@PreAuthorize("hasRole('tech_director')")
public void createDeptRole(RoleEntity role) {
    // 1. 校验 level：不能创建比自己级别高或平级的角色
    // 2. 校验部门范围：只能在自己管理的部门范围内创建
    // 3. 分配用户：只能分配给自己部门的人
}
```

---

## 九、映射到现有微服务

| 微服务 | 对应部门/角色 | 维护团队 |
|--------|-------------|---------|
| mall-product | 后端研发部 → 商品组 | 商品组开发 |
| mall-order | 后端研发部 → 订单组 | 订单组开发 |
| mall-marketing | 后端研发部 → 营销组 | 营销组开发 |
| mall-pay | 后端研发部 → 支付组 | 支付组开发 |
| mall-admin | 后端研发部（基础架构组） | 基础架构组 |
| mall-basic | 后端研发部（基础架构组） | 基础架构组 |
| mall-customer | 后端研发部（C端组） | C端开发 |
| mall-message | 后端研发部（基础架构组） | 基础架构组 |
| mall-recommend | 数据分析部 | 算法组 |
| mall-gateway | 运维部/SRE | 运维 |

---

## 十、初始种子数据清单

部署新环境时的最小初始化数据集：

```yaml
# 1. 一级部门（9 个）
# 2. 二级部门（约 20 个）
# 3. 核心角色（约 25 个，含 permission 标识）
# 4. 超级管理员：admin / password
# 5. 角色-菜单映射（按角色可见范围分配）
# 6. 角色-部门映射（数据权限范围）
# 7. 菜单树（目录约 10 个，菜单约 30 个，按钮约 80 个）
```

详见 `docs/26-权限目录清单.md`（已有菜单和按钮的种子数据清单）。

-- ============================================================
-- RBAC 数据清理脚本
-- 保留 admin 用户（ID=13），清空其他所有 auth_* 数据后重新初始化
-- ============================================================

-- ============================================================
-- 1. 清空关联表（先删子表）
-- ============================================================
DELETE FROM auth_role_menu;
DELETE FROM auth_role_dept;
DELETE FROM auth_user_role;
DELETE FROM auth_user_avatar;

-- ============================================================
-- 2. 清空主表
-- ============================================================
DELETE FROM auth_menu;
DELETE FROM auth_role;
DELETE FROM auth_job;
DELETE FROM auth_dept;

-- ============================================================
-- 3. 删除 admin 以外的用户（ID=13 的 admin 保留）
-- ============================================================
DELETE FROM auth_user WHERE id != 13;

-- ============================================================
-- 4. 初始化角色
-- ============================================================
INSERT INTO auth_role (id, name, permission, data_scope, level, remark, create_user_id, create_user_name, create_time, is_del)
VALUES (1, '超级管理员', 'admin', 'ALL', 1, '系统最高权限角色', 13, 'admin', NOW(), 0);

-- ============================================================
-- 5. admin 用户 → 超级管理员角色
-- ============================================================
INSERT INTO auth_user_role (id, user_id, role_id)
VALUES (1, 13, 1);

-- ============================================================
-- 6. 菜单 + 按钮权限树
-- ============================================================

-- 系统管理（一级目录）
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1, '系统管理', 0, 1, 1, 'system', '/system', '', 0, 13, 'admin', NOW(), 0);

-- 用户管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (11, '用户管理', 1, 2, 1, 'user', '/system/user', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1101, '用户列表', 11, 3, 1, 'user:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1102, '新增用户', 11, 3, 2, 'user:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1103, '编辑用户', 11, 3, 3, 'user:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1104, '删除用户', 11, 3, 4, 'user:del', 1, 13, 'admin', NOW(), 0);

-- 角色管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (12, '角色管理', 1, 2, 2, 'role', '/system/role', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1201, '角色列表', 12, 3, 1, 'role:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1202, '新增角色', 12, 3, 2, 'role:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1203, '编辑角色', 12, 3, 3, 'role:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1204, '删除角色', 12, 3, 4, 'role:del', 1, 13, 'admin', NOW(), 0);

-- 菜单管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (13, '菜单管理', 1, 2, 3, 'menu', '/system/menu', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1301, '菜单列表', 13, 3, 1, 'menu:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1302, '新增菜单', 13, 3, 2, 'menu:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1303, '编辑菜单', 13, 3, 3, 'menu:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1304, '删除菜单', 13, 3, 4, 'menu:del', 1, 13, 'admin', NOW(), 0);

-- 部门管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (14, '部门管理', 1, 2, 4, 'dept', '/system/dept', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1401, '部门列表', 14, 3, 1, 'dept:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1402, '新增部门', 14, 3, 2, 'dept:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1403, '编辑部门', 14, 3, 3, 'dept:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1404, '删除部门', 14, 3, 4, 'dept:del', 1, 13, 'admin', NOW(), 0);

-- 岗位管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (15, '岗位管理', 1, 2, 5, 'job', '/system/job', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1501, '岗位列表', 15, 3, 1, 'job:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1502, '新增岗位', 15, 3, 2, 'job:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1503, '编辑岗位', 15, 3, 3, 'job:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (1504, '删除岗位', 15, 3, 4, 'job:del', 1, 13, 'admin', NOW(), 0);

-- 基础数据（一级目录）
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2, '基础数据', 0, 1, 2, 'basic', '/basic', '', 0, 13, 'admin', NOW(), 0);

-- 字典管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (21, '字典管理', 2, 2, 1, 'dict', '/basic/dict', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2101, '字典列表', 21, 3, 1, 'dict:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2102, '新增字典', 21, 3, 2, 'dict:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2103, '编辑字典', 21, 3, 3, 'dict:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2104, '删除字典', 21, 3, 4, 'dict:del', 1, 13, 'admin', NOW(), 0);

-- 图片管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (22, '图片管理', 2, 2, 2, 'image', '/basic/image', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2201, '图片列表', 22, 3, 1, 'image:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2202, '上传图片', 22, 3, 2, 'image:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2203, '删除图片', 22, 3, 3, 'image:del', 1, 13, 'admin', NOW(), 0);

-- 敏感词管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (23, '敏感词管理', 2, 2, 3, 'sensitive', '/basic/sensitive', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2301, '敏感词列表', 23, 3, 1, 'sensitive:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2302, '新增敏感词', 23, 3, 2, 'sensitive:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2303, '编辑敏感词', 23, 3, 3, 'sensitive:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2304, '删除敏感词', 23, 3, 4, 'sensitive:del', 1, 13, 'admin', NOW(), 0);

-- 短信记录
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (24, '短信记录', 2, 2, 4, 'sms', '/basic/sms', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2401, '短信列表', 24, 3, 1, 'sms:list', 1, 13, 'admin', NOW(), 0);

-- 定时任务
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (25, '定时任务', 2, 2, 5, 'task', '/basic/job', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2501, '任务列表', 25, 3, 1, 'task:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2502, '新增任务', 25, 3, 2, 'task:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2503, '编辑任务', 25, 3, 3, 'task:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (2504, '删除任务', 25, 3, 4, 'task:del', 1, 13, 'admin', NOW(), 0);

-- 商品管理（一级目录）
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3, '商品管理', 0, 1, 3, 'product', '/product', '', 0, 13, 'admin', NOW(), 0);

-- 商品列表
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (31, '商品列表', 3, 2, 1, 'product-list', '/product/list', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3101, '商品查询', 31, 3, 1, 'product:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3102, '新增商品', 31, 3, 2, 'product:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3103, '编辑商品', 31, 3, 3, 'product:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3104, '删除商品', 31, 3, 4, 'product:del', 1, 13, 'admin', NOW(), 0);

-- 商品分类
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (32, '商品分类', 3, 2, 2, 'category', '/product/category', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3201, '分类列表', 32, 3, 1, 'category:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3202, '新增分类', 32, 3, 2, 'category:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3203, '编辑分类', 32, 3, 3, 'category:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3204, '删除分类', 32, 3, 4, 'category:del', 1, 13, 'admin', NOW(), 0);

-- 品牌管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (33, '品牌管理', 3, 2, 3, 'brand', '/product/brand', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3301, '品牌列表', 33, 3, 1, 'brand:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3302, '新增品牌', 33, 3, 2, 'brand:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3303, '编辑品牌', 33, 3, 3, 'brand:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3304, '删除品牌', 33, 3, 4, 'brand:del', 1, 13, 'admin', NOW(), 0);

-- 属性管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (34, '属性管理', 3, 2, 4, 'attr', '/product/attr', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3401, '属性列表', 34, 3, 1, 'attr:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3402, '新增属性', 34, 3, 2, 'attr:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3403, '编辑属性', 34, 3, 3, 'attr:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3404, '删除属性', 34, 3, 4, 'attr:del', 1, 13, 'admin', NOW(), 0);

-- 单位管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (35, '单位管理', 3, 2, 5, 'unit', '/product/unit', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3501, '单位列表', 35, 3, 1, 'unit:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3502, '新增单位', 35, 3, 2, 'unit:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3503, '编辑单位', 35, 3, 3, 'unit:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (3504, '删除单位', 35, 3, 4, 'unit:del', 1, 13, 'admin', NOW(), 0);

-- 首页内容（一级目录）
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (4, '首页内容', 0, 1, 4, 'home', '/home', '', 0, 13, 'admin', NOW(), 0);

-- 轮播图
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (41, '轮播图管理', 4, 2, 1, 'banner', '/home/banner', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (4101, '轮播图列表', 41, 3, 1, 'banner:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (4102, '新增轮播图', 41, 3, 2, 'banner:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (4103, '编辑轮播图', 41, 3, 3, 'banner:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (4104, '删除轮播图', 41, 3, 4, 'banner:del', 1, 13, 'admin', NOW(), 0);

-- 公告管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (42, '公告管理', 4, 2, 2, 'notice', '/home/notice', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (4201, '公告列表', 42, 3, 1, 'notice:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (4202, '新增公告', 42, 3, 2, 'notice:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (4203, '编辑公告', 42, 3, 3, 'notice:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (4204, '删除公告', 42, 3, 4, 'notice:del', 1, 13, 'admin', NOW(), 0);

-- 推荐商品
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (43, '推荐商品', 4, 2, 3, 'recommend', '/home/recommend', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (4301, '推荐列表', 43, 3, 1, 'recommend:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (4302, '新增推荐', 43, 3, 2, 'recommend:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (4303, '编辑推荐', 43, 3, 3, 'recommend:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (4304, '删除推荐', 43, 3, 4, 'recommend:del', 1, 13, 'admin', NOW(), 0);

-- 营销管理（一级目录）
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (5, '营销管理', 0, 1, 5, 'marketing', '/marketing', '', 0, 13, 'admin', NOW(), 0);

-- 优惠券管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (51, '优惠券管理', 5, 2, 1, 'coupon', '/marketing/coupon', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (5101, '优惠券列表', 51, 3, 1, 'coupon:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (5102, '新增优惠券', 51, 3, 2, 'coupon:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (5103, '编辑优惠券', 51, 3, 3, 'coupon:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (5104, '删除优惠券', 51, 3, 4, 'coupon:del', 1, 13, 'admin', NOW(), 0);

-- 秒杀管理
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (52, '秒杀管理', 5, 2, 2, 'seckill', '/marketing/seckill', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (5201, '秒杀列表', 52, 3, 1, 'seckill:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (5202, '新增秒杀', 52, 3, 2, 'seckill:add', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (5203, '编辑秒杀', 52, 3, 3, 'seckill:edit', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (5204, '删除秒杀', 52, 3, 4, 'seckill:del', 1, 13, 'admin', NOW(), 0);

-- 订单管理（一级目录）
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (6, '订单管理', 0, 1, 6, 'order', '/order', '', 0, 13, 'admin', NOW(), 0);

-- 订单列表
INSERT INTO auth_menu (id, name, pid, type, sort, icon, path, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (61, '订单列表', 6, 2, 1, 'order-list', '/order/list', '', 0, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (6101, '订单查询', 61, 3, 1, 'order:list', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (6102, '订单详情', 61, 3, 2, 'order:detail', 1, 13, 'admin', NOW(), 0);
INSERT INTO auth_menu (id, name, pid, type, sort, permission, hidden, create_user_id, create_user_name, create_time, is_del)
VALUES (6103, '编辑订单', 61, 3, 3, 'order:edit', 1, 13, 'admin', NOW(), 0);

-- ============================================================
-- 7. admin 角色 → 全部菜单权限
-- ============================================================
INSERT INTO auth_role_menu (id, role_id, menu_id)
VALUES
-- 系统管理
(1,  1, 1),   (2,  1, 11),  (3,  1, 1101), (4,  1, 1102), (5,  1, 1103), (6,  1, 1104),
(7,  1, 12),  (8,  1, 1201), (9,  1, 1202), (10, 1, 1203), (11, 1, 1204),
(12, 1, 13),  (13, 1, 1301), (14, 1, 1302), (15, 1, 1303), (16, 1, 1304),
(17, 1, 14),  (18, 1, 1401), (19, 1, 1402), (20, 1, 1403), (21, 1, 1404),
(22, 1, 15),  (23, 1, 1501), (24, 1, 1502), (25, 1, 1503), (26, 1, 1504),
-- 基础数据
(27, 1, 2),   (28, 1, 21),  (29, 1, 2101), (30, 1, 2102), (31, 1, 2103), (32, 1, 2104),
(33, 1, 22),  (34, 1, 2201), (35, 1, 2202), (36, 1, 2203),
(37, 1, 23),  (38, 1, 2301), (39, 1, 2302), (40, 1, 2303), (41, 1, 2304),
(42, 1, 24),  (43, 1, 2401),
(44, 1, 25),  (45, 1, 2501), (46, 1, 2502), (47, 1, 2503), (48, 1, 2504),
-- 商品管理
(49, 1, 3),   (50, 1, 31),  (51, 1, 3101), (52, 1, 3102), (53, 1, 3103), (54, 1, 3104),
(55, 1, 32),  (56, 1, 3201), (57, 1, 3202), (58, 1, 3203), (59, 1, 3204),
(60, 1, 33),  (61, 1, 3301), (62, 1, 3302), (63, 1, 3303), (64, 1, 3304),
(65, 1, 34),  (66, 1, 3401), (67, 1, 3402), (68, 1, 3403), (69, 1, 3404),
(70, 1, 35),  (71, 1, 3501), (72, 1, 3502), (73, 1, 3503), (74, 1, 3504),
-- 首页内容
(75, 1, 4),   (76, 1, 41),  (77, 1, 4101), (78, 1, 4102), (79, 1, 4103), (80, 1, 4104),
(81, 1, 42),  (82, 1, 4201), (83, 1, 4202), (84, 1, 4203), (85, 1, 4204),
(86, 1, 43),  (87, 1, 4301), (88, 1, 4302), (89, 1, 4303), (90, 1, 4304),
-- 营销管理
(91, 1, 5),   (92, 1, 51),  (93, 1, 5101), (94, 1, 5102), (95, 1, 5103), (96, 1, 5104),
(97, 1, 52),  (98, 1, 5201), (99, 1, 5202), (100,1, 5203), (101,1, 5204),
-- 订单管理
(102,1, 6),   (103,1, 61),  (104,1, 6101), (105,1, 6102), (106,1, 6103);

-- ====================================
-- mall-inventory 表结构迁移（第一阶段）
-- ====================================

-- 1. inventory：增加 BaseEntity 审计字段
ALTER TABLE inventory
    ADD COLUMN `create_user_id`   bigint       DEFAULT NULL COMMENT '创建人ID'  AFTER `version`,
    ADD COLUMN `create_user_name` varchar(64)  DEFAULT NULL COMMENT '创建人名称' AFTER `create_user_id`,
    ADD COLUMN `update_user_id`   bigint       DEFAULT NULL COMMENT '修改人ID'  AFTER `create_user_name`,
    ADD COLUMN `update_user_name` varchar(64)  DEFAULT NULL COMMENT '修改人名称' AFTER `update_user_id`,
    ADD COLUMN `is_del`           tinyint      NOT NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是' AFTER `update_user_name`,
    ADD INDEX `idx_is_del` (`is_del`);

-- 2. inventory_batch：增加 BaseEntity 审计字段
ALTER TABLE inventory_batch
    ADD COLUMN `create_user_id`   bigint       DEFAULT NULL COMMENT '创建人ID'  AFTER `status`,
    ADD COLUMN `create_user_name` varchar(64)  DEFAULT NULL COMMENT '创建人名称' AFTER `create_user_id`,
    ADD COLUMN `update_user_id`   bigint       DEFAULT NULL COMMENT '修改人ID'  AFTER `create_user_name`,
    ADD COLUMN `update_user_name` varchar(64)  DEFAULT NULL COMMENT '修改人名称' AFTER `update_user_id`,
    ADD COLUMN `is_del`           tinyint      NOT NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是' AFTER `update_user_name`,
    ADD INDEX `idx_is_del` (`is_del`);

-- 3. inventory_log：增加 BaseEntity 审计字段 + update_time + shipment_id
ALTER TABLE inventory_log
    ADD COLUMN `shipment_id`      bigint       DEFAULT NULL COMMENT '发货单ID' AFTER `order_id`,
    ADD COLUMN `create_user_id`   bigint       DEFAULT NULL COMMENT '创建人ID'  AFTER `shipment_id`,
    ADD COLUMN `create_user_name` varchar(64)  DEFAULT NULL COMMENT '创建人名称' AFTER `create_user_id`,
    ADD COLUMN `update_user_id`   bigint       DEFAULT NULL COMMENT '修改人ID'  AFTER `create_user_name`,
    ADD COLUMN `update_user_name` varchar(64)  DEFAULT NULL COMMENT '修改人名称' AFTER `update_user_id`,
    ADD COLUMN `update_time`      datetime(3)  NULL DEFAULT NULL COMMENT '修改时间' AFTER `update_user_name`,
    ADD COLUMN `is_del`           tinyint      NOT NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是' AFTER `update_time`,
    ADD INDEX `idx_is_del` (`is_del`);

-- 4. warehouse：新建仓库表
CREATE TABLE IF NOT EXISTS `warehouse` (
    `id`               bigint       NOT NULL COMMENT '系统ID',
    `name`             varchar(128) NOT NULL COMMENT '仓库名称',
    `code`             varchar(64)  NOT NULL COMMENT '仓库编码',
    `address`          varchar(256) DEFAULT NULL COMMENT '仓库地址',
    `contact`          varchar(64)  DEFAULT NULL COMMENT '联系人',
    `phone`            varchar(32)  DEFAULT NULL COMMENT '联系电话',
    `status`           tinyint      NOT NULL DEFAULT 1 COMMENT '状态 1:启用 0:停用',
    `remark`           varchar(256) DEFAULT NULL COMMENT '备注',
    `create_user_id`   bigint       DEFAULT NULL COMMENT '创建人ID',
    `create_user_name` varchar(64)  DEFAULT NULL COMMENT '创建人名称',
    `create_time`      datetime(3)  NOT NULL COMMENT '创建时间',
    `update_user_id`   bigint       DEFAULT NULL COMMENT '修改人ID',
    `update_user_name` varchar(64)  DEFAULT NULL COMMENT '修改人名称',
    `update_time`      datetime(3)  DEFAULT NULL COMMENT '修改时间',
    `is_del`           tinyint      NOT NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_status` (`status`),
    KEY `idx_is_del` (`is_del`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库';

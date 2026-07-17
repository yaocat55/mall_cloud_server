-- 创建数据库
CREATE DATABASE IF NOT EXISTS `cloud_mall_inventory` DEFAULT CHARSET utf8mb4;

-- 库存主表（一个 SKU 一条记录）
CREATE TABLE IF NOT EXISTS `cloud_mall_inventory`.`inventory` (
    `id`            bigint       NOT NULL  PRIMARY KEY,
    `product_id`    bigint       NOT NULL  COMMENT '商品/SKU ID',
    `quantity`      int          NOT NULL  DEFAULT 0  COMMENT '总入库量',
    `frozen`        int          NOT NULL  DEFAULT 0  COMMENT '已冻结未支付',
    `available`     int          NOT NULL  DEFAULT 0  COMMENT '可用库存',
    `sale_count`    int          NOT NULL  DEFAULT 0  COMMENT '已售数量',
    `version`       int          NOT NULL  DEFAULT 0  COMMENT '乐观锁',
    `create_time`   datetime(3)  NOT NULL,
    `update_time`   datetime(3)  NOT NULL,
    UNIQUE KEY `uk_product_id` (`product_id`),
    KEY `idx_available` (`available`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品库存';

-- 批次表
CREATE TABLE IF NOT EXISTS `cloud_mall_inventory`.`inventory_batch` (
    `id`             bigint       NOT NULL  PRIMARY KEY,
    `product_id`     bigint       NOT NULL  COMMENT '商品/SKU ID',
    `batch_no`       varchar(64)  NOT NULL  COMMENT '批次号（如 PO20260701-001）',
    `quantity`       int          NOT NULL  COMMENT '这批入库总量',
    `available`      int          NOT NULL  COMMENT '这批剩余数量',
    `supplier`       varchar(128)          COMMENT '供应商',
    `purchase_price` decimal(10,2)        COMMENT '采购单价',
    `warehouse`      varchar(64)           COMMENT '仓库/库位',
    `inbound_time`   datetime(3)  NOT NULL COMMENT '入库时间',
    `expire_time`    datetime(3)           COMMENT '过期时间（保质期商品）',
    `status`         tinyint      NOT NULL  DEFAULT 1  COMMENT '1=正常 2=已出完 3=已报废',
    `create_time`    datetime(3)  NOT NULL,
    `update_time`    datetime(3)  NOT NULL,
    UNIQUE KEY `uk_batch_no` (`batch_no`),
    KEY `idx_product_id_available` (`product_id`, `available`, `inbound_time`),
    KEY `idx_inbound_time` (`inbound_time`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存批次';

-- 库存变动流水
CREATE TABLE IF NOT EXISTS `cloud_mall_inventory`.`inventory_log` (
    `id`            bigint       NOT NULL  PRIMARY KEY,
    `product_id`    bigint       NOT NULL  COMMENT '商品/SKU ID',
    `batch_id`      bigint                 COMMENT '关联批次ID',
    `type`          varchar(32)  NOT NULL  COMMENT 'INBOUND/OUTBOUND/FREEZE/UNFREEZE/CONFIRM/RETURN/SCRAP',
    `quantity`      int          NOT NULL  COMMENT '变动数量',
    `before_val`    int          NOT NULL  COMMENT '变动前值',
    `after_val`     int          NOT NULL  COMMENT '变动后值',
    `order_id`      bigint                 COMMENT '关联订单ID',
    `shipment_id`   bigint                 COMMENT '关联运单ID（预留物流）',
    `remark`        varchar(255)           COMMENT '备注',
    `create_time`   datetime(3)  NOT NULL,
    KEY `idx_product_id` (`product_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_batch_id` (`batch_id`),
    KEY `idx_type_create_time` (`type`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变动流水';

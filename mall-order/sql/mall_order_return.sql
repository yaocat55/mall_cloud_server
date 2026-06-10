Use mall_trade_3;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `t_order_return_apply_0` (
  `id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `order_code` varchar(64) NOT NULL COMMENT "订单编码",
  `user_id` bigint(20) NOT NULL COMMENT "用户ID",
  `reason` varchar(255) DEFAULT NULL COMMENT "退货原因",
  `description` varchar(512) DEFAULT NULL COMMENT "问题描述",
  `apply_status` int(11) DEFAULT 1 COMMENT "申请状态 1:申请中 2:已拒绝 3:待寄回 4:待质检 5:待入库 6:待退款 7:已完成 8:已取消",
  `refund_amount` decimal(10,2) DEFAULT 0 COMMENT "拟退款金额",
  `apply_time` datetime DEFAULT NULL COMMENT "申请时间",
  `audit_time` datetime DEFAULT NULL COMMENT "审核时间",
  `receive_time` datetime DEFAULT NULL COMMENT "收货时间(逆向)",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_code` (`order_code`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货申请表";

CREATE TABLE IF NOT EXISTS `t_order_return_apply_1` (
  `id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `order_code` varchar(64) NOT NULL COMMENT "订单编码",
  `user_id` bigint(20) NOT NULL COMMENT "用户ID",
  `reason` varchar(255) DEFAULT NULL COMMENT "退货原因",
  `description` varchar(512) DEFAULT NULL COMMENT "问题描述",
  `apply_status` int(11) DEFAULT 1 COMMENT "申请状态 1:申请中 2:已拒绝 3:待寄回 4:待质检 5:待入库 6:待退款 7:已完成 8:已取消",
  `refund_amount` decimal(10,2) DEFAULT 0 COMMENT "拟退款金额",
  `apply_time` datetime DEFAULT NULL COMMENT "申请时间",
  `audit_time` datetime DEFAULT NULL COMMENT "审核时间",
  `receive_time` datetime DEFAULT NULL COMMENT "收货时间(逆向)",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_code` (`order_code`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货申请表";

CREATE TABLE IF NOT EXISTS `t_order_return_apply_2` (
  `id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `order_code` varchar(64) NOT NULL COMMENT "订单编码",
  `user_id` bigint(20) NOT NULL COMMENT "用户ID",
  `reason` varchar(255) DEFAULT NULL COMMENT "退货原因",
  `description` varchar(512) DEFAULT NULL COMMENT "问题描述",
  `apply_status` int(11) DEFAULT 1 COMMENT "申请状态 1:申请中 2:已拒绝 3:待寄回 4:待质检 5:待入库 6:待退款 7:已完成 8:已取消",
  `refund_amount` decimal(10,2) DEFAULT 0 COMMENT "拟退款金额",
  `apply_time` datetime DEFAULT NULL COMMENT "申请时间",
  `audit_time` datetime DEFAULT NULL COMMENT "审核时间",
  `receive_time` datetime DEFAULT NULL COMMENT "收货时间(逆向)",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_code` (`order_code`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货申请表";

CREATE TABLE IF NOT EXISTS `t_order_return_apply_3` (
  `id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `order_code` varchar(64) NOT NULL COMMENT "订单编码",
  `user_id` bigint(20) NOT NULL COMMENT "用户ID",
  `reason` varchar(255) DEFAULT NULL COMMENT "退货原因",
  `description` varchar(512) DEFAULT NULL COMMENT "问题描述",
  `apply_status` int(11) DEFAULT 1 COMMENT "申请状态 1:申请中 2:已拒绝 3:待寄回 4:待质检 5:待入库 6:待退款 7:已完成 8:已取消",
  `refund_amount` decimal(10,2) DEFAULT 0 COMMENT "拟退款金额",
  `apply_time` datetime DEFAULT NULL COMMENT "申请时间",
  `audit_time` datetime DEFAULT NULL COMMENT "审核时间",
  `receive_time` datetime DEFAULT NULL COMMENT "收货时间(逆向)",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_code` (`order_code`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货申请表";

CREATE TABLE IF NOT EXISTS `t_order_return_item_0` (
  `id` bigint(20) NOT NULL COMMENT "退货明细ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `order_item_id` bigint(20) NOT NULL COMMENT "订单明细ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `product_id` bigint(20) NOT NULL COMMENT "商品ID",
  `product_name` varchar(128) NOT NULL COMMENT "商品名称",
  `product_model` varchar(255) DEFAULT NULL COMMENT "商品规格",
  `product_price` decimal(10,2) NOT NULL COMMENT "商品单价",
  `quantity` int(11) NOT NULL COMMENT "退货数量",
  `amount` decimal(10,2) NOT NULL COMMENT "金额",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`),
  KEY `idx_order_item_id` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货明细表";

CREATE TABLE IF NOT EXISTS `t_order_return_item_1` (
  `id` bigint(20) NOT NULL COMMENT "退货明细ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `order_item_id` bigint(20) NOT NULL COMMENT "订单明细ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `product_id` bigint(20) NOT NULL COMMENT "商品ID",
  `product_name` varchar(128) NOT NULL COMMENT "商品名称",
  `product_model` varchar(255) DEFAULT NULL COMMENT "商品规格",
  `product_price` decimal(10,2) NOT NULL COMMENT "商品单价",
  `quantity` int(11) NOT NULL COMMENT "退货数量",
  `amount` decimal(10,2) NOT NULL COMMENT "金额",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`),
  KEY `idx_order_item_id` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货明细表";

CREATE TABLE IF NOT EXISTS `t_order_return_item_2` (
  `id` bigint(20) NOT NULL COMMENT "退货明细ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `order_item_id` bigint(20) NOT NULL COMMENT "订单明细ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `product_id` bigint(20) NOT NULL COMMENT "商品ID",
  `product_name` varchar(128) NOT NULL COMMENT "商品名称",
  `product_model` varchar(255) DEFAULT NULL COMMENT "商品规格",
  `product_price` decimal(10,2) NOT NULL COMMENT "商品单价",
  `quantity` int(11) NOT NULL COMMENT "退货数量",
  `amount` decimal(10,2) NOT NULL COMMENT "金额",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`),
  KEY `idx_order_item_id` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货明细表";

CREATE TABLE IF NOT EXISTS `t_order_return_item_3` (
  `id` bigint(20) NOT NULL COMMENT "退货明细ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `order_item_id` bigint(20) NOT NULL COMMENT "订单明细ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `product_id` bigint(20) NOT NULL COMMENT "商品ID",
  `product_name` varchar(128) NOT NULL COMMENT "商品名称",
  `product_model` varchar(255) DEFAULT NULL COMMENT "商品规格",
  `product_price` decimal(10,2) NOT NULL COMMENT "商品单价",
  `quantity` int(11) NOT NULL COMMENT "退货数量",
  `amount` decimal(10,2) NOT NULL COMMENT "金额",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`),
  KEY `idx_order_item_id` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货明细表";

CREATE TABLE IF NOT EXISTS `t_order_return_item_4` (
  `id` bigint(20) NOT NULL COMMENT "退货明细ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `order_item_id` bigint(20) NOT NULL COMMENT "订单明细ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `product_id` bigint(20) NOT NULL COMMENT "商品ID",
  `product_name` varchar(128) NOT NULL COMMENT "商品名称",
  `product_model` varchar(255) DEFAULT NULL COMMENT "商品规格",
  `product_price` decimal(10,2) NOT NULL COMMENT "商品单价",
  `quantity` int(11) NOT NULL COMMENT "退货数量",
  `amount` decimal(10,2) NOT NULL COMMENT "金额",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`),
  KEY `idx_order_item_id` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货明细表";

CREATE TABLE IF NOT EXISTS `t_order_return_item_5` (
  `id` bigint(20) NOT NULL COMMENT "退货明细ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `order_item_id` bigint(20) NOT NULL COMMENT "订单明细ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `product_id` bigint(20) NOT NULL COMMENT "商品ID",
  `product_name` varchar(128) NOT NULL COMMENT "商品名称",
  `product_model` varchar(255) DEFAULT NULL COMMENT "商品规格",
  `product_price` decimal(10,2) NOT NULL COMMENT "商品单价",
  `quantity` int(11) NOT NULL COMMENT "退货数量",
  `amount` decimal(10,2) NOT NULL COMMENT "金额",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`),
  KEY `idx_order_item_id` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货明细表";

CREATE TABLE IF NOT EXISTS `t_order_return_item_6` (
  `id` bigint(20) NOT NULL COMMENT "退货明细ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `order_item_id` bigint(20) NOT NULL COMMENT "订单明细ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `product_id` bigint(20) NOT NULL COMMENT "商品ID",
  `product_name` varchar(128) NOT NULL COMMENT "商品名称",
  `product_model` varchar(255) DEFAULT NULL COMMENT "商品规格",
  `product_price` decimal(10,2) NOT NULL COMMENT "商品单价",
  `quantity` int(11) NOT NULL COMMENT "退货数量",
  `amount` decimal(10,2) NOT NULL COMMENT "金额",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`),
  KEY `idx_order_item_id` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货明细表";

CREATE TABLE IF NOT EXISTS `t_order_return_item_7` (
  `id` bigint(20) NOT NULL COMMENT "退货明细ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `order_item_id` bigint(20) NOT NULL COMMENT "订单明细ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `product_id` bigint(20) NOT NULL COMMENT "商品ID",
  `product_name` varchar(128) NOT NULL COMMENT "商品名称",
  `product_model` varchar(255) DEFAULT NULL COMMENT "商品规格",
  `product_price` decimal(10,2) NOT NULL COMMENT "商品单价",
  `quantity` int(11) NOT NULL COMMENT "退货数量",
  `amount` decimal(10,2) NOT NULL COMMENT "金额",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`),
  KEY `idx_order_item_id` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货明细表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_0` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_1` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_2` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_3` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_4` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_5` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_6` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_7` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_8` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_9` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_10` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_11` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_12` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_13` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_14` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

CREATE TABLE IF NOT EXISTS `t_order_return_voucher_15` (
  `id` bigint(20) NOT NULL COMMENT "退货凭证ID",
  `order_id` bigint(20) NOT NULL COMMENT "订单ID",
  `return_apply_id` bigint(20) NOT NULL COMMENT "退货申请ID",
  `url` varchar(512) NOT NULL COMMENT "凭证图片URL",
  `create_user_id` bigint(20) DEFAULT NULL COMMENT "创建人ID",
  `create_user_name` varchar(64) DEFAULT NULL COMMENT "创建人名称",
  `create_time` datetime DEFAULT NULL COMMENT "创建时间",
  `update_user_id` bigint(20) DEFAULT NULL COMMENT "更新人ID",
  `update_user_name` varchar(64) DEFAULT NULL COMMENT "更新人名称",
  `update_time` datetime DEFAULT NULL COMMENT "更新时间",
  `is_del` tinyint(4) DEFAULT "0" COMMENT "是否删除 0:否 1:是",
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_return_apply_id` (`return_apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="退货凭证表";

SET FOREIGN_KEY_CHECKS = 1;


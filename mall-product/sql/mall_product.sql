CREATE TABLE IF NOT EXISTS `product_comment` (
  `id` bigint(20) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `user_name` varchar(64) DEFAULT NULL,
  `content` varchar(1024) DEFAULT NULL,
  `rating` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `create_user_id` bigint(20) DEFAULT NULL,
  `create_user_name` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_user_id` bigint(20) DEFAULT NULL,
  `update_user_name` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_del` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `product_comment_photo` (
  `id` bigint(20) NOT NULL,
  `comment_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `url` varchar(255) NOT NULL,
  `sort` int(11) DEFAULT 0,
  `create_user_id` bigint(20) DEFAULT NULL,
  `create_user_name` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_user_id` bigint(20) DEFAULT NULL,
  `update_user_name` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_del` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_comment_id` (`comment_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

alter table product_comment add column `order_id` bigint(20)  NULL;
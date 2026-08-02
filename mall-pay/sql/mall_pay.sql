-- ============================================================
-- 统一支付服务数据库初始化脚本
-- 数据库：cloud_mall_pay（本地 MySQL，root/123456，端口 33081）
-- 共 8 张表：业务侧 5 张 + 对账侧 3 张
-- 对应设计文档：docs/37-统一支付服务设计方案.md
-- ============================================================

CREATE DATABASE IF NOT EXISTS `cloud_mall_pay` DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;

USE `cloud_mall_pay`;

-- ------------------------------------------------------------
-- 1. pay_order 支付订单表（核心）—— 用户 → 商户 资金流入
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `pay_order` (
    `id`                 bigint       NOT NULL              COMMENT '主键，雪花ID',
    `pay_order_no`       varchar(32)  NOT NULL              COMMENT '支付订单号（对外交易号，雪花ID字符串）',
    `biz_order_no`       varchar(64)  NOT NULL              COMMENT '业务方订单号（如商城 tradeCode）',
    `biz_type`           varchar(32)  NOT NULL              COMMENT '业务类型：MALL_ORDER 商城订单 / OTHER 其他',
    `channel_code`       varchar(16)  NOT NULL              COMMENT '支付渠道：ALIPAY / WECHAT_PAY / WECHAT_MINI / MOCK（模拟渠道，仅 dev/test 启用）',
    `merchant_order_no`  varchar(64)  NOT NULL              COMMENT '渠道商户订单号（本地唯一，渠道侧可回溯）',
    `channel_trade_no`   varchar(64)          DEFAULT NULL   COMMENT '渠道交易号（回调成功后回填）',
    `user_id`            bigint       NOT NULL              COMMENT '用户ID',
    `open_id`            varchar(64)          DEFAULT NULL   COMMENT '微信 openId（小程序支付必填）',
    `subject`            varchar(128) NOT NULL              COMMENT '商品描述（收银台展示）',
    `body`               varchar(512)         DEFAULT NULL   COMMENT '商品详情',
    `total_amount`       bigint       NOT NULL              COMMENT '订单总金额（单位：分）',
    `pay_amount`         bigint       NOT NULL              COMMENT '实际支付金额（单位：分，默认=total_amount）',
    `refund_amount`      bigint       NOT NULL DEFAULT 0    COMMENT '已退款金额（单位：分）',
    `currency`           varchar(8)   NOT NULL DEFAULT 'CNY' COMMENT '币种',
    `pay_status`         tinyint      NOT NULL DEFAULT 10   COMMENT '支付状态：10待支付 20已支付 30已关闭 40支付失败',
    `refund_status`      tinyint      NOT NULL DEFAULT 0    COMMENT '退款状态：0无 1退款中 2部分退款 3全额退款 4退款失败',
    `notify_count`       int          NOT NULL DEFAULT 0    COMMENT '渠道回调次数',
    `notify_time`        datetime(3)          DEFAULT NULL   COMMENT '最近一次回调时间',
    `biz_notify_status`  tinyint      NOT NULL DEFAULT 0    COMMENT '业务方通知状态：0待通知 1通知成功 2通知失败',
    `client_ip`          varchar(64)          DEFAULT NULL   COMMENT '客户端IP',
    `device_info`        varchar(128)         DEFAULT NULL   COMMENT '设备信息',
    `success_time`       datetime(3)          DEFAULT NULL   COMMENT '支付成功时间',
    `expire_time`        datetime(3)          DEFAULT NULL   COMMENT '支付过期时间（超时自动关闭）',
    `closed_time`        datetime(3)          DEFAULT NULL   COMMENT '关闭时间',
    `version`            int          NOT NULL DEFAULT 0    COMMENT '乐观锁',
    `create_user_id`     bigint       NOT NULL              COMMENT '创建人ID',
    `create_user_name`   varchar(64)  NOT NULL              COMMENT '创建人姓名',
    `create_time`        datetime(3)  NOT NULL              COMMENT '创建时间',
    `update_user_id`     bigint       NOT NULL              COMMENT '更新人ID',
    `update_user_name`   varchar(64)  NOT NULL              COMMENT '更新人姓名',
    `update_time`        datetime(3)  NOT NULL              COMMENT '更新时间',
    `remark`             varchar(255)         DEFAULT NULL   COMMENT '备注',
    UNIQUE KEY `uk_pay_order_no` (`pay_order_no`),
    UNIQUE KEY `uk_merchant_order_no` (`merchant_order_no`),
    KEY `idx_biz_order` (`biz_type`, `biz_order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_channel_trade_no` (`channel_code`, `channel_trade_no`),
    KEY `idx_pay_status_create_time` (`pay_status`, `create_time`),
    KEY `idx_channel_success_time` (`channel_code`, `success_time`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付订单表';

-- ------------------------------------------------------------
-- 2. pay_notify_log 回调日志表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `pay_notify_log` (
    `id`               bigint       NOT NULL PRIMARY KEY  COMMENT '主键，雪花ID',
    `pay_order_id`     bigint       NOT NULL              COMMENT '支付订单ID',
    `pay_order_no`     varchar(32)  NOT NULL              COMMENT '支付订单号',
    `channel_code`     varchar(16)  NOT NULL              COMMENT '渠道编码',
    `channel_trade_no` varchar(64)          DEFAULT NULL   COMMENT '渠道交易号',
    `notify_type`      varchar(16)  NOT NULL              COMMENT '通知类型：PAY 支付 / REFUND 退款',
    `raw_data`         text         NOT NULL              COMMENT '渠道原始报文',
    `decrypt_data`     text                DEFAULT NULL   COMMENT '解密后报文（微信需要解密）',
    `verify_status`    tinyint      NOT NULL DEFAULT 0    COMMENT '验签结果：0未验证 1成功 2失败',
    `process_status`   tinyint      NOT NULL DEFAULT 0    COMMENT '处理状态：0待处理 1处理成功 2处理失败 3重复通知忽略',
    `process_msg`      varchar(512)         DEFAULT NULL   COMMENT '处理结果信息',
    `create_time`      datetime(3)  NOT NULL              COMMENT '创建时间',
    KEY `idx_pay_order_id` (`pay_order_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付渠道回调日志';

-- ------------------------------------------------------------
-- 3. pay_refund 退款表 —— 商户 → 用户 资金流出
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `pay_refund` (
    `id`                bigint       NOT NULL PRIMARY KEY  COMMENT '主键，雪花ID',
    `refund_no`         varchar(32)  NOT NULL              COMMENT '退款单号（雪花ID字符串）',
    `pay_order_id`      bigint       NOT NULL              COMMENT '支付订单ID',
    `pay_order_no`      varchar(32)  NOT NULL              COMMENT '支付订单号',
    `biz_order_no`      varchar(64)  NOT NULL              COMMENT '业务方订单号',
    `biz_type`          varchar(32)  NOT NULL              COMMENT '业务类型：MALL_ORDER 商城订单 / OTHER 其他（冗余自 pay_order）',
    `user_id`           bigint       NOT NULL              COMMENT '用户ID（冗余自 pay_order，供按用户追溯退款）',
    `channel_code`      varchar(16)  NOT NULL              COMMENT '渠道编码',
    `channel_refund_no` varchar(64)          DEFAULT NULL   COMMENT '渠道退款单号',
    `refund_amount`     bigint       NOT NULL              COMMENT '退款金额（分）',
    `refund_fee`        bigint       NOT NULL DEFAULT 0    COMMENT '退款手续费（分，微信等比例退还手续费，对账 FEE 对齐用）',
    `refund_reason`     varchar(255)         DEFAULT NULL   COMMENT '退款原因',
    `refund_type`       tinyint      NOT NULL DEFAULT 1    COMMENT '退款类型：1用户申请 2系统自动 3人工介入',
    `audit_status`      tinyint      NOT NULL DEFAULT 0    COMMENT '审核状态：0无需审核 1待审核 2审核通过 3审核拒绝',
    `refund_status`     tinyint      NOT NULL DEFAULT 0    COMMENT '退款状态：0待处理 1处理中 2退款成功 3退款失败',
    `audit_user_id`     bigint                DEFAULT NULL   COMMENT '审核人ID',
    `audit_user_name`   varchar(64)           DEFAULT NULL   COMMENT '审核人姓名',
    `audit_time`        datetime(3)           DEFAULT NULL   COMMENT '审核时间',
    `success_time`      datetime(3)           DEFAULT NULL   COMMENT '退款成功时间',
    `fail_reason`       varchar(255)          DEFAULT NULL   COMMENT '退款失败原因',
    `version`           int          NOT NULL DEFAULT 0    COMMENT '乐观锁',
    `create_user_id`    bigint       NOT NULL              COMMENT '创建人ID',
    `create_user_name`  varchar(64)  NOT NULL              COMMENT '创建人姓名',
    `create_time`       datetime(3)  NOT NULL              COMMENT '创建时间',
    `update_user_id`    bigint       NOT NULL              COMMENT '更新人ID',
    `update_user_name`  varchar(64)  NOT NULL              COMMENT '更新人姓名',
    `update_time`       datetime(3)  NOT NULL              COMMENT '更新时间',
    `remark`            varchar(255)         DEFAULT NULL   COMMENT '备注',
    UNIQUE KEY `uk_refund_no` (`refund_no`),
    KEY `idx_pay_order_id` (`pay_order_id`),
    KEY `idx_biz_order_no` (`biz_type`, `biz_order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_channel_refund_no` (`channel_code`, `channel_refund_no`),
    KEY `idx_channel_success_time` (`channel_code`, `success_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付退款单';

-- ------------------------------------------------------------
-- 4. pay_channel_config 渠道配置表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `pay_channel_config` (
    `id`                  bigint       NOT NULL PRIMARY KEY  COMMENT '主键，雪花ID',
    `channel_code`        varchar(16)  NOT NULL              COMMENT '渠道编码：ALIPAY / WECHAT_PAY / WECHAT_MINI / MOCK',
    `channel_name`        varchar(64)  NOT NULL              COMMENT '渠道名称',
    `app_id`              varchar(64)  NOT NULL              COMMENT '应用ID/AppID',
    `merchant_id`         varchar(64)          DEFAULT NULL   COMMENT '商户号/Partner',
    `app_secret`          text                DEFAULT NULL   COMMENT 'AppSecret（AES 加密存储）',
    `public_key`          text                DEFAULT NULL   COMMENT '渠道公钥（AES 加密存储）',
    `private_key`         text                DEFAULT NULL   COMMENT '商户私钥/APIv3密钥（AES 加密存储）',
    `notify_url`          varchar(255)         DEFAULT NULL   COMMENT '支付回调地址',
    `refund_notify_url`   varchar(255)         DEFAULT NULL   COMMENT '退款回调地址',
    `bill_download_url`   varchar(255)         DEFAULT NULL   COMMENT '对账单下载地址模板',
    `config_json`         json                DEFAULT NULL   COMMENT '扩展配置（签约模式/商户证书路径等）',
    `status`              tinyint      NOT NULL DEFAULT 1    COMMENT '状态：1启用 0禁用',
    `version`             int          NOT NULL DEFAULT 0    COMMENT '乐观锁',
    `create_user_id`      bigint       NOT NULL              COMMENT '创建人ID',
    `create_user_name`    varchar(64)  NOT NULL              COMMENT '创建人姓名',
    `create_time`         datetime(3)  NOT NULL              COMMENT '创建时间',
    `update_user_id`      bigint       NOT NULL              COMMENT '更新人ID',
    `update_user_name`    varchar(64)  NOT NULL              COMMENT '更新人姓名',
    `update_time`         datetime(3)  NOT NULL              COMMENT '更新时间',
    UNIQUE KEY `uk_channel_code` (`channel_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付渠道配置';

-- ------------------------------------------------------------
-- 5. pay_biz_config 业务渠道接入表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `pay_biz_config` (
    `id`                 bigint       NOT NULL PRIMARY KEY  COMMENT '主键，雪花ID',
    `biz_type`           varchar(32)  NOT NULL              COMMENT '业务类型编码（唯一，如 MALL_ORDER）',
    `biz_name`           varchar(64)  NOT NULL              COMMENT '业务方名称（如 商城订单）',
    `notify_mq_topic`    varchar(64)  NOT NULL              COMMENT '业务方接收支付结果通知的 MQ Topic',
    `notify_mq_tag`      varchar(64)          DEFAULT NULL   COMMENT 'MQ Tag（可选，用于过滤）',
    `sign_key`           text                DEFAULT NULL   COMMENT '业务方回调验签密钥（AES 加密存储，预留）',
    `status`             tinyint      NOT NULL DEFAULT 1    COMMENT '状态：1启用 0禁用',
    `remark`             varchar(255)         DEFAULT NULL   COMMENT '备注',
    `create_user_id`     bigint       NOT NULL              COMMENT '创建人ID',
    `create_user_name`   varchar(64)  NOT NULL              COMMENT '创建人姓名',
    `create_time`        datetime(3)  NOT NULL              COMMENT '创建时间',
    `update_user_id`     bigint       NOT NULL              COMMENT '更新人ID',
    `update_user_name`   varchar(64)  NOT NULL              COMMENT '更新人姓名',
    `update_time`        datetime(3)  NOT NULL              COMMENT '更新时间',
    UNIQUE KEY `uk_biz_type` (`biz_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务渠道接入表';

-- ------------------------------------------------------------
-- 6. recon_batch 对账批次表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `recon_batch` (
    `id`                    bigint      NOT NULL PRIMARY KEY  COMMENT '主键，雪花ID',
    `batch_no`              varchar(32) NOT NULL              COMMENT '批次号：RECON{yyyyMMdd}{渠道}{序号}',
    `channel_code`          varchar(16) NOT NULL              COMMENT '渠道编码',
    `trade_date`            date        NOT NULL              COMMENT '对账日',
    `file_name`             varchar(255)         DEFAULT NULL  COMMENT '对账单文件名',
    `file_path`             varchar(255)         DEFAULT NULL  COMMENT '文件存储路径（MinIO）',
    `channel_count`         int         NOT NULL DEFAULT 0    COMMENT '渠道侧交易总笔数',
    `channel_total_amount`  bigint      NOT NULL DEFAULT 0    COMMENT '渠道侧交易总额（分）',
    `platform_count`        int         NOT NULL DEFAULT 0    COMMENT '本地匹配交易笔数',
    `platform_total_amount` bigint      NOT NULL DEFAULT 0    COMMENT '本地匹配交易总额（分）',
    `diff_count`            int         NOT NULL DEFAULT 0    COMMENT '差异笔数',
    `diff_total_amount`     bigint      NOT NULL DEFAULT 0    COMMENT '差异总金额（分）',
    `status`                tinyint     NOT NULL DEFAULT 1    COMMENT '状态：1已下载 2已解析入库 3对账中 4对账完成 5有差异待处理 6差异已处理 7失败',
    `begin_time`            datetime(3)          DEFAULT NULL  COMMENT '对账开始时间',
    `end_time`              datetime(3)          DEFAULT NULL  COMMENT '对账结束时间',
    `error_msg`             varchar(512)         DEFAULT NULL  COMMENT '失败原因',
    `version`               int         NOT NULL DEFAULT 0    COMMENT '乐观锁',
    `create_user_id`        bigint      NOT NULL              COMMENT '创建人ID',
    `create_user_name`      varchar(64) NOT NULL              COMMENT '创建人姓名',
    `create_time`           datetime(3) NOT NULL              COMMENT '创建时间',
    `update_user_id`        bigint      NOT NULL              COMMENT '更新人ID',
    `update_user_name`      varchar(64) NOT NULL              COMMENT '更新人姓名',
    `update_time`           datetime(3) NOT NULL              COMMENT '更新时间',
    UNIQUE KEY `uk_channel_trade_date` (`channel_code`, `trade_date`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对账批次表';

-- ------------------------------------------------------------
-- 7. recon_temp 对账临时表（核心）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `recon_temp` (
    `id`                bigint       NOT NULL AUTO_INCREMENT  COMMENT '自增主键（临时表专用，非雪花）',
    `batch_no`          varchar(32)  NOT NULL                 COMMENT '对账批次号',
    `line_no`           int          NOT NULL                 COMMENT '文件行号',
    `trade_no`          varchar(64)  NOT NULL                 COMMENT '渠道商户订单号（= merchant_order_no）',
    `channel_trade_no`  varchar(64)           DEFAULT NULL    COMMENT '渠道交易流水号',
    `refund_no`         varchar(64)           DEFAULT NULL    COMMENT '渠道退款单号（退款记录行才有）',
    `trade_time`        datetime     NOT NULL                 COMMENT '交易时间',
    `trade_type`        varchar(16)  NOT NULL                 COMMENT '交易类型：PAY 支付 / REFUND 退款',
    `amount`            bigint       NOT NULL                 COMMENT '交易金额（分，正数）',
    `fee`               bigint       NOT NULL DEFAULT 0       COMMENT '手续费（分）',
    `income`            bigint       NOT NULL DEFAULT 0       COMMENT '净入账金额（分，amount - fee，可正可负）',
    `trade_status`      varchar(16)           DEFAULT NULL    COMMENT '渠道交易状态：SUCCESS/REFUND/CLOSED',
    `payer_account`     varchar(64)           DEFAULT NULL    COMMENT '付款方账号',
    `ext_json`          json                  DEFAULT NULL    COMMENT '渠道特有字段（原样保留）',
    `create_time`       datetime(3)  NOT NULL                 COMMENT '入库时间',
    PRIMARY KEY (`id`),
    KEY `idx_batch_no` (`batch_no`),
    KEY `idx_batch_trade_no` (`batch_no`, `trade_no`),
    KEY `idx_batch_trade_time` (`batch_no`, `trade_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对账临时表（按批次批量插入，对账完成保留N天后清理）';

-- ------------------------------------------------------------
-- 8. recon_result 对账结果表（逐笔差异）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `recon_result` (
    `id`               bigint       NOT NULL PRIMARY KEY  COMMENT '主键，雪花ID',
    `batch_no`         varchar(32)  NOT NULL              COMMENT '对账批次号',
    `channel_code`     varchar(16)  NOT NULL              COMMENT '渠道编码',
    `trade_no`         varchar(64)  NOT NULL              COMMENT '渠道商户订单号',
    `channel_trade_no` varchar(64)          DEFAULT NULL   COMMENT '渠道交易号',
    `pay_order_id`     bigint                DEFAULT NULL   COMMENT '本地支付订单ID',
    `pay_order_no`     varchar(32)           DEFAULT NULL   COMMENT '本地支付订单号',
    `trade_time`       datetime              DEFAULT NULL   COMMENT '交易时间',
    `platform_amount`  bigint       NOT NULL DEFAULT 0    COMMENT '平台侧金额（分，退款记负）',
    `channel_amount`   bigint       NOT NULL DEFAULT 0    COMMENT '渠道侧金额（分，退款记负）',
    `diff_type`        varchar(32)  NOT NULL              COMMENT '差异类型：LONG_PAYMENT/SHORT_PAYMENT/AMOUNT_MISMATCH/ONLY_PLATFORM/ONLY_CHANNEL/FEE_MISMATCH/STATUS_MISMATCH',
    `diff_amount`      bigint       NOT NULL DEFAULT 0    COMMENT '差异金额（分，带方向）',
    `handle_status`    tinyint      NOT NULL DEFAULT 0    COMMENT '处理状态：0待处理 1处理中 2已处理 3无需处理',
    `handle_type`      tinyint                DEFAULT NULL COMMENT '处理方式：1自动冲正 2自动退款 3自动补单 4人工处理',
    `handle_result`    varchar(512)          DEFAULT NULL   COMMENT '处理结果说明',
    `handle_user_id`   bigint                DEFAULT NULL   COMMENT '处理人ID',
    `handle_time`      datetime(3)           DEFAULT NULL   COMMENT '处理时间',
    `version`          int          NOT NULL DEFAULT 0    COMMENT '乐观锁',
    `create_time`      datetime(3)  NOT NULL              COMMENT '创建时间',
    `update_time`      datetime(3)  NOT NULL              COMMENT '更新时间',
    KEY `idx_batch_no` (`batch_no`),
    KEY `idx_handle_status` (`handle_status`),
    KEY `idx_trade_no` (`trade_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对账逐笔结果表';

-- ------------------------------------------------------------
-- 初始数据：商城订单业务方注册
-- ------------------------------------------------------------
INSERT INTO `pay_biz_config`
    (`id`, `biz_type`, `biz_name`, `notify_mq_topic`, `status`, `create_user_id`, `create_user_name`, `create_time`, `update_user_id`, `update_user_name`, `update_time`)
VALUES
    (1, 'MALL_ORDER', '商城订单', 'PAY_NOTIFY_TOPIC', 1, 1, '系统管理员', NOW(3), 1, '系统管理员', NOW(3));

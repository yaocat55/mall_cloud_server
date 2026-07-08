-- ============================================================
-- 数据库重命名脚本：旧库名 → cloud_* 前缀
-- 用法：mysql -h localhost -P 33081 -u root -p123456 < rename.sql
-- ============================================================

-- ========== 1. 创建新库 ==========
CREATE DATABASE IF NOT EXISTS cloud_admin     DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_basic     DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_product   DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_marketing DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_member    DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_trade_0   DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_trade_1   DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_trade_2   DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_trade_3   DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_trade_4   DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_trade_5   DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_trade_6   DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_trade_7   DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_message_0 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_message_1 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_message_2 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_message_3 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_message_4 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_message_5 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_message_6 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_message_7 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_recommend_0 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS cloud_recommend_1 DEFAULT CHARACTER SET utf8mb4;

-- ========== 2. 迁移单库表 ==========

-- susan_mall_auth → cloud_admin（23 张表，含 auth + 遗留 product 表）
RENAME TABLE susan_mall_auth.attribute            TO cloud_admin.attribute;
RENAME TABLE susan_mall_auth.attribute_value      TO cloud_admin.attribute_value;
RENAME TABLE susan_mall_auth.auth_dept            TO cloud_admin.auth_dept;
RENAME TABLE susan_mall_auth.auth_job             TO cloud_admin.auth_job;
RENAME TABLE susan_mall_auth.auth_menu            TO cloud_admin.auth_menu;
RENAME TABLE susan_mall_auth.auth_role            TO cloud_admin.auth_role;
RENAME TABLE susan_mall_auth.auth_role_dept       TO cloud_admin.auth_role_dept;
RENAME TABLE susan_mall_auth.auth_role_menu       TO cloud_admin.auth_role_menu;
RENAME TABLE susan_mall_auth.auth_user            TO cloud_admin.auth_user;
RENAME TABLE susan_mall_auth.auth_user_avatar     TO cloud_admin.auth_user_avatar;
RENAME TABLE susan_mall_auth.auth_user_role       TO cloud_admin.auth_user_role;
RENAME TABLE susan_mall_auth.brand                TO cloud_admin.brand;
RENAME TABLE susan_mall_auth.category             TO cloud_admin.category;
RENAME TABLE susan_mall_auth.product              TO cloud_admin.product;
RENAME TABLE susan_mall_auth.product_attribute    TO cloud_admin.product_attribute;
RENAME TABLE susan_mall_auth.product_comment      TO cloud_admin.product_comment;
RENAME TABLE susan_mall_auth.product_detail       TO cloud_admin.product_detail;
RENAME TABLE susan_mall_auth.product_favorites    TO cloud_admin.product_favorites;
RENAME TABLE susan_mall_auth.product_group        TO cloud_admin.product_group;
RENAME TABLE susan_mall_auth.product_group_attribute TO cloud_admin.product_group_attribute;
RENAME TABLE susan_mall_auth.product_photo        TO cloud_admin.product_photo;
RENAME TABLE susan_mall_auth.product_view_record  TO cloud_admin.product_view_record;
RENAME TABLE susan_mall_auth.unit                 TO cloud_admin.unit;

-- susan_mall_basic → cloud_basic（12 张表）
RENAME TABLE susan_mall_basic.common_area          TO cloud_basic.common_area;
RENAME TABLE susan_mall_basic.common_biz_log       TO cloud_basic.common_biz_log;
RENAME TABLE susan_mall_basic.common_dict          TO cloud_basic.common_dict;
RENAME TABLE susan_mall_basic.common_dict_detail   TO cloud_basic.common_dict_detail;
RENAME TABLE susan_mall_basic.common_job           TO cloud_basic.common_job;
RENAME TABLE susan_mall_basic.common_job_log       TO cloud_basic.common_job_log;
RENAME TABLE susan_mall_basic.common_notify        TO cloud_basic.common_notify;
RENAME TABLE susan_mall_basic.common_photo         TO cloud_basic.common_photo;
RENAME TABLE susan_mall_basic.common_photo_group   TO cloud_basic.common_photo_group;
RENAME TABLE susan_mall_basic.common_sensitive_word TO cloud_basic.common_sensitive_word;
RENAME TABLE susan_mall_basic.common_sms_record    TO cloud_basic.common_sms_record;
RENAME TABLE susan_mall_basic.common_task          TO cloud_basic.common_task;

-- susan_mall_product → cloud_product（17 张表）
RENAME TABLE susan_mall_product.attribute               TO cloud_product.attribute;
RENAME TABLE susan_mall_product.attribute_value         TO cloud_product.attribute_value;
RENAME TABLE susan_mall_product.brand                   TO cloud_product.brand;
RENAME TABLE susan_mall_product.category                TO cloud_product.category;
RENAME TABLE susan_mall_product.mall_index_carousel_image TO cloud_product.mall_index_carousel_image;
RENAME TABLE susan_mall_product.mall_index_notice       TO cloud_product.mall_index_notice;
RENAME TABLE susan_mall_product.mall_index_product      TO cloud_product.mall_index_product;
RENAME TABLE susan_mall_product.product                 TO cloud_product.product;
RENAME TABLE susan_mall_product.product_attribute       TO cloud_product.product_attribute;
RENAME TABLE susan_mall_product.product_comment         TO cloud_product.product_comment;
RENAME TABLE susan_mall_product.product_detail          TO cloud_product.product_detail;
RENAME TABLE susan_mall_product.product_favorites       TO cloud_product.product_favorites;
RENAME TABLE susan_mall_product.product_group           TO cloud_product.product_group;
RENAME TABLE susan_mall_product.product_group_attribute TO cloud_product.product_group_attribute;
RENAME TABLE susan_mall_product.product_photo           TO cloud_product.product_photo;
RENAME TABLE susan_mall_product.product_view_record     TO cloud_product.product_view_record;
RENAME TABLE susan_mall_product.unit                    TO cloud_product.unit;

-- susan_mall_marketing → cloud_marketing（4 张表）
RENAME TABLE susan_mall_marketing.coupon              TO cloud_marketing.coupon;
RENAME TABLE susan_mall_marketing.coupon_user_provide TO cloud_marketing.coupon_user_provide;
RENAME TABLE susan_mall_marketing.coupon_user_receive TO cloud_marketing.coupon_user_receive;
RENAME TABLE susan_mall_marketing.seckill_product     TO cloud_marketing.seckill_product;

-- mall_member → cloud_member（1 张表）
RENAME TABLE mall_member.ums_member TO cloud_member.ums_member;

-- ========== 3. 迁移分库分表（仅示例 mall_trade_0 / mall_message_0 / mall_recommend_0） ==========
-- 完整脚本太长，用下面 Python 脚本生成所有分片的 RENAME

-- mall_trade_0 → cloud_trade_0（40 张表）
RENAME TABLE mall_trade_0.t_order_0                 TO cloud_trade_0.t_order_0;
RENAME TABLE mall_trade_0.t_order_16                TO cloud_trade_0.t_order_16;
RENAME TABLE mall_trade_0.t_order_24                TO cloud_trade_0.t_order_24;
RENAME TABLE mall_trade_0.t_order_8                 TO cloud_trade_0.t_order_8;
RENAME TABLE mall_trade_0.t_order_delivery_address_0  TO cloud_trade_0.t_order_delivery_address_0;
RENAME TABLE mall_trade_0.t_order_delivery_address_16 TO cloud_trade_0.t_order_delivery_address_16;
RENAME TABLE mall_trade_0.t_order_delivery_address_24 TO cloud_trade_0.t_order_delivery_address_24;
RENAME TABLE mall_trade_0.t_order_delivery_address_8  TO cloud_trade_0.t_order_delivery_address_8;
RENAME TABLE mall_trade_0.t_order_item_0 TO cloud_trade_0.t_order_item_0;
RENAME TABLE mall_trade_0.t_order_item_104 TO cloud_trade_0.t_order_item_104;
RENAME TABLE mall_trade_0.t_order_item_112 TO cloud_trade_0.t_order_item_112;
RENAME TABLE mall_trade_0.t_order_item_120 TO cloud_trade_0.t_order_item_120;
RENAME TABLE mall_trade_0.t_order_item_128 TO cloud_trade_0.t_order_item_128;
RENAME TABLE mall_trade_0.t_order_item_136 TO cloud_trade_0.t_order_item_136;
RENAME TABLE mall_trade_0.t_order_item_144 TO cloud_trade_0.t_order_item_144;
RENAME TABLE mall_trade_0.t_order_item_152 TO cloud_trade_0.t_order_item_152;
RENAME TABLE mall_trade_0.t_order_item_16 TO cloud_trade_0.t_order_item_16;
RENAME TABLE mall_trade_0.t_order_item_160 TO cloud_trade_0.t_order_item_160;
RENAME TABLE mall_trade_0.t_order_item_168 TO cloud_trade_0.t_order_item_168;
RENAME TABLE mall_trade_0.t_order_item_176 TO cloud_trade_0.t_order_item_176;
RENAME TABLE mall_trade_0.t_order_item_184 TO cloud_trade_0.t_order_item_184;
RENAME TABLE mall_trade_0.t_order_item_192 TO cloud_trade_0.t_order_item_192;
RENAME TABLE mall_trade_0.t_order_item_200 TO cloud_trade_0.t_order_item_200;
RENAME TABLE mall_trade_0.t_order_item_208 TO cloud_trade_0.t_order_item_208;
RENAME TABLE mall_trade_0.t_order_item_216 TO cloud_trade_0.t_order_item_216;
RENAME TABLE mall_trade_0.t_order_item_224 TO cloud_trade_0.t_order_item_224;
RENAME TABLE mall_trade_0.t_order_item_232 TO cloud_trade_0.t_order_item_232;
RENAME TABLE mall_trade_0.t_order_item_24 TO cloud_trade_0.t_order_item_24;
RENAME TABLE mall_trade_0.t_order_item_240 TO cloud_trade_0.t_order_item_240;
RENAME TABLE mall_trade_0.t_order_item_248 TO cloud_trade_0.t_order_item_248;
RENAME TABLE mall_trade_0.t_order_item_32 TO cloud_trade_0.t_order_item_32;
RENAME TABLE mall_trade_0.t_order_item_40 TO cloud_trade_0.t_order_item_40;
RENAME TABLE mall_trade_0.t_order_item_48 TO cloud_trade_0.t_order_item_48;
RENAME TABLE mall_trade_0.t_order_item_56 TO cloud_trade_0.t_order_item_56;
RENAME TABLE mall_trade_0.t_order_item_64 TO cloud_trade_0.t_order_item_64;
RENAME TABLE mall_trade_0.t_order_item_72 TO cloud_trade_0.t_order_item_72;
RENAME TABLE mall_trade_0.t_order_item_8 TO cloud_trade_0.t_order_item_8;
RENAME TABLE mall_trade_0.t_order_item_80 TO cloud_trade_0.t_order_item_80;
RENAME TABLE mall_trade_0.t_order_item_88 TO cloud_trade_0.t_order_item_88;
RENAME TABLE mall_trade_0.t_order_item_96 TO cloud_trade_0.t_order_item_96;

/* 自动生成的消息分库分表 SQL - Database Prefix: mall_message */

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mall_message_0 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS mall_message_1 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS mall_message_2 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS mall_message_3 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS mall_message_4 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS mall_message_5 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS mall_message_6 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS mall_message_7 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 每个库创建 64 张分表
DELIMITER $$
CREATE PROCEDURE create_common_notify_tables(IN db_prefix VARCHAR(64))
BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i < 64 DO
        SET @sql = CONCAT(
            'CREATE TABLE IF NOT EXISTS ', db_prefix, '.common_notify_', i, ' (',
            '  id BIGINT NOT NULL,',
            '  type INT,',
            '  title VARCHAR(255),',
            '  content TEXT,',
            '  link_url VARCHAR(512),',
            '  read_status INT,',
            '  to_user_id BIGINT,',
            '  is_push INT,',
            '  create_user_id BIGINT,',
            '  create_user_name VARCHAR(64),',
            '  create_time DATETIME(3),',
            '  update_user_id BIGINT,',
            '  update_user_name VARCHAR(64),',
            '  update_time DATETIME(3),',
            '  is_del INT DEFAULT 0,',
            '  PRIMARY KEY (id),',
            '  KEY idx_to_user_id (to_user_id),',
            '  KEY idx_create_time (create_time)',
            ');'
        );
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SET i = i + 1;
    END WHILE;
END $$
DELIMITER ;

CALL create_common_notify_tables('mall_message_0');
CALL create_common_notify_tables('mall_message_1');
CALL create_common_notify_tables('mall_message_2');
CALL create_common_notify_tables('mall_message_3');
CALL create_common_notify_tables('mall_message_4');
CALL create_common_notify_tables('mall_message_5');
CALL create_common_notify_tables('mall_message_6');
CALL create_common_notify_tables('mall_message_7');

-- 清理存储过程
DROP PROCEDURE IF EXISTS create_common_notify_tables;

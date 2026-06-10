package cn.net.mall.order.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;

import org.yaml.snakeyaml.Yaml;

@Component
public class SchemaBootstrap implements ApplicationRunner {

    private final Environment environment;

    public SchemaBootstrap(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean enable = environment.getProperty("app.schema.bootstrap.enable", Boolean.class, false);
        if (!enable) {
            return;
        }
        Map<String, Map<String, String>> ds = parseDataSources();
        List<JdbcInfo> infos = toJdbcInfos(ds);
        for (JdbcInfo info : infos) {
            try (Connection conn = DriverManager.getConnection(info.url, info.username, info.password);
                 Statement stmt = conn.createStatement()) {
                String db = extractDbName(info.url);
                exec(stmt, "CREATE DATABASE IF NOT EXISTS " + db);
                exec(stmt, "USE " + db);
                createUndoLog(stmt);
                int idx = extractDbIndex(db);
                createOrderTables(stmt, idx);
                createOrderItemTables(stmt, idx);
                createOrderAddressTables(stmt, idx);
            } catch (SQLException ignored) {
            }
        }
    }

    private Map<String, Map<String, String>> parseDataSources() {
        String yaml = loadResolvedYaml();
        Map<String, Object> root = new Yaml().load(yaml);
        Object dsObj = root.get("dataSources");
        Map<String, Map<String, String>> result = new LinkedHashMap<>();
        if (dsObj instanceof Map) {
            Map<?, ?> dsMap = (Map<?, ?>) dsObj;
            for (Map.Entry<?, ?> e : dsMap.entrySet()) {
                Object v = e.getValue();
                if (v instanceof Map) {
                    Map<?, ?> m = (Map<?, ?>) v;
                    String url = str(m.get("url"));
                    String username = str(m.get("username"));
                    String password = str(m.get("password"));
                    Map<String, String> one = new LinkedHashMap<>();
                    one.put("url", url);
                    one.put("username", username);
                    one.put("password", password);
                    result.put(String.valueOf(e.getKey()), one);
                }
            }
        }
        return result;
    }

    private String loadResolvedYaml() {
        try {
            ClassPathResource resource = new ClassPathResource("sharding.yaml");
            String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return environment.resolvePlaceholders(content);
        } catch (Exception e) {
            return "";
        }
    }

    private List<JdbcInfo> toJdbcInfos(Map<String, Map<String, String>> map) {
        List<JdbcInfo> list = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> e : map.entrySet()) {
            Map<String, String> m = e.getValue();
            String url = m.get("url");
            String username = m.get("username");
            String password = m.get("password");
            if (url != null && !url.isEmpty()) {
                list.add(new JdbcInfo(url, username, password));
            }
        }
        return list;
    }

    private void createUndoLog(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS `undo_log` (\n" +
                "  `branch_id` bigint NOT NULL,\n" +
                "  `xid` varchar(128) NOT NULL,\n" +
                "  `context` varchar(128) NOT NULL,\n" +
                "  `rollback_info` longblob NOT NULL,\n" +
                "  `log_status` int NOT NULL,\n" +
                "  `log_created` datetime NOT NULL,\n" +
                "  `log_modified` datetime NOT NULL,\n" +
                "  `id` bigint NOT NULL AUTO_INCREMENT,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        exec(stmt, sql);
    }

    private void createOrderTables(Statement stmt, int dbIndex) throws SQLException {
        int[] suffixes = new int[]{dbIndex, dbIndex + 8, dbIndex + 16, dbIndex + 24};
        for (int s : suffixes) {
            String sql = "CREATE TABLE IF NOT EXISTS `t_order_" + s + "` (\n" +
                    "  `id` bigint(20) NOT NULL,\n" +
                    "  `code` varchar(64) NOT NULL,\n" +
                    "  `code_hash` int(11) DEFAULT NULL,\n" +
                    "  `user_id` bigint(20) NOT NULL,\n" +
                    "  `user_name` varchar(64) DEFAULT NULL,\n" +
                    "  `order_time` datetime DEFAULT NULL,\n" +
                    "  `order_status` int(11) DEFAULT NULL,\n" +
                    "  `pay_status` int(11) DEFAULT NULL,\n" +
                    "  `total_amount` decimal(10,2) NOT NULL,\n" +
                    "  `payment_amount` decimal(10,2) NOT NULL,\n" +
                    "  `remark` varchar(255) DEFAULT NULL,\n" +
                    "  `create_user_id` bigint(20) DEFAULT NULL,\n" +
                    "  `create_user_name` varchar(64) DEFAULT NULL,\n" +
                    "  `create_time` datetime DEFAULT NULL,\n" +
                    "  `update_user_id` bigint(20) DEFAULT NULL,\n" +
                    "  `update_user_name` varchar(64) DEFAULT NULL,\n" +
                    "  `update_time` datetime DEFAULT NULL,\n" +
                    "  `is_del` tinyint(4) DEFAULT 0,\n" +
                    "  PRIMARY KEY (`id`),\n" +
                    "  KEY `idx_code` (`code`),\n" +
                    "  KEY `idx_user_id` (`user_id`),\n" +
                    "  KEY `idx_create_time` (`create_time`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            exec(stmt, sql);
        }
    }

    private void createOrderItemTables(Statement stmt, int dbIndex) throws SQLException {
        for (int s = dbIndex; s < 256; s += 8) {
            String sql = "CREATE TABLE IF NOT EXISTS `t_order_item_" + s + "` (\n" +
                    "  `id` bigint(20) NOT NULL,\n" +
                    "  `order_id` bigint(20) NOT NULL,\n" +
                    "  `order_code` varchar(64) NOT NULL,\n" +
                    "  `product_id` bigint(20) NOT NULL,\n" +
                    "  `product_name` varchar(128) NOT NULL,\n" +
                    "  `product_model` varchar(255) DEFAULT NULL,\n" +
                    "  `product_price` decimal(10,2) NOT NULL,\n" +
                    "  `quantity` int(11) NOT NULL,\n" +
                    "  `amount` decimal(10,2) NOT NULL,\n" +
                    "  `product_pic` varchar(255) DEFAULT NULL,\n" +
                    "  `create_user_id` bigint(20) DEFAULT NULL,\n" +
                    "  `create_user_name` varchar(64) DEFAULT NULL,\n" +
                    "  `create_time` datetime DEFAULT NULL,\n" +
                    "  `update_user_id` bigint(20) DEFAULT NULL,\n" +
                    "  `update_user_name` varchar(64) DEFAULT NULL,\n" +
                    "  `update_time` datetime DEFAULT NULL,\n" +
                    "  `is_del` tinyint(4) DEFAULT 0,\n" +
                    "  PRIMARY KEY (`id`),\n" +
                    "  KEY `idx_order_id` (`order_id`),\n" +
                    "  KEY `idx_order_code` (`order_code`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            exec(stmt, sql);
        }
    }

    private void createOrderAddressTables(Statement stmt, int dbIndex) throws SQLException {
        int[] suffixes = new int[]{dbIndex, dbIndex + 8, dbIndex + 16, dbIndex + 24};
        for (int s : suffixes) {
            String sql = "CREATE TABLE IF NOT EXISTS `t_order_delivery_address_" + s + "` (\n" +
                    "  `id` bigint(20) NOT NULL,\n" +
                    "  `order_id` bigint(20) NOT NULL,\n" +
                    "  `order_code` varchar(64) NOT NULL,\n" +
                    "  `user_id` bigint(20) NOT NULL,\n" +
                    "  `receiver_name` varchar(128) DEFAULT NULL,\n" +
                    "  `receiver_phone` varchar(32) DEFAULT NULL,\n" +
                    "  `province` varchar(64) DEFAULT NULL,\n" +
                    "  `city` varchar(64) DEFAULT NULL,\n" +
                    "  `area` varchar(64) DEFAULT NULL,\n" +
                    "  `address` varchar(255) DEFAULT NULL,\n" +
                    "  `zip_code` varchar(32) DEFAULT NULL,\n" +
                    "  `create_user_id` bigint(20) DEFAULT NULL,\n" +
                    "  `create_user_name` varchar(64) DEFAULT NULL,\n" +
                    "  `create_time` datetime DEFAULT NULL,\n" +
                    "  `update_user_id` bigint(20) DEFAULT NULL,\n" +
                    "  `update_user_name` varchar(64) DEFAULT NULL,\n" +
                    "  `update_time` datetime DEFAULT NULL,\n" +
                    "  `is_del` tinyint(4) DEFAULT 0,\n" +
                    "  PRIMARY KEY (`id`),\n" +
                    "  KEY `idx_order_id` (`order_id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            exec(stmt, sql);
        }
    }

    private String extractDbName(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        int i = url.indexOf('/');
        if (i < 0) {
            return "";
        }
        int j = url.indexOf('?', i + 1);
        String db = j > 0 ? url.substring(i + 1, j) : url.substring(i + 1);
        int k = db.lastIndexOf('/');
        String v = k >= 0 ? db.substring(k + 1) : db;
        return v == null ? "" : v;
    }

    private int extractDbIndex(String dbName) {
        int i = dbName.lastIndexOf('_');
        if (i < 0) {
            return 0;
        }
        try {
            return Integer.parseInt(dbName.substring(i + 1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void exec(Statement stmt, String sql) throws SQLException {
        stmt.execute(sql);
    }

    private String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static class JdbcInfo {
        final String url;
        final String username;
        final String password;

        JdbcInfo(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }
    }
}

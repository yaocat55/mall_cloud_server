package cn.net.mall.message.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class ShardingSphereConfiguration {

    private final Environment environment;

    public ShardingSphereConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mysql://" +
                environment.getProperty("DB_HOST", "localhost") + ":" +
                environment.getProperty("DB_PORT", "33081") +
                "/cloud_mall_message_0?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8");
        ds.setUsername(environment.getProperty("DB_USER", "root"));
        ds.setPassword(environment.getProperty("DB_PWD", "123456"));
        return ds;
    }
}

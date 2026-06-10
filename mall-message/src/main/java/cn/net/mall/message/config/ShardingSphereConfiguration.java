package cn.net.mall.message.config;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@Configuration
public class ShardingSphereConfiguration implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @Primary
    public DataSource dataSource() throws SQLException, IOException {
        ClassPathResource resource = new ClassPathResource("sharding.yaml");
        String yamlContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        String resolvedYaml = environment.resolvePlaceholders(yamlContent);
        return YamlShardingSphereDataSourceFactory.createDataSource(resolvedYaml.getBytes(StandardCharsets.UTF_8));
    }
}

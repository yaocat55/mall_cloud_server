package cn.net.mall.order.config;

import org.apache.ibatis.plugin.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPluginConfig {

    @Bean
    public Interceptor dbRouteLoggingInterceptor() {
        return new DbRouteLoggingInterceptor();
    }
}

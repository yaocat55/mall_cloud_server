package cn.net.mall.basic.config;

import cn.net.mall.basic.mybatis.DictCacheKeyGenerator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 应用运行时配置
 *
 * @date 2024/1/4 下午5:07
 */
@MapperScan(basePackages = "cn.net.mall.basic.mapper")
@Configuration
public class ApplicationConfig {

    @Bean
    public DictCacheKeyGenerator dictCacheKeyGenerator() {
        return new DictCacheKeyGenerator();
    }
}

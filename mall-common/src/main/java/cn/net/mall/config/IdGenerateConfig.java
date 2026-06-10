package cn.net.mall.config;

import cn.net.mall.util.SnowFlakeIdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 分布式ID配置
 *
 * @date 2024/5/21 下午6:37
 */
@Configuration
public class IdGenerateConfig {

    @Bean
    public SnowFlakeIdWorker snowFlakeIdWorker() {
        return new SnowFlakeIdWorker(0, 0);
    }
}

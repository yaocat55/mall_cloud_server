package cn.net.mall.basic.client.config;

import cn.net.mall.basic.client.fallback.DictFeignFallbackFactory;
import cn.net.mall.basic.client.fallback.SmsFeignFallbackFactory;
import cn.net.mall.basic.client.fallback.SmsRecordFeignFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FallbackFactory 自动配置.
 *
 * <p>统一注册 basic 模块的 Feign 降级工厂，避免各服务重复配置 {@code scanBasePackages}。</p>
 */
@Configuration
public class FallbackFactoryAutoConfiguration {

    @Bean
    public DictFeignFallbackFactory dictFeignFallbackFactory() {
        return new DictFeignFallbackFactory();
    }

    @Bean
    public SmsFeignFallbackFactory smsFeignFallbackFactory() {
        return new SmsFeignFallbackFactory();
    }

    @Bean
    public SmsRecordFeignFallbackFactory smsRecordFeignFallbackFactory() {
        return new SmsRecordFeignFallbackFactory();
    }
}

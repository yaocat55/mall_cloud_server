package cn.net.mall.product.config;

import cn.net.mall.product.config.properties.ProductDetailThreadPoolProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置类
 *
 * @date 2024/10/15 上午11:13
 */
@Configuration
public class ThreadPoolConfig {

    private final BusinessConfig businessConfig;

    public ThreadPoolConfig(BusinessConfig businessConfig) {
        this.businessConfig = businessConfig;
    }


    /**
     * 商品详情线程池
     *
     * @return 线程池
     */
    @Bean(name = "productDetailThreadPoolExecutor")
    public ThreadPoolExecutor productDetailThreadPoolExecutor() {
        ProductDetailThreadPoolProperties productDetailThreadPoolPoolConfig = businessConfig.getProductDetailThreadPoolPoolConfig();
        ThreadPoolExecutor threadPoolTaskExecutor = new ThreadPoolExecutor(productDetailThreadPoolPoolConfig.getCorePoolSize(),
                productDetailThreadPoolPoolConfig.getMaxPoolSize(),
                productDetailThreadPoolPoolConfig.getKeepAliveSeconds(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(productDetailThreadPoolPoolConfig.getQueueSize()),
                new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolTaskExecutor;
    }
}

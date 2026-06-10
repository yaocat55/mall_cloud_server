package cn.net.mall.product.config.properties;

import lombok.Data;

/**
 * 商品详情线程池配置
 *
 * @date 2024/4/30 下午4:22
 */
@Data
public class ProductDetailThreadPoolProperties {

    /**
     * 核心线程数
     */
    private int corePoolSize = 8;

    /**
     * 最大线程数
     */
    private int maxPoolSize = 10;

    /**
     * 队列大小
     */
    private int queueSize = 200;

    /**
     * 空闲线程回收时间，多少秒
     */
    private int keepAliveSeconds = 30;

    /**
     * 线程前缀
     */
    private String threadNamePrefix = "ProductDetailThread";


}

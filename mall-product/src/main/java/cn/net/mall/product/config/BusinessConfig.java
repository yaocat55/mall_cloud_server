package cn.net.mall.product.config;

import cn.net.mall.product.config.properties.ProductDetailThreadPoolProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 业务配置类
 *
 * @date 2024/4/30 下午4:20
 */
@Data
@Component
@Slf4j
@ConfigurationProperties(prefix = "mall.product")
public class BusinessConfig {

    /**
     * 商品搜索index名称
     */
    private String productEsIndexName = "product-es-index-v2";

    /**
     * 秒杀商品搜索index名称
     */
    private String seckillProductEsIndexName = "seckill-product-es-index-v2";

    /**
     * 商品详情线程池配置
     */
    private ProductDetailThreadPoolProperties productDetailThreadPoolPoolConfig = new ProductDetailThreadPoolProperties();

    private String recommendProductViewTopic = "RECOMMEND_PRODUCT_VIEW_TOPIC";

}

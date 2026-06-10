package cn.net.mall.marketing.config;

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
@ConfigurationProperties(prefix = "mall.marketing")
public class BusinessConfig {

    /**
     * 秒杀商品搜索index名称
     */
    private String seckillProductEsIndexName = "seckill-product-es-index-v1";
}

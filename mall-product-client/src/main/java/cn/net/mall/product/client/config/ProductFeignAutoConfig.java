package cn.net.mall.product.client.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Product Feign 客户端自动配置.
 */
@AutoConfiguration
@EnableFeignClients(basePackages = "cn.net.mall.product.client")
public class ProductFeignAutoConfig {
}

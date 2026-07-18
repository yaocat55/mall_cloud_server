package cn.net.mall.order.client.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Order Feign 客户端自动配置.
 */
@AutoConfiguration
@EnableFeignClients(basePackages = "cn.net.mall.order.client")
public class OrderFeignAutoConfig {
}

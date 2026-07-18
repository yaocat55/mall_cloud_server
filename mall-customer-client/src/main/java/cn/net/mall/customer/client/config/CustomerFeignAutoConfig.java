package cn.net.mall.customer.client.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Customer Feign 客户端自动配置.
 */
@AutoConfiguration
@EnableFeignClients(basePackages = "cn.net.mall.customer.client")
public class CustomerFeignAutoConfig {
}

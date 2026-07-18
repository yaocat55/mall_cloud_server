package cn.net.mall.pay.client.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Pay Feign 客户端自动配置.
 */
@AutoConfiguration
@EnableFeignClients(basePackages = "cn.net.mall.pay.client")
public class PayFeignAutoConfig {
}

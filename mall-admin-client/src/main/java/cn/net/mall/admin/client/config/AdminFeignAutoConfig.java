package cn.net.mall.admin.client.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Admin Feign 客户端自动配置.
 */
@AutoConfiguration
@EnableFeignClients(basePackages = "cn.net.mall.admin.client")
public class AdminFeignAutoConfig {
}

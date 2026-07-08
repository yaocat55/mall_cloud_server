package cn.net.mall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"cn.net.mall.order"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"cn.net.mall.order", "cn.net.mall.product.client", "cn.net.mall.marketing.client", "cn.net.mall.admin.client"})
@MapperScan("cn.net.mall.order.mapper")
@EnableAsync
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}

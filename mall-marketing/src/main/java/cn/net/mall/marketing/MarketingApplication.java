package cn.net.mall.marketing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @date 2024/1/3 下午3:44
 */
@EnableFeignClients(basePackages = {"cn.net.mall.basic", "cn.net.mall.product.client", "cn.net.mall.customer.client"})
@EnableDiscoveryClient
@MapperScan("cn.net.mall.marketing.mapper")
@EnableCaching
@SpringBootApplication(scanBasePackages = {"cn.net.mall.marketing"})
public class MarketingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketingApplication.class, args);
    }
}

package cn.net.mall.product;

import cn.net.mall.annotation.EnableRequestLogFilter;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * @date 2024/1/3 下午3:44
 */
@EnableFeignClients(basePackages = {"cn.net.mall.product.client"})
@EnableRequestLogFilter
@EnableDiscoveryClient
@MapperScan("cn.net.mall.product.mapper")
@EnableCaching
@Import(RocketMQAutoConfiguration.class)
@SpringBootApplication(scanBasePackages = {"cn.net.mall.product"})
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}

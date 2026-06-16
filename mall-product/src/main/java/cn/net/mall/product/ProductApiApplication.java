package cn.net.mall.product;

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
@EnableFeignClients(basePackages = {"cn.net.mall.basic","cn.net.mall.auth"})
@EnableDiscoveryClient
@MapperScan("cn.net.mall.product.mapper")
@EnableCaching
@Import(RocketMQAutoConfiguration.class)
@SpringBootApplication(scanBasePackages = {"cn.net.mall.product"})
public class ProductApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApiApplication.class, args);
    }
}

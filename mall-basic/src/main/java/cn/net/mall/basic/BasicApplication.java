package cn.net.mall.basic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @date 2024/1/3 下午3:44
 */
@EnableFeignClients(basePackages = {"cn.net.mall.admin.client"})
@MapperScan("cn.net.mall.basic.mapper")
@EnableCaching
@SpringBootApplication(scanBasePackages = {"cn.net.mall.basic"})
public class BasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicApplication.class, args);
    }
}

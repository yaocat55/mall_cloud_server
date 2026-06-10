package cn.net.mall.auth;

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
@EnableFeignClients(basePackages = "cn.net.mall.basic")
@EnableDiscoveryClient
@MapperScan("cn.net.mall.auth.mapper")
@EnableCaching
@SpringBootApplication(scanBasePackages = {"cn.net.mall"})
public class AuthApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApiApplication.class, args);
    }
}

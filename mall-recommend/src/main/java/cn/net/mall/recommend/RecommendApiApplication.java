package cn.net.mall.recommend;

import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "cn.net.mall")
@MapperScan("cn.net.mall.recommend.mapper")
@EnableFeignClients(basePackages = {
        "cn.net.mall.product.client",
        "cn.net.mall.recommend.support"
})
@EnableDiscoveryClient
@Import(RocketMQAutoConfiguration.class)
public class RecommendApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecommendApiApplication.class, args);
    }
}

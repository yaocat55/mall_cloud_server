package cn.net.mall.mobile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"cn.net.mall.admin.client", "cn.net.mall.basic.client",
        "cn.net.mall.product.client", "cn.net.mall.marketing.client",
        "cn.net.mall.order.client", "cn.net.mall.pay.client",
        "cn.net.mall.customer.client"})
public class MobileBffApplication {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(MobileBffApplication.class, args);
    }
}

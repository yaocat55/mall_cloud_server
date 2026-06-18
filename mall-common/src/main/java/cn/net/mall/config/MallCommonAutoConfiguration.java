package cn.net.mall.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ComponentScan(basePackages = {
    "cn.net.mall.config",
    "cn.net.mall.handler",
    "cn.net.mall.interceptor",
    "cn.net.mall.helper",
    "cn.net.mall.util",
    "cn.net.mall.sensitive",
    "cn.net.mall.workid"
})
public class MallCommonAutoConfiguration {
}

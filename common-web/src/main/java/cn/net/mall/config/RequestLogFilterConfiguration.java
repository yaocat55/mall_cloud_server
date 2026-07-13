package cn.net.mall.config;

import cn.net.mall.filter.RequestLogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 请求日志过滤器配置，由 {@link EnableRequestLogFilter} 导入。
 */
@Configuration
public class RequestLogFilterConfiguration {

    @Bean
    public FilterRegistrationBean<RequestLogFilter> requestLogFilter() {
        FilterRegistrationBean<RequestLogFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestLogFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("requestLogFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}

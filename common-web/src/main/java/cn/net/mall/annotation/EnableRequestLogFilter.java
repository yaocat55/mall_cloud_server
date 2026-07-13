package cn.net.mall.annotation;

import cn.net.mall.config.RequestLogFilterConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用请求日志过滤器。
 *
 * <p>在 {@code @Configuration} 类上添加此注解，会自动注册 {@link cn.net.mall.filter.RequestLogFilter}，
 * 拦截所有 {@code /*} 请求并打印请求/响应日志（含 TraceId、耗时等）。</p>
 *
 * <pre>{@code
 * @EnableRequestLogFilter
 * @SpringBootApplication
 * public class AdminApplication { ... }
 * }</pre>
 *
 * 替代在各个服务中重复编写 {@code RequestLogFilterConfig} + {@code FilterRegistrationBean}。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RequestLogFilterConfiguration.class)
public @interface EnableRequestLogFilter {
}

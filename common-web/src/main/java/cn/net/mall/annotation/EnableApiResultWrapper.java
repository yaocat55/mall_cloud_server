package cn.net.mall.annotation;

import cn.net.mall.config.ApiResultWrapperConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用统一 API 响应包装。
 *
 * <p>在 {@code @Configuration} 类上添加此注解，会对所有 {@code /v1/**} 的控制器返回值
 * 自动包装为统一的 {@code ApiResult} 格式。</p>
 *
 * <p>适用场景：直接对外暴露的服务（如 BFF、Gateway）。<br>
 * 不适用场景：仅对内提供 Feign 调用的服务（如 admin-api、basic-api）。</p>
 *
 * <pre>{@code
 * @EnableApiResultWrapper
 * @SpringBootApplication
 * public class AdminBffApplication { ... }
 * }</pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ApiResultWrapperConfiguration.class)
public @interface EnableApiResultWrapper {
}

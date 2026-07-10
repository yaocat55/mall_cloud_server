package cn.net.mall.config;

import cn.net.mall.handler.GlobalApiResultHandler;
import cn.net.mall.handler.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 * Web 通用自动配置。
 *
 * <p>条件：SERVLET Web 应用</p>
 *
 * <p>注册的 Bean：</p>
 * <ul>
 *   <li>{@link GlobalApiResultHandler} — 统一 API 响应包装（@RestControllerAdvice + ResponseBodyAdvice）</li>
 *   <li>{@link GlobalExceptionHandler} — 全局异常捕获（@RestControllerAdvice）</li>
 * </ul>
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebAutoConfiguration {

    /**
     * GlobalApiResultHandler —— 统一 API 响应包装器。
     * <p>对所有匹配 {@code /v1/**} 的控制器返回值进行拦截，自动包装为 {@code ApiResult} 格式，
     * 确保前端收到统一的数据结构。</p>
     *
     * @return GlobalApiResultHandler 实例
     */
    @Bean
    public GlobalApiResultHandler globalApiResultHandler() {
        return new GlobalApiResultHandler();
    }

    /**
     * GlobalExceptionHandler —— 全局异常处理器。
     * <p>捕获控制器层抛出的所有 {@link Throwable}，按异常类型分类处理：</p>
     * <ul>
     *   <li>{@code BusinessException} — 返回业务错误码和消息</li>
     *   <li>{@code MethodArgumentNotValidException} — 返回参数校验错误</li>
     *   <li>其它异常 — 返回 500 服务器内部错误</li>
     * </ul>
     * <p>内部调用（INNER-REQUEST 头）直接返回 {@code ResponseEntity} 而非 {@code ApiResult}。</p>
     *
     * @return GlobalExceptionHandler 实例
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}

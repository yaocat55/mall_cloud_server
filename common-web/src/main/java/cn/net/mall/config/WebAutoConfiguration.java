package cn.net.mall.config;

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
 *   <li>{@link GlobalExceptionHandler} — 全局异常捕获（@RestControllerAdvice）</li>
 * </ul>
 *
 * <p>注意：{@link cn.net.mall.handler.GlobalApiResultHandler} 已通过 {@link EnableApiResultWrapper} 控制，
 * 不再自动注入。</p>
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebAutoConfiguration {

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

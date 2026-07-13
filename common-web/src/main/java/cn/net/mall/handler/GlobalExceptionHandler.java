package cn.net.mall.handler;

import cn.net.mall.exception.BusinessException;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * 全局异常处理器。
 *
 * <p>拦截所有控制器抛出的异常，按类型分类处理：</p>
 * <ul>
 *   <li>{@link BusinessException} — 返回自定义业务错误码与消息</li>
 *   <li>{@link MethodArgumentNotValidException} — 返回 400 + 参数校验失败信息</li>
 *   <li>其它异常 — 返回 500 + 通用提示</li>
 * </ul>
 *
 * <p>特殊行为：当请求头包含 {@code INNER-REQUEST} 时（微服务内部 Feign 调用），
 * 直接返回 {@link ResponseEntity} 而非 {@code ApiResult} 格式，便于调用方获取原始异常信息。</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ====================================
    // 统一入口
    // ====================================

    @ExceptionHandler(Throwable.class)
    public Object handleException(Throwable e) {
        String reqInfo = getRequestInfo();

        // 微服务内部调用（Feign），返回 ResponseEntity 而非 ApiResult
        if (isInnerRequest()) {
            return handleInnerRequest(e, reqInfo);
        }

        // 按类型分类处理
        if (e instanceof BusinessException be) {
            return handleBusinessException(be, reqInfo);
        }
        if (e instanceof MethodArgumentNotValidException me) {
            return handleValidationException(me, reqInfo);
        }
        return handleUnknownException(e, reqInfo);
    }

    // ====================================
    // 内部 Feign 调用（INNER-REQUEST 头）
    // ====================================

    private Object handleInnerRequest(Throwable e, String reqInfo) {
        if (e instanceof BusinessException be) {
            log.error("内部调用业务异常：{} code={} msg={}", reqInfo, be.getCode(), be.getMessage(), e);
            return ResponseEntity.status(be.getCode()).body(be.getMessage());
        }
        log.error("内部调用异常：{}", reqInfo, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    // ====================================
    // 业务异常 — 返回自定义错误码和消息
    // ====================================

    private ApiResult<String> handleBusinessException(BusinessException e, String reqInfo) {
        log.error("业务异常：{} code={} msg={}", reqInfo, e.getCode(), e.getMessage(), e);
        return ApiResultUtil.error(e.getCode(), e.getMessage());
    }

    // ====================================
    // 参数校验异常 — 返回 400 + 第一条校验失败信息
    // ====================================

    private ApiResult<String> handleValidationException(MethodArgumentNotValidException e, String reqInfo) {
        log.warn("参数校验异常：{}", reqInfo, e);
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getFieldError().getDefaultMessage();
            return ApiResultUtil.error(HttpStatus.BAD_REQUEST.value(), msg);
        }
        return ApiResultUtil.error(HttpStatus.BAD_REQUEST.value(), "请求参数校验失败");
    }

    // ====================================
    // 未知异常 — 返回 500 通用提示
    // ====================================

    private ApiResult<String> handleUnknownException(Throwable e, String reqInfo) {
        log.error("其他异常：{} msg={}", reqInfo, e.getMessage(), e);
        return ApiResultUtil.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误，请联系系统管理员！");
    }

    // ====================================
    // 工具方法
    // ====================================

    /**
     * 判断当前请求是否来自微服务内部 Feign 调用（含 INNER-REQUEST 头）。
     */
    private boolean isInnerRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes sra)) {
            return false;
        }
        HttpServletRequest request = sra.getRequest();
        String header = request.getHeader("INNER-REQUEST");
        return header != null && !header.isEmpty();
    }

    /**
     * 获取当前请求的方法、URI、客户端 IP，用于日志记录。
     */
    private static String getRequestInfo() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes sra)) {
            return "";
        }
        HttpServletRequest req = sra.getRequest();
        String method = req.getMethod();
        String uri = req.getRequestURI();
        String query = req.getQueryString();
        String fullUri = query == null ? uri : uri + "?" + query;
        return "method=" + method + " uri=" + fullUri + " ip=" + req.getRemoteAddr();
    }
}

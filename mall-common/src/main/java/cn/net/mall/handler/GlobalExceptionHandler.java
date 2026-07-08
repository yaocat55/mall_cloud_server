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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 全局异常处理器。
 *
 * <p>通过 {@code @RestControllerAdvice} 拦截所有控制器抛出的异常，按以下规则分类处理：</p>
 * <ul>
 *   <li><strong>BusinessException（业务异常）</strong> — 返回自定义业务错误码与消息</li>
 *   <li><strong>MethodArgumentNotValidException（参数校验异常）</strong> — 提取 BindingResult
 *       中的第一条错误消息，返回 400 状态码</li>
 *   <li><strong>其它异常</strong> — 返回 500 服务器内部错误，日志记录完整堆栈</li>
 * </ul>
 *
 * <p>特殊行为：</p>
 * <ul>
 *   <li>当请求头中包含 {@code INNER-REQUEST} 时（微服务间内部调用），直接返回
 *       {@link org.springframework.http.ResponseEntity} 而非 {@code ApiResult} 格式，
 *       便于内部调用方精确获取原始异常信息</li>
 * </ul>
 *
 * @date 2024/1/9 下午1:16
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 统一处理异常
     *
     * @param e 异常
     * @return API请求响应实体
     */
    @ExceptionHandler(Throwable.class)
    public Object handleException(Throwable e) {
        String reqInfo = getRequestInfo();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(requestAttributes)) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            if (request != null && request.getHeader("INNER-REQUEST") != null && !request.getHeader("INNER-REQUEST").isEmpty()) {
                if (e instanceof BusinessException) {
                    BusinessException be = (BusinessException) e;
                    log.error("内部调用业务异常：{} code={} msg={}", reqInfo, be.getCode(), be.getMessage(), e);
                    return ResponseEntity.status(be.getCode()).body(be.getMessage());
                }
                log.error("内部调用异常：{}", reqInfo, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }

        if (e instanceof BusinessException) {
            BusinessException businessException = (BusinessException) e;
            log.error("业务异常：{} code={} msg={}", reqInfo, businessException.getCode(), businessException.getMessage(), e);
            return ApiResultUtil.error(businessException.getCode(), businessException.getMessage());
        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException message = (MethodArgumentNotValidException) e;
            BindingResult bindingResult = message.getBindingResult();
            if (bindingResult.hasErrors()) {
                return ApiResultUtil.error(HttpStatus.BAD_REQUEST.value(), bindingResult.getFieldError().getDefaultMessage());
            }
        }
        log.error("其他异常：{} msg={}", reqInfo, e.getMessage(), e);
        return ApiResultUtil.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误，请联系系统管理员！");
    }

    /**
     * 获取字符串中最后一个中括号内的内容
     *
     * @param str 需要解析的字符串
     * @return 最后一个中括号中的内容，如果没有找到则返回空字符串
     */
    public static String getErrorMessage(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        // 正则表达式匹配最后一个中括号内的内容
        Pattern pattern = Pattern.compile(".*\\[(.*?)\\].*?");
        Matcher matcher = pattern.matcher(str);

        // 如果找到匹配，返回捕获组中的内容（即中括号内的内容）
        if (matcher.matches()) {
            return matcher.group(1);
        }

        // 处理嵌套中括号的情况
        pattern = Pattern.compile("\\[(.*?)\\](?!.*\\[)");
        matcher = pattern.matcher(str);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "系统错误";
    }

    /**
     * 获取字符串中第一个中括号[]内的内容
     *
     * @param str 需要提取内容的字符串
     * @return 返回中括号内的内容，如果没有找到则返回空字符串
     */
    public static String getErrorCode(String str) {
        if (str == null || str.isEmpty()) {
            return "系统出现异常，请稍后重试";
        }

        // 正则表达式：匹配第一个中括号[]内的内容
        // \[ 表示左中括号，\] 表示右中括号
        // (.*?) 表示非贪婪模式匹配任意字符
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(str);

        // 如果找到匹配项，返回第一个组（即括号内的内容）
        if (matcher.find()) {
            return matcher.group(1);
        }

        // 如果没有找到匹配项，返回空字符串
        return "";
    }

    private static String getRequestInfo() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes) {
            HttpServletRequest req = ((ServletRequestAttributes) attrs).getRequest();
            String method = req.getMethod();
            String uri = req.getRequestURI();
            String query = req.getQueryString();
            String ip = req.getRemoteAddr();
            String fullUri = query == null ? uri : uri + "?" + query;
            return "method=" + method + " uri=" + fullUri + " ip=" + ip;
        }
        return "";
    }
}

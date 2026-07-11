package cn.net.mall.decoder;

import cn.hutool.json.JSONUtil;
import cn.net.mall.exception.BusinessException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 全局 Feign ErrorDecoder.
 *
 * <p>将 Feign 调用的 HTTP 异常翻译为业务异常，并决定是否触发重试：</p>
 * <ul>
 *   <li>4xx（客户端错误）→ {@link BusinessException}，不重试</li>
 *   <li>5xx（服务端错误）→ {@link RetryableException}，触发 resilience4j 重试</li>
 *   <li>连接超时等网络异常 → Feign 原生处理，自动触发重试</li>
 * </ul>
 *
 * <p>注意：此解码器与 {@link cn.net.mall.config.FeignDecoderConfig} 配合使用，
 * 通过 common-web 全局生效。</p>
 *
 * @author system
 * @since 1.0.0
 */
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        // 读取响应体
        String bodyStr = readResponseBody(response);
        int status = response.status();

        log.warn("Feign 调用异常 [{}] {} - status={}, body={}",
                response.request().httpMethod(), methodKey, status, bodyStr);

        // 尝试从响应体中提取错误信息
        String errorMsg = extractErrorMessage(bodyStr, status);

        if (status >= 400 && status < 500) {
            // 4xx：客户端请求错误，不重试
            return handleClientError(methodKey, status, errorMsg);
        } else if (status >= 500) {
            // 5xx：服务端错误，触发重试
            return handleServerError(methodKey, response, status, errorMsg);
        }

        // 其他状态码走默认
        return defaultDecoder.decode(methodKey, response);
    }

    /**
     * 处理 4xx 客户端错误.
     *
     * <p>直接抛 {@link BusinessException}，resilience4j 默认不重试此异常。</p>
     */
    private Exception handleClientError(String methodKey, int status, String errorMsg) {
        log.error("Feign 客户端错误 [{}] status={}, {}", methodKey, status, errorMsg);
        return new BusinessException("请求服务失败：" + errorMsg);
    }

    /**
     * 处理 5xx 服务端错误.
     *
     * <p>抛 {@link RetryableException}，resilience4j 会按重试策略自动重试。</p>
     */
    private Exception handleServerError(String methodKey, Response response, int status, String errorMsg) {
        log.error("Feign 服务端错误 [{}] status={}, {}", methodKey, status, errorMsg);
        return new RetryableException(
                status,
                "服务暂不可用：" + errorMsg,
                feign.Request.HttpMethod.GET,
                System.currentTimeMillis() + 1000L,
                response.request()
        );
    }

    /**
     * 读取 Feign Response 的 body 内容.
     */
    private String readResponseBody(Response response) {
        if (response.body() == null) {
            return "";
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = response.body().asInputStream().read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            return baos.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("读取 Feign 响应体失败", e);
            return "";
        }
    }

    /**
     * 从 JSON 响应体中提取 message 字段，若失败则返回 HTTP 状态码对应的默认消息.
     */
    @SuppressWarnings("unchecked")
    private String extractErrorMessage(String body, int status) {
        if (body == null || body.isBlank()) {
            return getDefaultMessage(status);
        }
        try {
            Map<String, Object> map = JSONUtil.toBean(body, Map.class);
            if (map != null && map.get("message") != null) {
                return map.get("message").toString();
            }
            if (map != null && map.get("msg") != null) {
                return map.get("msg").toString();
            }
        } catch (Exception ignored) {
            // 非 JSON 体，直接返回原始内容截断
        }
        return body.length() > 100 ? body.substring(0, 100) + "..." : body;
    }

    private static String getDefaultMessage(int status) {
        return switch (status) {
            case 400 -> "请求参数错误";
            case 401 -> "未授权，请重新登录";
            case 403 -> "无权限访问";
            case 404 -> "请求的资源不存在";
            case 408 -> "请求超时";
            case 429 -> "请求过于频繁";
            case 500 -> "服务内部错误";
            case 502 -> "网关错误";
            case 503 -> "服务暂时不可用";
            case 504 -> "网关超时";
            default -> "未知错误 (" + status + ")";
        };
    }
}

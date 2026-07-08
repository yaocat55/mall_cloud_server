package cn.net.mall.handler;

import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


/**
 * 统一 API 响应包装器。
 *
 * <p>通过 {@code @RestControllerAdvice} + {@link ResponseBodyAdvice} 拦截控制器的返回值，
 * 将非 {@code ApiResult} 类型的返回值自动包装为统一的 {@code ApiResult} 格式。</p>
 *
 * <p>拦截范围：匹配 {@code /v1/**} 路径的请求。</p>
 *
 * <p>处理规则：</p>
 * <ul>
 *   <li>如果返回值已经是 {@code ApiResult} 类型，直接返回不做二次包装</li>
 *   <li>其它返回值自动包装为 {@code ApiResult.success(body)}</li>
 * </ul>
 *
 * @date 2024/1/9 下午2:09
 */
// @RestControllerAdvice (registered by WebAutoConfiguration)
public class GlobalApiResultHandler implements ResponseBodyAdvice<Object> {
    public static final String URL_PREFIX = "/v1";

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        String requestURI = request.getRequestURI();
        return matchUrl(requestURI);
    }

    private boolean matchUrl(String uri) {
        if (uri == null || uri.isEmpty()) {
            return false;
        }
        return uri.contains(URL_PREFIX);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResult) {
            return (ApiResult) body;
        }
        return ApiResultUtil.success(body);
    }
}

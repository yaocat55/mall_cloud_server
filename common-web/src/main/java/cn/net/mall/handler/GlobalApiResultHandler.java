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
 * <p>路径匹配规则：</p>
 * <ul>
 *   <li>{@code /v1/**} — 内部微服务公开接口</li>
 *   <li>{@code /admin/v1/**} — admin-bff 聚合接口</li>
 *   <li>{@code /mobile/v1/**} — mobile-bff 聚合接口</li>
 *   <li>{@code /v1/internal/**} — ⛔ 不包装（Feign 内部调用需要裸 DTO）</li>
 *   <li>非以上前缀（如 ForwardController 的代理路径）— ⛔ 不包装</li>
 * </ul>
 *
 * <p>处理规则：</p>
 * <ul>
 *   <li>如果返回值已经是 {@code ApiResult} 类型，直接返回不做二次包装</li>
 *   <li>{@code null} 返回值 → 包装为 {@code ApiResult.success(null)}</li>
 *   <li>其它返回值自动包装为 {@code ApiResultUtil.success(body)}</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalApiResultHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra == null) return false;
        HttpServletRequest request = sra.getRequest();
        String requestURI = request.getRequestURI();
        return matchUrl(requestURI);
    }

    /**
     * 接口路径匹配规则.
     *
     * <p>BFF 路径（/admin/v1  /mobile/v1）和微服务公开路径（/v1）自动包装。
     * 内部 Feign 路径（/v1/internal）跳过，Feign 需要裸 DTO。</p>
     */
    private boolean matchUrl(String uri) {
        if (uri == null || uri.isEmpty()) return false;
        // 内部 Feign 接口不包装
        if (uri.contains("/v1/internal/")) return false;
        return uri.startsWith("/v1/")
                || uri.startsWith("/admin/v1/")
                || uri.startsWith("/mobile/v1/");
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResult) {
            return body;
        }
        return ApiResultUtil.success(body);
    }
}

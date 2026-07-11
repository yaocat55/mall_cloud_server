package cn.net.mall.auth.interceptor;

import cn.net.mall.util.TokenUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

import static cn.net.mall.util.TokenUtil.AUTHORIZATION;

/**
 * Feign 调用权限拦截器。
 *
 * 作用：
 *   在微服务间通过 Feign 发起内部调用时，自动将当前请求上下文中的
 *   Authorization 令牌透传到下游服务，同时添加内部调用标识头。
 *
 * 使用场景：
 *   服务 A 收到外部请求（带 JWT），通过 Feign 调用服务 B 时，
 *   本拦截器自动将 JWT 从请求头中取出，放到 Feign 请求头中透传下去，
 *   下游服务的 AuthApiInterceptor 可以识别并解析该 JWT。
 *
 * 为什么需要这个拦截器？
 *   如果不透传令牌，下游服务无法识别当前操作用户的身份，
 *   导致无法做权限校验或无法获取当前用户信息。
 *
 * 配合使用的组件：
 *   @see cn.net.mall.auth.interceptor.AuthApiInterceptor 下游服务接收并解析 JWT
 *   @see cn.net.mall.gateway.filter.AuthFilter Gateway 层验证 JWT 并透传身份头
 *
 * @author system
 * @date 2025/1/13 16:39
 */
public class FeignAuthInterceptor implements RequestInterceptor {

    /**
     * 内部请求标识头。
     *
     * 下游服务可通过检查此头判断请求是否来自内部 Feign 调用，
     * 用于区分外部请求和内部调用，实现差异化的鉴权策略。
     */
    public static final String INNER_REQUEST = "INNER-REQUEST";

    /**
     * Feign 请求拦截逻辑。
     *
     * 在 Feign 发起 HTTP 请求前执行，将当前线程绑定的请求上下文中的
     * Authorization 令牌取出，放入 Feign 请求头中。
     *
     * 执行流程：
     *   1. 获取当前请求上下文（RequestContextHolder）
     *   2. 若上下文为空，直接返回（可能处于非 Web 线程环境）
     *   3. 从 HttpServletRequest 中提取 Authorization 头
     *   4. 若令牌存在，添加到 Feign 请求头中
     *   5. 无论令牌是否存在，都添加 INNER_REQUEST=true 标识头
     *
     * 注意：
     *   Feign 默认不自动传递 ServletRequest 中的请求头，
     *   必须通过 RequestInterceptor 手动透传。
     *
     * @param template Feign 请求模板，可往其中添加请求头
     */
    @Override
    public void apply(RequestTemplate template) {
        // 获取当前请求上下文，可能为 null（如异步任务、定时任务中调用 Feign）
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            return;
        }

        // 转换为 Servlet 请求上下文，获取 HttpServletRequest
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();

        // 从请求头中提取 Authorization 令牌
        String authorization = TokenUtil.getAuthorization(request);

        // 令牌存在则透传到 Feign 请求头中，保证下游服务能识别用户身份
        if (StringUtils.hasLength(authorization)) {
            template.header(AUTHORIZATION, authorization);
        }

        // 标记为内部调用，下游服务可用此头判断请求来源
        template.header(INNER_REQUEST, "true");
    }
}
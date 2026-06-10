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
 * Feign调用权限拦截器
 *
 * @date 2025/1/13 16:39
 */
public class FeignAuthInterceptor implements RequestInterceptor {

    public static String INNER_REQUEST = "INNER-REQUEST";


    @Override
    public void apply(RequestTemplate template) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            return;
        }

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String authorization = TokenUtil.getAuthorization(request);
        if (StringUtils.hasLength(authorization)) {
            template.header(AUTHORIZATION, authorization);
        }
        template.header(INNER_REQUEST, "true");
    }
}
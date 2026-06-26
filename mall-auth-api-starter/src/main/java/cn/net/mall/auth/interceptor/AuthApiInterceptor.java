package cn.net.mall.auth.interceptor;

import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.redis.TokenHelper;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.util.SpringUtil;
import cn.net.mall.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

/**
 * 权限拦截器
 *
 * @date 2025/1/13 16:25
 */
public class AuthApiInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = TokenUtil.getTokenForAuthorization(request);
        if (StringUtils.hasLength(token)) {
            TokenHelper tokenHelper = SpringUtil.getBean("tokenHelper", TokenHelper.class);
            String username = tokenHelper.getUsernameFromToken(token);
            if (StringUtils.hasLength(username)) {
                JwtUserEntity userEntity = (JwtUserEntity) tokenHelper.getUserDetailsFromUsername(username);
                if (Objects.nonNull(userEntity)) {
                    FillUserUtil.setCurrentUser(userEntity.getId(), userEntity.getUsername());
                }
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        FillUserUtil.clearCurrentUser();
    }
}
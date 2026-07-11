package cn.net.mall.auth.interceptor;

import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.util.TokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 认证 API 拦截器。
 *
 * 处理三种场景：
 *   1. 有 X-User-* 请求头 → 必须同时有有效 JWT，且 claims 与请求头一致
 *      防止内网直调时伪造身份头绕过鉴权。
 *   2. 只有 JWT → 独立验签，从 claims 设置 SecurityContext。
 *   3. 无身份信息 → 放行，由 Spring Security 决定是否拒绝。
 *
 * 信任链条：
 *   Gateway（AuthFilter）验证 JWT 后会透传 X-User-Id / X-User-Name / X-Roles
 *   到下游。内部 Feign 调用通过 FeignAuthInterceptor 传递 Authorization 头。
 *   本拦截器始终以 JWT claims 为最终信任源，请求头仅作辅助比对，不直接采纳。
 *
 * 设计上不依赖 Redis：
 *   不依赖 TokenHelper / RedisUtil bean，避免无 Redis 的服务（如 BFF）
 *   启动时因 Bean 缺失而失败。tokenSecret 通过构造方法注入。
 *
 * @author system
 * @since 1.0.0
 * @see cn.net.mall.gateway.filter.AuthFilter
 */
public class AuthApiInterceptor implements HandlerInterceptor {

    /** JWT 签名密钥，从配置 mall.mgt.tokenSecret 注入 */
    private final String tokenSecret;

    public AuthApiInterceptor(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    /**
     * 请求前置处理。
     *
     * 从请求头提取 X-User-Id 和 Authorization 令牌，
     * 验证 JWT 合法性，校验请求头与 JWT 身份一致性，
     * 通过后注入 SecurityContext。
     *
     * @param request  当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param handler  被调用的处理器
     * @return true 继续执行，false 中断请求
     * @throws Exception 处理异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdStr = request.getHeader("X-User-Id");
        String token = TokenUtil.getTokenForAuthorization(request);

        Claims claims = null;
        if (StringUtils.hasLength(token) && StringUtils.hasLength(tokenSecret)) {
            try {
                claims = TokenUtil.parseClaimsFromToken(token, tokenSecret);
            } catch (Exception ignored) {
                // JWT 解析失败静默处理，不抛异常
            }
        }

        if (StringUtils.hasLength(userIdStr)) {
            if (claims == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "缺少有效的 JWT");
                return false;
            }

            Long headerUserId = Long.parseLong(userIdStr);
            Long claimsUserId = claims.get("user_id", Long.class);
            if (!Objects.equals(headerUserId, claimsUserId)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 身份与请求头不匹配");
                return false;
            }

            setSecurityContext(claims);
            return true;
        }

        if (claims != null) {
            setSecurityContext(claims);
            return true;
        }

        return true;
    }

    /**
     * 从 JWT claims 解析用户信息并注入 SecurityContext。
     *
     * 解析字段：user_id, user_name, roles
     * 同时设置 FillUserUtil 的 ThreadLocal 和 SecurityContextHolder。
     *
     * @param claims JWT 声明对象
     */
    private void setSecurityContext(Claims claims) {
        Long userId = claims.get("user_id", Long.class);
        String username = claims.get("user_name", String.class);
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);

        if (userId == null) {
            return;
        }

        FillUserUtil.setCurrentUser(userId, username);

        if (roles == null || roles.isEmpty()) {
            return;
        }

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        JwtUserEntity jwtUserEntity = new JwtUserEntity();
        jwtUserEntity.setId(userId);
        jwtUserEntity.setUsername(username);
        jwtUserEntity.setRoles(roles);
        jwtUserEntity.setAuthorities(authorities);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(jwtUserEntity, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 请求完成后清理 ThreadLocal，避免用户身份污染线程复用。
     *
     * @param request  当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param handler  被调用的处理器
     * @param ex       处理过程中抛出的异常（如有）
     * @throws Exception 清理异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        FillUserUtil.clearCurrentUser();
    }
}
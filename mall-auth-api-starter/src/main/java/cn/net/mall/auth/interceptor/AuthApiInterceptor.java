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
 * 权限拦截器.
 *
 * 核心逻辑：
 * 1. 有 X-User-* 请求头 → 必须同时有有效 JWT，且 claims 与请求头一致
 *    防止内网直调时伪造身份头绕过鉴权。
 * 2. 只有 JWT → 独立验签，从 claims 设置 SecurityContext。
 * 3. 无身份信息 → 放行，由 Spring Security 决定是否拒绝。
 *
 * 信任关系：
 * Gateway（AuthFilter）验证 JWT 后会透传 X-User-Id / X-User-Name / X-Roles
 * 到下游。内部 Feign 调用通过 FeignAuthInterceptor 传递 Authorization 头。
 * 本拦截器始终以 JWT claims 为最终信任源，请求头仅作辅助比对，不直接采纳。
 *
 * 不依赖 TokenHelper / RedisUtil bean，避免无 Redis 的服务（如 BFF）启动失败。
 * tokenSecret 通过构造方法注入，无 Redis 的服务只需在配置中提供 mall.mgt.tokenSecret。
 *
 * @author system
 * @since 1.0.0
 * @see cn.net.mall.gateway.filter.AuthFilter
 */
public class AuthApiInterceptor implements HandlerInterceptor {

    private final String tokenSecret;

    public AuthApiInterceptor(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 提取身份来源
        String userIdStr = request.getHeader("X-User-Id");
        String token = TokenUtil.getTokenForAuthorization(request);

        // 解析 JWT（如有）
        Claims claims = null;
        if (StringUtils.hasLength(token) && StringUtils.hasLength(tokenSecret)) {
            try {
                claims = TokenUtil.parseClaimsFromToken(token, tokenSecret);
            } catch (Exception e) {
                // JWT 解析失败时静默，Spring Security 会做最终决策
            }
        }

        // 有 X-User-* 请求头 → 必须同时有匹配的 JWT
        if (StringUtils.hasLength(userIdStr)) {
            if (claims == null) {
                // 有请求头但无有效 JWT → 可能是内网伪造
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "缺少有效的 JWT");
                return false;
            }

            Long headerUserId = Long.parseLong(userIdStr);
            Long claimsUserId = claims.get("user_id", Long.class);
            if (!Objects.equals(headerUserId, claimsUserId)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 身份与请求头不匹配");
                return false;
            }

            // 以 JWT claims 为准设置 SecurityContext
            setSecurityContext(claims);
            return true;
        }

        // 只有 JWT → 从 claims 设置
        if (claims != null) {
            setSecurityContext(claims);
            return true;
        }

        // 无身份信息 → 放行（公开接口由 Spring Security permitAll 处理）
        return true;
    }

    /**
     * 从 JWT claims 解析身份并设置 SecurityContext.
     *
     * @param claims JWT 声明
     */
    private void setSecurityContext(Claims claims) {
        Long userId = claims.get("user_id", Long.class);
        String username = claims.get("user_name", String.class);
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);

        if (userId != null) {
            FillUserUtil.setCurrentUser(userId, username);
            if (roles != null && !roles.isEmpty()) {
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                JwtUserEntity jwtUserEntity = new JwtUserEntity();
                jwtUserEntity.setId(userId);
                jwtUserEntity.setUsername(username);
                jwtUserEntity.setRoles(roles);
                jwtUserEntity.setAuthorities(authorities);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(jwtUserEntity, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        FillUserUtil.clearCurrentUser();
    }
}
package cn.net.mall.auth.interceptor;

import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.redis.TokenHelper;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.util.SpringUtil;
import cn.net.mall.util.TokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 权限拦截器
 *
 * 优先从 Gateway 透传的 Header（X-User-Id / X-User-Name / X-Roles）取身份，0 Redis。
 * 如果没有 Header（直连或旧链路），回退到 JWT 解析。
 *
 * @date 2025/1/13 16:25
 */
public class AuthApiInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 优先从 Gateway Header 取身份
        String userIdStr = request.getHeader("X-User-Id");
        if (StringUtils.hasLength(userIdStr)) {
            Long userId = Long.parseLong(userIdStr);
            String username = request.getHeader("X-User-Name");
            String rolesStr = request.getHeader("X-Roles");

            FillUserUtil.setCurrentUser(userId, username);
            if (StringUtils.hasLength(rolesStr)) {
                List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesStr.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                JwtUserEntity jwtUserEntity = new JwtUserEntity();
                jwtUserEntity.setId(userId);
                jwtUserEntity.setUsername(username);
                jwtUserEntity.setAuthorities(authorities);
                jwtUserEntity.setRoles(Arrays.asList(rolesStr.split(",")));
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(jwtUserEntity, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            return true;
        }

        // 2. 回退：Gateway 未透传，从 JWT claims 解析
        String token = TokenUtil.getTokenForAuthorization(request);
        if (StringUtils.hasLength(token)) {
            TokenHelper tokenHelper = SpringUtil.getBean("tokenHelper", TokenHelper.class);
            Claims claims = tokenHelper.getClaimsFromToken(token);
            if (claims != null) {
                Long userId = claims.get("user_id", Long.class);
                String username = claims.get("user_name", String.class);
                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);

                if (userId != null) {
                    FillUserUtil.setCurrentUser(userId, username);
                    if (roles != null && !roles.isEmpty()) {
                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                        JwtUserEntity jwtUserEntity = new JwtUserEntity();
                        jwtUserEntity.setId(userId);
                        jwtUserEntity.setUsername(username);
                        jwtUserEntity.setRoles(roles);
                        jwtUserEntity.setAuthorities(authorities);
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(jwtUserEntity, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                    return true;
                }

                // 3. 旧 token fallback：无 user_id claim，走 Redis
                String oldUsername = claims.getSubject();
                if (StringUtils.hasLength(oldUsername)) {
                    JwtUserEntity userEntity = (JwtUserEntity) tokenHelper.getUserDetailsFromUsername(oldUsername);
                    if (userEntity != null) {
                        FillUserUtil.setCurrentUser(userEntity.getId(), userEntity.getUsername());
                    }
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
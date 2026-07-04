package cn.net.mall.message.websocket;

import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.redis.UserTokenHelper;
import cn.net.mall.util.TokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * WebSocket 握手拦截器
 *
 * WebSocket 不走 Gateway，所以直接从 JWT claims 解析身份，不查 Redis。
 */
@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final UserTokenHelper userTokenHelper;

    public AuthHandshakeInterceptor(UserTokenHelper userTokenHelper) {
        this.userTokenHelper = userTokenHelper;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = extractToken(request);
        if (StringUtils.hasLength(token)) {
            try {
                Claims claims = userTokenHelper.getClaimsFromToken(token);
                if (claims != null) {
                    Long userId = claims.get("user_id", Long.class);
                    String username = claims.get("user_name", String.class);

                    if (userId == null) {
                        // 旧 token fallback
                        String oldUsername = claims.getSubject();
                        if (StringUtils.hasLength(oldUsername)) {
                            attributes.put("username", oldUsername);
                        }
                    } else {
                        @SuppressWarnings("unchecked")
                        List<String> roles = claims.get("roles", List.class);
                        List<SimpleGrantedAuthority> authorities = roles != null
                                ? roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                                : List.of();

                        JwtUserEntity jwtUserEntity = new JwtUserEntity();
                        jwtUserEntity.setId(userId);
                        jwtUserEntity.setUsername(username);
                        jwtUserEntity.setRoles(roles);
                        jwtUserEntity.setAuthorities(authorities);

                        Authentication authentication = new UsernamePasswordAuthenticationToken(jwtUserEntity, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        attributes.put("username", username);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractToken(ServerHttpRequest request) {
        // 从查询参数提取
        String query = request.getURI().getQuery();
        if (StringUtils.hasLength(query)) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf('=');
                if (idx > 0) {
                    String key = pair.substring(0, idx);
                    if ("token".equalsIgnoreCase(key) || "access_token".equalsIgnoreCase(key)) {
                        String value = pair.substring(idx + 1);
                        if (StringUtils.hasLength(value)) {
                            try { return URLDecoder.decode(value, "UTF-8"); } catch (Exception e) { return value; }
                        }
                    }
                }
            }
        }

        // 从请求参数提取
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String p1 = servletRequest.getServletRequest().getParameter("token");
            String p2 = servletRequest.getServletRequest().getParameter("access_token");
            String token = StringUtils.hasLength(p1) ? p1 : p2;
            if (StringUtils.hasLength(token)) return token;
            token = TokenUtil.getTokenForAuthorization(servletRequest.getServletRequest());
            if (StringUtils.hasLength(token)) return token;
        }

        // 从 Header 提取
        List<String> headers = request.getHeaders().get(TokenUtil.AUTHORIZATION);
        if (headers != null && !headers.isEmpty()) {
            String authorization = headers.get(0);
            if (StringUtils.hasLength(authorization)) {
                return authorization.startsWith("Bearer ") ? authorization.substring("Bearer ".length()) : authorization;
            }
        }

        return null;
    }
}

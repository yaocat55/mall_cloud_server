package cn.net.mall.message.websocket;

import cn.net.mall.redis.TokenHelper;
import cn.net.mall.redis.UserTokenHelper;
import cn.net.mall.util.TokenUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final UserTokenHelper userTokenHelper;
    private final TokenHelper tokenHelper;

    public AuthHandshakeInterceptor(UserTokenHelper userTokenHelper, TokenHelper tokenHelper) {
        this.userTokenHelper = userTokenHelper;
        this.tokenHelper = tokenHelper;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = null;
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
                            try {
                                token = URLDecoder.decode(value, "UTF-8");
                            } catch (Exception e) {
                                token = value;
                            }
                            break;
                        }
                    }
                }
            }
        }
            if (!StringUtils.hasLength(token) && request instanceof ServletServerHttpRequest servletRequest) {
            String p1 = servletRequest.getServletRequest().getParameter("token");
            String p2 = servletRequest.getServletRequest().getParameter("access_token");
            token = StringUtils.hasLength(p1) ? p1 : p2;
            if (!StringUtils.hasLength(token)) {
                token = TokenUtil.getTokenForAuthorization(servletRequest.getServletRequest());
            }
        }
        if (!StringUtils.hasLength(token)) {
            List<String> headers = request.getHeaders().get(TokenUtil.AUTHORIZATION);
            if (headers != null && !headers.isEmpty()) {
                String authorization = headers.get(0);
                if (StringUtils.hasLength(authorization)) {
                    token = authorization.startsWith("Bearer ") ? authorization.substring("Bearer ".length()) : authorization;
                }
            }
        }
        if (StringUtils.hasLength(token)) {
            try {
                String username = userTokenHelper.getUsernameFromToken(token);
                if (StringUtils.hasLength(username)) {
                    UserDetails userDetails = tokenHelper.getUserDetailsFromUsername(username);
                    if (userDetails != null) {
                        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                    attributes.put("username", username);
                }
            } catch (Exception ignored) {
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}

package cn.net.mall.gateway.filter;

import cn.net.mall.util.TokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 网关全局鉴权过滤器
 *
 * 职责明确：
 * 1. 白名单路径直接放行（登录、验证码等）
 * 2. JWT 验签 + 过期校验
 * 3. 验签通过后将 userId / userName / roles 写入请求头透传下游
 *
 * 不做的事：
 * - 不查 Redis 黑名单（黑名单由 mall-auth 内部处理，仅针对 admin 接口）
 * - 不拦截无效/过期 token（放行后由下游服务自行决定）
 *
 * 黑名单设计思路：
 * C 端用户不需要踢人下线功能。异地登录应通过 mall-message 发通知提醒，而非强制踢人。
 * Admin 端的踢人/权限变更可在 auth 服务内部校验，不需要 Gateway 层参与。
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${mall.mgt.tokenSecret}")
    private String tokenSecret;

    @Value("${gateway.filter.noAuth:}")
    private String noAuth;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI uri = exchange.getRequest().getURI();

        // 白名单路径直接放行
        if (isNoAuth(uri.getPath())) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String token = TokenUtil.getToken(request);

        if (StringUtils.hasLength(token)) {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                // 透传身份到下游服务
                ServerHttpRequest.Builder builder = request.mutate();
                Long userId = claims.get("user_id", Long.class);
                String username = claims.get("user_name", String.class);
                Object rolesObj = claims.get("roles");

                if (userId != null) {
                    builder.header("X-User-Id", String.valueOf(userId));
                }
                if (StringUtils.hasLength(username)) {
                    builder.header("X-User-Name", username);
                }
                if (rolesObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) rolesObj;
                    builder.header("X-Roles", String.join(",", roles));
                }

                return chain.filter(exchange.mutate().request(builder.build()).build());
            } catch (JwtException e) {
                log.warn("JWT 验签失败（已放行，由下游服务拦截）: {}", e.getMessage());
            }
        }

        // 无 token 或验签失败均放行，由下游服务决定是否拒绝
        return chain.filter(exchange);
    }

    private boolean isNoAuth(String requestUri) {
        if (StringUtils.hasLength(noAuth)) {
            for (String url : noAuth.split(",")) {
                if (requestUri.startsWith(url)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return -2;
    }
}

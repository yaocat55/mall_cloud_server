package cn.net.mall.gateway.filter;

import cn.net.mall.redis.RedisUtil;
import cn.net.mall.util.TokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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
import java.util.List;

/**
 * 网关全局鉴权过滤器
 *
 * 功能：
 * 1. 白名单路径直接放行（登录、验证码等）
 * 2. JWT 签名校验 + 黑名单检查（Gateway 直连 Redis）
 * 3. 验签通过后将 userId / userName / roles 写入请求头透传下游
 *
 * 注意：
 * - 黑名单 Redis 不可用时降级为放行（非核心路径）
 * - 业务服务从 Header 取身份，不再查 Redis
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${mall.mgt.tokenSecret:123456test}")
    private String tokenSecret;

    @Value("${gateway.filter.noAuth:}")
    private String noAuth;

    private final RedisUtil redisUtil;

    public AuthFilter(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

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
                        .setSigningKey(tokenSecret)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                // 查黑名单：token 是否被踢
                String jti = claims.getId();
                if (StringUtils.hasLength(jti)) {
                    try {
                        String blacklisted = redisUtil.get("blacklist:" + jti);
                        if (blacklisted != null) {
                            log.warn("黑名单 token 被拦截, jti={}", jti);
                            return unauthorized(exchange, "token 已被踢下线");
                        }
                    } catch (Exception e) {
                        // Redis 不可用时降级放行
                        log.warn("黑名单 Redis 不可用，降级放行", e);
                    }
                }

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

        // 无 token 或验签失败均放行
        return chain.filter(exchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
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

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
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * 网关全局鉴权过滤器
 *
 * 功能：
 * 1. 白名单路径直接放行（登录、验证码等）
 * 2. JWT 签名校验（仅验签 + 过期，不查 Redis）
 * 3. 验签通过后将用户名写入请求头 X-User-Name，下游服务可据此恢复上下文
 *
 * 注意：
 * - 踢人下线 / 完整用户信息查询由 mall-auth-api-starter 在下游服务中完成
 * - 因为时区使用 localDateTime ，如果遇到时区相关时间问题自己调整一下
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${mall.mgt.tokenSecret:123456test}")
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

        // 有 token 则验签，通过后把用户名传给下游
        if (StringUtils.hasLength(token)) {
            try {
                SecretKey key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String username = claims.getSubject();
                if (StringUtils.hasLength(username)) {
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .header("X-User-Name", username)
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                }
            } catch (JwtException e) {
                log.warn("JWT 验签失败（已放行，由下游服务拦截）: {}", e.getMessage());
            }
        }

        // 无 token 或验签失败均放行，鉴权强制由下游服务完成
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

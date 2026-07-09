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
 * Gateway 全局 JWT 鉴权过滤器.
 *
 * 职责：
 * 1. 白名单路径直接放行（登录、注册、验证码等公开接口）
 * 2. JWT 验签 + 过期校验
 * 3. 验签通过后将 userId / userName / roles 写入请求头透传下游服务
 *
 * 不做的事：
 * - 不查 Redis 黑名单（黑名单由 mall-admin 内部处理，仅针对 admin 接口）
 * - 不拦截无效/过期 token —— 放行后由下游服务自行决定
 *
 * 黑名单设计思路：
 * C 端用户不需要踢人下线功能，异地登录应通过消息通知提醒而非强制踢人。
 * Admin 端的踢人/权限变更可在 auth 服务内部校验，不需要 Gateway 层参与。
 *
 * 关联组件：
 * - TokenUtil：用于从请求中提取 token
 * - AuthApiInterceptor：auth 服务中的 API 拦截器
 * - JwtTokenFilter：admin 服务中的 JWT 过滤器
 *
 * @author system
 * @since 1.0.0
 * @see TokenUtil
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${mall.mgt.tokenSecret}")
    private String tokenSecret;

    @Value("${gateway.filter.noAuth:}")
    private String noAuth;

    /**
     * 全局鉴权入口.
     *
     * 处理流程：
     * 1. 匹配白名单路径 → 直接放行
     * 2. 有 token → 验签 → 透传 Header → 放行
     * 3. 无 token 或验签失败 → 返回 401
     *
     * @param exchange 当前请求-响应交换对象
     * @param chain 过滤器链
     * @return 响应流
     */
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
                // JWT 验签，与 TokenUtil.generateToken 使用相同的 Keys.hmacShaKeyFor 签名算法
                Claims claims = Jwts.parser()
                        .verifyWith(Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                // 验签通过，将身份信息写入请求头，透传给下游服务
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
                log.warn("JWT 验签失败，返回 401: {}", e.getMessage());
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }

        // 无 token 返回 401
        log.warn("未携带 token，返回 401, uri={}", uri.getPath());
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    /**
     * 判断请求路径是否在白名单中.
     *
     * 匹配规则：
     * 1. 末尾带 / → 前缀匹配
     *    例如：/api/basic/v1/commonSmsRecord/ 匹配其下所有路径
     * 2. 末尾不带 / → 精确匹配
     *    例如：/api/admin/v1/auth/login 只匹配路径本身
     *
     * @param requestUri 请求路径（可能包含 query string）
     * @return true 表示在白名单中，不需要 JWT 认证；false 表示需要认证
     */
    private boolean isNoAuth(String requestUri) {
        if (!StringUtils.hasLength(noAuth)) {
            return false;
        }

        // 去掉 query string，只取路径部分
        String path = requestUri.contains("?")
                ? requestUri.substring(0, requestUri.indexOf("?"))
                : requestUri;

        for (String url : noAuth.split(",")) {
            url = url.trim();
            if (url.isEmpty()) {
                continue;
            }

            if (url.endsWith("/")) {
                // 末尾带 / → 前缀匹配，适用于整段都是公开接口的路径
                if (path.startsWith(url)) {
                    return true;
                }
            } else {
                // 末尾不带 / → 精确匹配，适用于与保护接口混在同一前缀下的公开接口
                if (path.equals(url)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取过滤器执行顺序.
     *
     * @return 顺序值，值越小优先级越高
     */
    @Override
    public int getOrder() {
        return -2;
    }
}
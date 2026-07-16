package cn.net.mall.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;


/**
 * token处理工具
 *
 * @date 2024/1/12 下午1:01
 */
public abstract class TokenUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";

    private TokenUtil() {
    }

    /**
     * 从Header中获取 Authorization
     *
     * @param request 请求
     * @return Authorization
     */
    public static String getAuthorization(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }

    /**
     * 从authorization中解析token
     * 
* authorization字符串是下面这样的：
     * Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXNhbiIsImV4cCI6MTcwNTAzOTA3N30.DZV6CZYGla74CZaXU1sqnX9R_x5YxfTM-DWObURn3Uhr1E88XsOxOz8F_MDfh8AaVFm87zlGXAENC8soZNz0Qw
     *
     * @param request 用户请求
     * @return token
     */
    public static String getTokenForAuthorization(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);
        return parserToken(authorization);
    }

    /**
     * 从authorization中解析token
     * 
* authorization字符串是下面这样的：
     * Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXNhbiIsImV4cCI6MTcwNTAzOTA3N30.DZV6CZYGla74CZaXU1sqnX9R_x5YxfTM-DWObURn3Uhr1E88XsOxOz8F_MDfh8AaVFm87zlGXAENC8soZNz0Qw
     *
     * @param request 用户请求
     * @return token
     */
    public static String getToken(ServerHttpRequest request) {
        List<String> params = request.getHeaders().get(AUTHORIZATION);
        String authorization = null;
        if (!CollectionUtils.isEmpty(params)) {
            authorization = params.get(0);
        }
        return parserToken(authorization);
    }

    /**
     * 从 authorization 字符串中解析 token（去除 "Bearer " 前缀）
     *
     * @param authorization Authorization 头字符串
     * @return token
     */
    public static String getTokenFromAuthorization(String authorization) {
        return parserToken(authorization);
    }

    private static String parserToken(String authorization) {
        if (!StringUtils.hasLength(authorization)) {
            return null;
        }
        if (authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 生成带身份 claims 的 JWT（替代 TokenHelper.generateToken）
     * 使用 HS512 + 密钥字符串，与 mall-redis-spring-boot-starter 的 UserTokenHelper 兼容
     * 
* 注意：密钥长度必须 ≥ 512 bits（64 个 ASCII 字符），否则 JJWT 0.12+ 会抛出 WeakKeyException
     */
    public static String generateToken(Long userId, String username, List<String> roles,
                                        String secret, int expireSeconds) {
        return Jwts.builder()
                .subject(username)
                .claim("user_id", userId)
                .claim("user_name", username)
                .claim("roles", roles)
                .id(UuidUtil.getUuid())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(expireSeconds)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    /**
     * 从请求中解析 JWT claims
     */
    public static Claims parseClaims(HttpServletRequest request, String secret) {
        String token = getTokenForAuthorization(request);
        return parseClaimsFromToken(token, secret);
    }

    /**
     * 从 token 字符串中解析 JWT claims
     * 与 mall-redis-spring-boot-starter 的 UserTokenHelper.getClaimsFromToken 一致
     */
    public static Claims parseClaimsFromToken(String token, String secret) {
        if (!StringUtils.hasLength(token)) return null;
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token);
            return claimsJws.getPayload();
        } catch (Exception e) {
            return null;
        }
    }
}

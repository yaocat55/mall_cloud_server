package cn.net.mall.util;

import cn.net.mall.constant.NumberConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;
import java.util.List;


/**
 * token处理工具
 *
 * @date 2024/1/12 下午1:01
 */
public abstract class TokenUtil {

    private static final String AUTHORIZATION_PREFIX = "Basic";
    private static final String AUTHORIZATION_SEPARATE = "@";
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
     * <p>
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
     * <p>
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

    private static String parserToken(String authorization) {
        if (!StringUtils.hasLength(authorization)) {
            return null;
        }

        if (authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }

        if (!authorization.contains(AUTHORIZATION_PREFIX)
                || !authorization.contains(AUTHORIZATION_SEPARATE)) {
            return null;
        }

        String[] values = authorization.split(AUTHORIZATION_SEPARATE);
        if (values.length != NumberConstant.NUMBER_2) {
            return null;
        }
        return values[1];
    }

    /**
     * 生成带身份 claims 的 JWT（替代 TokenHelper.generateToken）
     * 使用 HS512 + 密钥字符串，与 mall-redis-spring-boot-starter 的 UserTokenHelper 兼容
     */
    public static String generateToken(Long userId, String username, List<String> roles,
                                        String secret, int expireSeconds) {
        return Jwts.builder()
                .setSubject(username)
                .claim("user_id", userId)
                .claim("user_name", username)
                .claim("roles", roles)
                .setId(UuidUtil.getUuid())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(expireSeconds)))
                .signWith(SignatureAlgorithm.HS512, secret)
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
                    .setSigningKey(secret)
                    .build()
                    .parseSignedClaims(token);
            return claimsJws.getPayload();
        } catch (Exception e) {
            return null;
        }
    }
}

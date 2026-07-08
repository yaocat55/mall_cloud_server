package cn.net.mall.redis;

import cn.net.mall.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.Objects;

/**
 * @date 2024/6/25 下午4:34
 */
@Slf4j
public class UserTokenHelper {

    private static final String TOKEN_PREFIX = "token:";
    private static final String USER_PREFIX = "user:";

    private final RedisUtil redisUtil;

    @Getter
    @Value("${mall.mgt.tokenSecret:123456test}")
    private String tokenSecret;
    @Value("${mall.mgt.tokenExpireTimeInRecord:3600}")
    private int tokenExpireTimeInRecord;

    public UserTokenHelper(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    protected RedisUtil getRedisUtil() {
        return redisUtil;
    }

    /**
     * 生成token
     *
     * @param username 用户名
     * @param json     用户信息
     * @return
     */
    public String generateToken(String username, String json) {
        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(generateExpired())
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
        redisUtil.set(getTokenKey(username), token, tokenExpireTimeInRecord);
        redisUtil.set(getUserKey(username), json, tokenExpireTimeInRecord);
        return token;
    }


    public String getTokenKey(String username) {
        return getKey(TOKEN_PREFIX, username);
    }

    public String getUserKey(String username) {
        return getKey(USER_PREFIX, username);
    }


    /**
     * 从token中解析出username
     *
     * @param token token
     * @return username
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (Objects.isNull(claims)) {
            return null;
        }
        return claims.getSubject();
    }

    /**
     * 获得 Claims
     *
     * @param token Token
     * @return Claims
     */

    public Claims getClaimsFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(getTokenSecret())
                    .build()
                    .parseSignedClaims(token);
            return claimsJws.getPayload();
        } catch (Exception e) {
            log.info("获得 Claims失败：", e);
            throw new BusinessException(HttpStatus.FORBIDDEN.value(), "请先登录");
        }
    }


    /**
     * 计算过期时间
     *
     * @return Date
     */
    protected Date generateExpired() {
        return new Date(System.currentTimeMillis() + tokenExpireTimeInRecord * 1000);
    }


    protected String getKey(String prefix, String userName) {
        return String.format("%s%s", prefix, userName);
    }
}

package cn.net.mall.redis;

import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.exception.BusinessException;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * token 帮助类，提供基于 Spring Security 上下文的 Token 操作
 * <p>
 * 注意：本类依赖 spring-boot-starter-security，当类路径中不存在 Spring Security 时自动跳过。
 *
 * @date 2024/1/11 下午7:59
 */
@Slf4j
public class TokenHelper extends UserTokenHelper {

    public TokenHelper(RedisUtil redisUtil) {
        super(redisUtil);
    }

    /**
     * 获取当前登录的用户名称
     *
     * @return 用户名称
     */
    public String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() instanceof String) {
            throw new BusinessException(HttpStatus.FORBIDDEN.value(), "当前登录状态过期");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    /**
     * 生成token
     *
     * @param userDetails 用户信息
     * @return token
     */
    public String generateToken(UserDetails userDetails) {
        return super.generateToken(userDetails.getUsername(), JSON.toJSONString(userDetails));
    }

    /**
     * 根据用户名称查询用户详情信息
     *
     * @param username 用户名称
     * @return 用户详情
     */
    public UserDetails getUserDetailsFromUsername(String username) {
        String userKey = getUserKey(username);
        String userDetailJson = getRedisUtil().get(userKey);
        if (!org.springframework.util.StringUtils.hasLength(userDetailJson)) {
            return null;
        }
        return JSON.parseObject(userDetailJson, JwtUserEntity.class);
    }

    /**
     * 获取token
     *
     * @param username 用户名称
     * @return token
     */
    public String getToken(String username) {
        return getRedisUtil().get(getTokenKey(username));
    }

    /**
     * 删除token
     *
     * @param token token
     */
    public void delToken(String token) {
        String username = getUsernameFromToken(token);
        getRedisUtil().del(getTokenKey(username));
        getRedisUtil().del(getUserKey(username));
    }
}

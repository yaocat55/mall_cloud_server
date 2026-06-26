package cn.net.mall.helper;

import cn.hutool.json.JSONUtil;
import cn.net.mall.entity.BaseEntity;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.util.RedisUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * token 帮助类
 *
 * @date 2024/1/11 下午7:59
 */
@Slf4j
@Component
public class TokenHelper extends UserTokenHelper {

    private static final String TOKEN_PREFIX = "token:";
    private static final String USER_PREFIX = "user:";

    private final RedisUtil redisUtil;

    public TokenHelper(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    /**
     * 填充修改用户信息
     *
     * @param baseEntity 实体
     */
    public void fillUpdateUserInfo(BaseEntity baseEntity) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FillUserUtil.checkUserLoginStatus(authentication);
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            throw new BusinessException(HttpStatus.FORBIDDEN.value(), "登录已过期，请重新登录");
        }
        JwtUserEntity jwtUserEntity = (JwtUserEntity) principal;
        baseEntity.setUpdateUserId(jwtUserEntity.getId());
        baseEntity.setUpdateUserName(jwtUserEntity.getUsername());
    }


    /**
     * 获取当前登录的用户名称
     *
     * @return 用户名称
     */
    public String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FillUserUtil.checkUserLoginStatus(authentication);
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            throw new BusinessException(HttpStatus.FORBIDDEN.value(), "登录已过期，请重新登录");
        }
        UserDetails userDetails = (UserDetails) principal;
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
        String userDetailJson = redisUtil.get(userKey);
        if (!StringUtils.hasLength(userDetailJson)) {
            return null;
        }
        return JSON.parseObject(userDetailJson, JwtUserEntity.class);
        // return JSON.parseObject(userDetailJson, JwtUserEntity.class);
    }


    /**
     * 获取token
     *
     * @param username 用户名称
     * @return token
     */
    public String getToken(String username) {
        return redisUtil.get(getKey(TOKEN_PREFIX, username));
    }

    /**
     * 删除token
     *
     * @param token 用户名称
     */
    public void delToken(String token) {
        String username = getUsernameFromToken(token);
        redisUtil.del(getKey(TOKEN_PREFIX, username));
        redisUtil.del(getKey(USER_PREFIX, username));
    }

    /**
     * 获取用户详情
     *
     * @param username 用户名称
     * @return 用户详情
     */
    public UserDetails getUserDetails(String username) {
        String userJson = redisUtil.get(getKey(USER_PREFIX, username));
        return JSONUtil.toBean(userJson, UserDetails.class);
    }
}

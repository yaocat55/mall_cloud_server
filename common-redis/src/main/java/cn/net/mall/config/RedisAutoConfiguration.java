package cn.net.mall.config;

import cn.net.mall.redis.RedisUtil;
import cn.net.mall.redis.TokenHelper;
import cn.net.mall.redis.UserTokenHelper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RedisUtil redisUtil(StringRedisTemplate stringRedisTemplate) {
        return new RedisUtil(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "io.jsonwebtoken.Jwts")
    public UserTokenHelper userTokenHelper(RedisUtil redisUtil) {
        return new UserTokenHelper(redisUtil);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "org.springframework.security.core.Authentication")
    public TokenHelper tokenHelper(RedisUtil redisUtil) {
        return new TokenHelper(redisUtil);
    }
}

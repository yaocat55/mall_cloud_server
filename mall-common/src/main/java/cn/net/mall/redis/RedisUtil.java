package cn.net.mall.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @date 2024/1/9 下午5:14
 */
@Slf4j
public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    /**
     * 批量保存hash结构数据
     *
     * @param key key
     * @param map map
     * @return 是否成功
     */
    public boolean putHashMap(String key, Map<Object, Object> map) {
        try {
            stringRedisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("Redis保存数据失败：", e);
            return false;
        }
    }

    /**
     * 保存hash结构数据
     *
     * @param key     key
     * @param hashKey hashKey
     * @param value   值
     * @return 是否成功
     */
    public boolean putHashValue(String key, Object hashKey, Object value) {
        try {
            stringRedisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            log.error("Redis保存数据失败：", e);
            return false;
        }
    }

    /**
     * 获取hash结构数据
     *
     * @param key key
     * @param key hashKey
     * @return 值
     */
    public Object getHashValue(String key, Object hashKey) {
        return key == null || hashKey == null ? null : stringRedisTemplate.opsForHash().get(key, hashKey);
    }


    /**
     * 保存缓存，同时设置过期时间
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, String value, long expireTime) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("Redis保存数据失败：", e);
            return false;
        }
    }

    /**
     * 保存缓存
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("Redis保存数据失败：", e);
            return false;
        }
    }

    /**
     * 保存缓存，如果key存在，则不做处理
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean setIfAbsent(String key, String value) {
        try {
            return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
        } catch (Exception e) {
            log.error("Redis保存数据失败：", e);
            return false;
        }
    }

    /**
     * 获取普通缓存
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 加1操作
     *
     * @param key   键
     * @param value 值
     * @return 值
     */
    public Long increment(String key, long value) {
        return stringRedisTemplate.opsForValue().increment(key, value);
    }

    /**
     * 加1操作
     *
     * @param key 键
     * @return 值
     */
    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key, 1);
    }

    /**
     * 减1操作
     *
     * @param key 键
     * @return 值
     */
    public Long decrement(String key) {
        return stringRedisTemplate.opsForValue().increment(key, -1);
    }

    /**
     * 保存缓存
     *
     * @param key 键
     * @return true成功 false失败
     */
    public void del(String key) {
        try {
            if (StringUtils.hasLength(key)) {
                stringRedisTemplate.delete(key);
            }
        } catch (Exception e) {
            log.error("Redis删除数据失败：", e);
        }
    }
}

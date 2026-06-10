package cn.net.mall.basic.mybatis;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

import static cn.hutool.core.util.StrUtil.UNDERLINE;

/**
 * @date 2024/3/27 下午6:43
 */
public class DictCacheKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getSimpleName() + UNDERLINE + StringUtils.arrayToDelimitedString(params, UNDERLINE);
    }
}

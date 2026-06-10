package cn.net.mall.auth.util;

import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

import static cn.net.mall.handler.GlobalApiResultHandler.URL_PREFIX;

/**
 * 无需登录map
 *
 * @date 2024/9/28 下午5:29
 */
public abstract class NoLoginMap {
    private static final Set<String> NO_LOGIN_URL_SET = new HashSet<>();

    private NoLoginMap() {

    }

    /**
     * 获取不需要登录url集合
     *
     * @return 不需要登录url集合
     */
    public static Set<String> getNoLoginUrlSet() {
        return NO_LOGIN_URL_SET;
    }

    /**
     * 初始化set
     *
     * @param noLoginUrls url集合
     */
    public static void initSet(Set<String> noLoginUrls) {
        if (CollectionUtils.isEmpty(noLoginUrls)) {
            return;
        }
        NO_LOGIN_URL_SET.addAll(noLoginUrls);
    }

    /**
     * 不存在该url
     *
     * @param url url地址
     * @return 是否不存在
     */
    public static boolean notExist(String url) {
        if (!url.startsWith(URL_PREFIX)) {
            return false;
        }
        return !NO_LOGIN_URL_SET.contains(url);
    }
}

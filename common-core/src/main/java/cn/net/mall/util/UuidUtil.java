package cn.net.mall.util;

import java.util.UUID;

/**
 * uuid工具
 *
 * @date 2024/9/19 下午3:16
 */
public abstract class UuidUtil {

    private UuidUtil() {
    }

    /**
     * 获取uuid字符串
     *
     * @return uuid字符串
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

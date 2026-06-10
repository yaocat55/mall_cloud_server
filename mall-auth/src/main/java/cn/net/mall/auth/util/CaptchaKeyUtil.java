package cn.net.mall.auth.util;

/**
 * 验证码 key工具类
 *
 * @date 2024/11/8 下午3:02
 */
public abstract class CaptchaKeyUtil {
    private static final String CAPTCHA_PREFIX = "captcha:";

    private CaptchaKeyUtil() {

    }

    /**
     * 获取验证码Redis的key
     *
     * @param uuid 验证码uuid
     * @return 验证码Redis的key
     */
    public static String getCaptchaKey(String uuid) {
        return String.format("%s%s", CAPTCHA_PREFIX, uuid);
    }
}

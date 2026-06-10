package cn.net.mall.util;

import cn.net.mall.enums.SmsTypeEnum;
import cn.net.mall.exception.BusinessException;

/**
 * 短信验证码 key工具类
 *
 * @date 2024/11/8 下午3:36
 */
public abstract class SmsKeyUtil {
    private static final String SMS_REGISTER_CODE_PREFIX = "smsRegisterCode:";
    private static final String SMS_LOGIN_CODE_PREFIX = "smsLoginCode:";

    private SmsKeyUtil() {

    }

    /**
     * 获取短信验证码Redis中的key
     *
     * @param phone 手机号
     * @param type  短信类型
     * @return key
     */
    public static String getSmsCodePrefixKey(String phone, SmsTypeEnum type) {
        return String.format("%s%s", getPrefix(type), phone);
    }

    private static String getPrefix(SmsTypeEnum type) {
        switch (type) {
            case LOGIN:
                return SMS_REGISTER_CODE_PREFIX;
            case REGISTER:
                return SMS_LOGIN_CODE_PREFIX;
            default:
                throw new BusinessException("短信类型错误");
        }

    }
}

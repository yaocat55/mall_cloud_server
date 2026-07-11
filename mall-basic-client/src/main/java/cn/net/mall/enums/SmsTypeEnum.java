package cn.net.mall.enums;

import cn.net.mall.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信类型枚举
 *
 * @date 2024/1/4 下午3:39
 */
@AllArgsConstructor
@Getter
public enum SmsTypeEnum {

    /**
     * 注册验证码
     */
    REGISTER(1, "注册验证码"),

    /**
     * 重置密码
     */
    RESET_PASSWORD(2, "重置密码"),

    /**
     * 登录验证码
     */
    LOGIN(3, "登录验证码"),


    /**
     * 绑定手机号
     */
    BIND_PHONE(4, "绑定手机号"),

    /**
     * 会员过期提醒
     */
    MEMBER_EXPIRE(5, "会员过期提醒");

    private Integer value;

    private String desc;


    /**
     * 获取短信类型枚举
     *
     * @param value 短信类型
     * @return 短信类型枚举
     */
    public static SmsTypeEnum getSmsTypeEnum(Integer value) {
        for (SmsTypeEnum smsTypeEnum : SmsTypeEnum.values()) {
            if (smsTypeEnum.getValue().equals(value)) {
                return smsTypeEnum;
            }
        }
        throw new BusinessException("短信类型错误");
    }
}

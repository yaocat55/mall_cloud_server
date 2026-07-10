package cn.net.mall.enums;

import cn.hutool.core.util.DesensitizedUtil;
import lombok.Getter;

/**
 * 脱敏数据类型
 *
 * @date 2024/5/23 下午5:16
 */
public enum SensitiveTypeEnum {

    MOBILE("mobile", "手机号") {
        @Override
        public String maskSensitiveData(String data) {
            //手机号前3位后4位脱敏，中间部分加*处理，比如：138****5678
            return DesensitizedUtil.mobilePhone(data);
        }
    },
    IDENTIFY("identify", "身份证号") {
        @Override
        public String maskSensitiveData(String data) {
            //身份证前3位后4位脱敏，中间部分加*处理，比如：110***********3706
            return DesensitizedUtil.idCardNum(data, 3, 4);
        }
    },
    BANKCARD("bankcard", "银行卡号") {
        @Override
        public String maskSensitiveData(String data) {
            //银行卡号前4位后4位脱敏，中间部分加*处理，比如：6225 **** **** *** 0845
            return DesensitizedUtil.bankCard(data);
        }
    },

    EMAIL("email", "邮箱") {
        @Override
        public String maskSensitiveData(String data) {
            //邮箱@符号后明文显示，@符号前的字符串，只显示第一个字符，其余加*处理，比如：z***********@test.com
            return DesensitizedUtil.email(data);
        }
    },
    DEFAULT("default", "默认") {
        @Override
        public String maskSensitiveData(String data) {
            // 默认原值返回，其他这个也没啥意义^_^
            return data;
        }
    },
    ;


    @Getter
    private String type;

    @Getter
    private String desc;

    SensitiveTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }


    /**
     * 遮挡敏感数据
     *
     * @param data
     * @return
     */
    public String maskSensitiveData(String data) {
        return data;
    }

}

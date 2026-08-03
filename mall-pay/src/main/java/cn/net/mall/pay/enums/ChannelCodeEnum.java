package cn.net.mall.pay.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付渠道编码枚举.
 */
@AllArgsConstructor
@Getter
@Schema(description = "支付渠道编码枚举", allowableValues = {"ALIPAY", "WECHAT_PAY", "WECHAT_MINI", "MOCK"})
public enum ChannelCodeEnum {

    /** 支付宝（App/手机网站） */
    ALIPAY("ALIPAY", "支付宝"),

    /** 微信支付（App） */
    WECHAT_PAY("WECHAT_PAY", "微信支付"),

    /** 微信小程序支付（JSAPI） */
    WECHAT_MINI("WECHAT_MINI", "微信小程序支付"),

    /** MOCK 模拟渠道（仅 dev/test 启用） */
    MOCK("MOCK", "模拟渠道");

    private final String code;
    private final String desc;

    public static ChannelCodeEnum from(String code) {
        if (code == null) {
            return null;
        }
        for (ChannelCodeEnum channel : values()) {
            if (channel.code.equals(code)) {
                return channel;
            }
        }
        return null;
    }
}

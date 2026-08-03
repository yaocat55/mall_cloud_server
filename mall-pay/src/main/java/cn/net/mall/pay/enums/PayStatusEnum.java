package cn.net.mall.pay.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付状态枚举（对应 pay_order.pay_status）.
 *
 * <p>状态只关心「收钱」；收完钱之后的退款走 refund_status 维度，不回退 pay_status。</p>
 * <p>待支付/支付中已合并为单一待支付态（10）。</p>
 */
@AllArgsConstructor
@Getter
@Schema(description = "支付状态枚举", allowableValues = {"10", "20", "30", "40"})
public enum PayStatusEnum {

    /** 10 待支付：支付单已创建（含已调起渠道、用户支付操作进行中）；超时/取消可重发新支付单 */
    WAIT_PAY(10, "待支付"),

    /** 20 已支付：渠道回调或主动查单确认成功，终态 */
    PAYMENT(20, "已支付"),

    /** 30 已关闭：超时未付/用户主动取消，可重新发起支付单 */
    CLOSED(30, "已关闭"),

    /** 40 支付失败：渠道明确返回失败（余额不足/风控拦截/交易关闭） */
    FAILURE(40, "支付失败");

    private final Integer value;
    private final String desc;

    /**
     * 根据 value 获取枚举
     */
    public static PayStatusEnum from(Integer value) {
        if (value == null) {
            return null;
        }
        for (PayStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}

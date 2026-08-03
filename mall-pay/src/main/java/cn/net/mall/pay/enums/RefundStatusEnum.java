package cn.net.mall.pay.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 退款单状态枚举（对应 pay_refund.refund_status）.
 */
@AllArgsConstructor
@Getter
@Schema(description = "退款单状态枚举", allowableValues = {"0", "1", "2", "3"})
public enum RefundStatusEnum {

    /** 0 待处理：退款单已创建，未提交渠道 */
    PENDING(0, "待处理"),

    /** 1 处理中：已提交渠道，等待回调/查单 */
    PROCESSING(1, "处理中"),

    /** 2 退款成功 */
    SUCCESS(2, "退款成功"),

    /** 3 退款失败（可重新发起退款单） */
    FAILED(3, "退款失败");

    private final Integer value;
    private final String desc;

    public static RefundStatusEnum from(Integer value) {
        if (value == null) {
            return null;
        }
        for (RefundStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}

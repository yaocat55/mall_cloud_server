package cn.net.mall.pay.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务方通知状态枚举（对应 pay_order.biz_notify_status）.
 */
@AllArgsConstructor
@Getter
@Schema(description = "业务方通知状态枚举", allowableValues = {"0", "1", "2"})
public enum BizNotifyStatusEnum {

    /** 0 待通知：支付/退款成功落库，尚未发 MQ */
    PENDING(0, "待通知"),

    /** 1 通知成功：MQ 已发送 */
    SUCCESS(1, "通知成功"),

    /** 2 通知失败：MQ 发送失败，定时重推补偿 */
    FAILED(2, "通知失败");

    private final Integer value;
    private final String desc;

    public static BizNotifyStatusEnum from(Integer value) {
        if (value == null) {
            return null;
        }
        for (BizNotifyStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}

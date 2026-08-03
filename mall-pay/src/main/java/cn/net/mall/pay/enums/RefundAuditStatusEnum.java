package cn.net.mall.pay.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 退款审核状态枚举（对应 pay_refund.audit_status）.
 */
@AllArgsConstructor
@Getter
@Schema(description = "退款审核状态枚举", allowableValues = {"0", "1", "2", "3"})
public enum RefundAuditStatusEnum {

    /** 0 无需审核：自动退款（默认） */
    NONE(0, "无需审核"),

    /** 1 待审核：退款请求的 biz_order_no 或 user_id 在 pay_order 查不到，进入人工审核 */
    WAIT_AUDIT(1, "待审核"),

    /** 2 审核通过 */
    PASSED(2, "审核通过"),

    /** 3 审核拒绝 */
    REJECTED(3, "审核拒绝");

    private final Integer value;
    private final String desc;

    public static RefundAuditStatusEnum from(Integer value) {
        if (value == null) {
            return null;
        }
        for (RefundAuditStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}

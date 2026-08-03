package cn.net.mall.pay.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对账差异类型枚举（对应 recon_result.diff_type）.
 */
@AllArgsConstructor
@Getter
@Schema(description = "对账差异类型枚举")
public enum DiffTypeEnum {

    /** 长款：渠道净额 > 平台净额（我方少记收款） */
    LONG_PAYMENT("LONG_PAYMENT", "长款：渠道净额大于我方"),

    /** 短款：渠道净额 < 平台净额（我方多记收款） */
    SHORT_PAYMENT("SHORT_PAYMENT", "短款：渠道净额小于我方"),

    /** 金额不一致：同一笔单两侧金额不同 */
    AMOUNT_MISMATCH("AMOUNT_MISMATCH", "金额不一致"),

    /** 平台有单、渠道对账单无（我方虚记/渠道漏单） */
    ONLY_PLATFORM("ONLY_PLATFORM", "仅平台有单"),

    /** 渠道有单、平台无（回调丢失/补单） */
    ONLY_CHANNEL("ONLY_CHANNEL", "仅渠道有单"),

    /** 手续费不一致 */
    FEE_MISMATCH("FEE_MISMATCH", "手续费不一致"),

    /** 状态不一致（如平台已支付，渠道已退款） */
    STATUS_MISMATCH("STATUS_MISMATCH", "状态不一致");

    private final String code;
    private final String desc;
}

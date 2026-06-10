package cn.net.mall.marketing.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 优惠券类型
 *
 * @date 2024/9/18 下午3:46
 */
@Getter
@AllArgsConstructor
public enum CouponTypeEnum {

    /**
     * 现金券
     */
    CASH(1, "现金券"),

    /**
     * 阶梯满减
     */
    NORMAL_FULL_REDUCTION(2, "阶梯满减"),

    /**
     * 每满减
     */
    EVERY_FULL_REDUCTION(3, "每满减"),

    /**
     * 通用折扣
     */
    NORMAL_DISCOUNT(4, "通用折扣"),

    /**
     * 满N件折扣
     */
    FULL_COUNT_DISCOUNT(5, "满N件折扣"),

    /**
     * 满Y元折扣
     */
    FULL_AMOUNT_DISCOUNT(6, "满Y元折扣");

    /**
     * 枚举值
     */
    private Integer value;


    /**
     * 枚举描述
     */
    private String desc;
}

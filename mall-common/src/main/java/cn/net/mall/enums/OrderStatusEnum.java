package cn.net.mall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 *
 * @date 2024/5/30 下午3:39
 */
@AllArgsConstructor
@Getter
public enum OrderStatusEnum {

    /**
     * 已下单/待支付
     */
    CREATE(1, "已下单"),

    /**
     * 已支付
     */
    PAY(2, "已支付"),

    /**
     * 已发货
     */
    SHIPPED(3, "已发货"),

    /**
     * 已完成
     */
    FINISH(4, "已完成"),

    /**
     * 已取消
     */
    CANCEL(5, "已取消"),



    /**
     * 已评价
     */
    COMMENT(7, "已评价"),

    /**
     * 退货申请
     */
    RETURN_APPLY(8, "退货申请"),

    /**
     * 已退货
     */
    RETURN(9, "已退货");

    private Integer value;

    private String desc;
}

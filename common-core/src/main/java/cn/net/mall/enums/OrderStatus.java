package cn.net.mall.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "订单状态", enumAsRef = true)
public enum OrderStatus {

    @Schema(description = "待付款") WAIT_PAY(1),
    @Schema(description = "已支付/待发货") PAID(2),
    @Schema(description = "已发货") SHIPPED(3),
    @Schema(description = "已完成") COMPLETED(4),
    @Schema(description = "已取消") CANCELLED(5),
    @Schema(description = "已退货") RETURNED(6),
    @Schema(description = "售后中") AFTER_SALE(7);

    private final int value;
    OrderStatus(int value) { this.value = value; }
    public int getValue() { return value; }
    public static OrderStatus from(Integer value) {
        if (value == null) return null;
        for (OrderStatus s : values()) { if (s.value == value) return s; }
        return null;
    }
}

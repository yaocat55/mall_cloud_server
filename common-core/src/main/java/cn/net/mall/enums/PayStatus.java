package cn.net.mall.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "支付状态", enumAsRef = true)
public enum PayStatus {

    @Schema(description = "未支付") UNPAID(1),
    @Schema(description = "已支付") PAID(2),
    @Schema(description = "已退款") REFUNDED(3),
    @Schema(description = "支付失败") FAILED(4);

    private final int value;
    PayStatus(int value) { this.value = value; }
    public int getValue() { return value; }
    public static PayStatus from(Integer value) {
        if (value == null) return null;
        for (PayStatus s : values()) { if (s.value == value) return s; }
        return null;
    }
}

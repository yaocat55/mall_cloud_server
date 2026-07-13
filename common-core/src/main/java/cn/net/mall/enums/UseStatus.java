package cn.net.mall.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "使用状态", enumAsRef = true)
public enum UseStatus {

    @Schema(description = "未使用") UNUSED(0),
    @Schema(description = "已使用") USED(1);

    private final int value;
    UseStatus(int value) { this.value = value; }
    public int getValue() { return value; }
    public static UseStatus from(Integer value) {
        if (value == null) return null;
        for (UseStatus s : values()) { if (s.value == value) return s; }
        return null;
    }
}

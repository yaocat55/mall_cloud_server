package cn.net.mall.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "是否状态", enumAsRef = true)
public enum YesNoStatus {

    @Schema(description = "是") YES(1),
    @Schema(description = "否") NO(0);

    private final int value;
    YesNoStatus(int value) { this.value = value; }
    public int getValue() { return value; }
    public static YesNoStatus from(Integer value) {
        if (value == null) return null;
        for (YesNoStatus s : values()) { if (s.value == value) return s; }
        return null;
    }
}

package cn.net.mall.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 有效状态枚举.
 */
@Schema(description = "有效状态", enumAsRef = true)
public enum ValidStatus {

    @Schema(description = "启用")
    ENABLED(1),

    @Schema(description = "禁用")
    DISABLED(0);

    private final int value;

    ValidStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ValidStatus from(Integer value) {
        if (value == null) return null;
        for (ValidStatus s : values()) {
            if (s.value == value) return s;
        }
        return null;
    }
}

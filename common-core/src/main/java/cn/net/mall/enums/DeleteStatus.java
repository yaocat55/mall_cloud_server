package cn.net.mall.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 删除状态枚举.
 */
@Schema(description = "删除状态", enumAsRef = true)
public enum DeleteStatus {

    @Schema(description = "正常")
    NORMAL(0),

    @Schema(description = "已删除")
    DELETED(1);

    private final int value;

    DeleteStatus(int value) { this.value = value; }

    public int getValue() { return value; }

    public static DeleteStatus from(Integer value) {
        if (value == null) return null;
        for (DeleteStatus s : values()) {
            if (s.value == value) return s;
        }
        return null;
    }
}

package cn.net.mall.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "敏感词类型", enumAsRef = true)
public enum SensitiveWordType {

    @Schema(description = "政治") POLITICAL(1),
    @Schema(description = "广告") ADVERTISING(2),
    @Schema(description = "色情") PORNOGRAPHY(3);

    private final int value;
    SensitiveWordType(int value) { this.value = value; }
    public int getValue() { return value; }
    public static SensitiveWordType from(Integer value) {
        if (value == null) return null;
        for (SensitiveWordType s : values()) { if (s.value == value) return s; }
        return null;
    }
}

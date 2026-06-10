package cn.net.mall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 有效状态枚举
 *
 * @date 2024/9/4 下午3:39
 */
@AllArgsConstructor
@Getter
public enum ValidStatusEnum {

    /**
     * 有效
     */
    VALID(1, "有效"),

    /**
     * 无效
     */
    INVALID(0, "无效");

    private Integer value;

    private String desc;
}

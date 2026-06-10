package cn.net.mall.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品评价类型枚举
 *
 * @date 2024/9/6 下午3:39
 */
@AllArgsConstructor
@Getter
public enum ProductCommentTypeEnum {

    /**
     * 好评
     */
    POSITIVE(1, "好评"),

    /**
     * 中评
     */
    MODERATE(2, "中评"),

    /**
     * 差评
     */
    NEGATIVE(3, "差评");

    private Integer value;

    private String desc;
}

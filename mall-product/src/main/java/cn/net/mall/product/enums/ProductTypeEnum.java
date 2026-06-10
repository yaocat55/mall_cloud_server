package cn.net.mall.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品类型枚举
 *
 * @date 2024/8/15 下午3:39
 */
@AllArgsConstructor
@Getter
public enum ProductTypeEnum {

    /**
     * 热门商品
     */
    HOT(1, "热门商品"),

    /**
     * 新品推荐
     */
    NEW(2, "新品推荐"),

    /**
     * 秒杀商品
     */
    SECKILL(3, "秒杀商品");

    private Integer value;

    private String desc;
}

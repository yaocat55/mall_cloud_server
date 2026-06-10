package cn.net.mall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 图片类型
 *
 * @date 2024/3/5 下午2:46
 */
@Getter
@AllArgsConstructor
public enum PhotoTypeEnum {

    /**
     * 封面
     */
    COVER(1, "封面"),

    /**
     * 轮播图
     */
    SWIPER(2, "轮播图");

    /**
     * 枚举值
     */
    private Integer value;


    /**
     * 枚举描述
     */
    private String desc;
}

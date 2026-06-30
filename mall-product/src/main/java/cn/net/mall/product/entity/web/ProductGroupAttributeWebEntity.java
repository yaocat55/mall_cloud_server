package cn.net.mall.product.entity.web;

import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品组属性web实体
 *
 * @date 2024/9/7 下午3:24
 */
@Data
@Schema(description = "商品分组属性")

public class ProductGroupAttributeWebEntity {

    /**
     * 属性
     */
    private Long id;

    /**
     * 属性名称
     */
    private String name;

    /**
     * 属性值集合
     */
    private List<ProductGroupAttributeValueWebEntity> valueList;

}

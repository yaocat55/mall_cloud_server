package cn.net.mall.product.dto;

import lombok.Data;

import java.util.List;

/**
 * 商品组属性web实体
 *
 * @date 2024/9/7 下午3:24
 */
@Data
public class ProductGroupAttributeDTO {

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
    private List<ProductGroupAttributeValueDTO> valueList;

}

package cn.net.mall.product.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品组属性值web实体
 *
 * @date 2024/9/7 下午3:24
 */
@Data
@Schema(description = "商品分组属性值")

public class ProductGroupAttributeValueDTO {

    /**
     * 系统ID
     */
    private Long id;

    /**
     * 属性值
     */
    private String value;

}

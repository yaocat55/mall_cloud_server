package cn.net.mall.product.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品属性实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-05-09 14:43:56
 */
@Schema(name = "商品属性实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductAttributeEntity extends BaseEntity {


    /**
     * 商品ID
     */
    @Schema(name = "商品ID")
    private Long productId;

    /**
     * 属性ID
     */
    @Schema(name = "属性ID")
    private Long attributeId;

    /**
     * 属性值ID
     */
    @Schema(name = "属性值ID")
    private Long attributeValueId;
}

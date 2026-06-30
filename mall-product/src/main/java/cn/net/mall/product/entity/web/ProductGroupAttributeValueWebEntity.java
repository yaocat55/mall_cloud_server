package cn.net.mall.product.entity.web;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品组属性值web实体
 *
 * @date 2024/9/7 下午3:24
 */
@Data
@Schema(description = "ProductGroupAttributeValue信息")

public class ProductGroupAttributeValueWebEntity {

    /**
     * 系统ID
     */
    @Schema(description = "系统ID")
    private Long id;

    /**
     * 属性值
     */
    @Schema(description = "值")
    private String value;

}

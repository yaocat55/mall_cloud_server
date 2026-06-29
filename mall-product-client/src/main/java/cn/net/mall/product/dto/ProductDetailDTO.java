package cn.net.mall.product.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品详情实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-07-07 15:14:11
 */
@Schema(description = "商品详情 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDetailDTO extends BaseEntity {

    /**
     * 商品ID
     */
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    /**
     * 商品详情
     */
    @Schema(description = "商品详情", example = "-")
    private String detail;
}

package cn.net.mall.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商品收藏实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-09-04 15:12:10
 */
@Data
public class ProductFavoritesDTO {
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 是否收藏
     */
    private Boolean isFavorites;
}

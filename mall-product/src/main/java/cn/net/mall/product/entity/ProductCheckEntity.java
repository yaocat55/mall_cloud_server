package cn.net.mall.product.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品检查实体
 *
 * @date 2024/5/10 上午11:43
 */
@Schema(description = "商品检查实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductCheckEntity {

    /**
     * 分类列表
     */
    @Schema(description = "category Entities")
    private List<CategoryEntity> categoryEntities;

    /**
     * 品牌列表
     */
    @Schema(description = "brand Entities")
    private List<BrandEntity> brandEntities;
}

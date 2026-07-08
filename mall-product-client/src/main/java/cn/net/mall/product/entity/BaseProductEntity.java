package cn.net.mall.product.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品基本信息
 *
 * @date 2024/9/8 下午1:37
 */
@Data
@Schema(description = "Product信息")

public class BaseProductEntity extends BaseEntity {

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "0")
    private Long categoryId;

    /**
     * 分类名称
     */
    @Schema(description = "category Name", example = "string")
    private String categoryName;

    /**
     * 单位ID
     */
    @Schema(description = "单位ID", example = "0")
    private Long unitId;

    /**
     * 单位名称
     */
    @Schema(description = "unit Name", example = "string")
    private String unitName;
}

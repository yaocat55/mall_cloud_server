package cn.net.mall.product.entity.web;

import cn.net.mall.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品查询条件实体
 *
 * @date 2024-05-09 14:43:56
 */
@Schema(description = "商品查询条件实体")
@Data
public class ProductConditionWebEntity extends RequestPageEntity {


    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "0")
    private Long categoryId;

    /**
     * 品牌ID
     */
    @Schema(description = "品牌ID", example = "0")
    private Long brandId;

    /**
     * 单位ID
     */
    @Schema(description = "单位ID", example = "0")
    private Long unitId;


    /**
     * 关键字
     */
    @Schema(description = "关键字", example = "-")
    private String keyword;

    /**
     * 类型
     */
    @Schema(description = "类型", example = "1")
    private Integer type;
}

package cn.net.mall.product.dto;

import cn.net.mall.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品查询条件实体
 *
 * @date 2024-05-09 14:43:56
 */
@Schema(name = "商品查询条件实体")
@Data
public class ProductSearchConditionDTO extends RequestPageEntity {


    /**
     * 分类ID
     */
    @Schema(name = "分类ID")
    private Long categoryId;

    /**
     * 品牌ID
     */
    @Schema(name = "品牌ID")
    private Long brandId;

    /**
     * 单位ID
     */
    @Schema(name = "单位ID")
    private Long unitId;


    /**
     * 关键字
     */
    @Schema(name = "关键字")
    private String keyword;

    /**
     * 类型
     */
    @Schema(name = "类型")
    private Integer type;
}

package cn.net.mall.product.dto;

import cn.net.mall.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品查询条件实体
 *
 * @date 2024-05-09 14:43:56
 */
@Schema(description = "商品搜索条件，支持关键字模糊搜索、分类/品牌精确筛选")
@Data
public class ProductSearchConditionDTO extends RequestPageEntity {


    /**
     * 分类ID
     */
    @Schema(description = "分类ID，精确匹配", example = "0")
    private Long categoryId;

    /**
     * 品牌ID
     */
    @Schema(description = "品牌ID，精确匹配", example = "0")
    private Long brandId;

    /**
     * 单位ID
     */
    @Schema(description = "单位ID，精确匹配", example = "0")
    private Long unitId;


    /**
     * 搜索关键字，模糊匹配
     */
    @Schema(description = "搜索关键字，模糊匹配", example = "-")
    private String keyword;

    /**
     * 排序类型：1-综合 2-价格 3-销量
     */
    @Schema(description = "排序类型：1-综合 2-价格 3-销量", example = "1")
    private Integer type;
}

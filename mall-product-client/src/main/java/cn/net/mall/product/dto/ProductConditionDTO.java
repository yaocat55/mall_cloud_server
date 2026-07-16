package cn.net.mall.product.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品查询条件 DTO
 *
 * 用于 mall-admin-bff 通过 Feign 调用分页查询商品列表
 */
@Schema(description = "商品查询条件 DTO")
@Data
public class ProductConditionDTO extends RequestConditionEntity {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "ID集合")
    private List<Long> idList;

    @Schema(description = "分类ID", example = "0")
    private Long categoryId;

    @Schema(description = "品牌ID", example = "0")
    private Long brandId;

    @Schema(description = "单位ID", example = "0")
    private Long unitId;

    @Schema(description = "商品名称", example = "测试数据")
    private String name;

    @Schema(description = "规格", example = "型号")
    private String model;

    @Schema(description = "hash值")
    private String hash;

    @Schema(description = "数量", example = "10")
    private Integer quantity;

    @Schema(description = "价格", example = "99.99")
    private BigDecimal price;

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "商品组ID", example = "0")
    private Long productGroupId;

    @Schema(description = "商品组ID集合")
    private List<Long> productGroupIdList;
}

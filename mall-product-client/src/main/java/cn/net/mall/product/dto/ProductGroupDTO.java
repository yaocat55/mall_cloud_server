package cn.net.mall.product.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品组 DTO
 *
 * @date 2025/07/15
 */
@Schema(description = "商品组DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductGroupDTO extends BaseEntity {

    @Schema(description = "商品组名称")
    private String name;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "单位ID")
    private Long unitId;

    @Schema(description = "单位名称")
    private String unitName;

    @Schema(description = "规格")
    private String model;

    @Schema(description = "hash")
    private String hash;

    @Schema(description = "是否新创建的商品组")
    private Boolean isNew;

    @Schema(description = "逻辑删除ID")
    private Long delId;
}

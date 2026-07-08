package cn.net.mall.product.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "商品浏览记录查询条件DTO")
@Data
public class ProductViewRecordConditionDTO extends RequestConditionEntity {
    /**
     * ID
     */
    @Schema(description = "系统ID", example = "0")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "0")
    private Long userId;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID", example = "0")
    private Long productId;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(description = "是否删除 0:未删除 1:已删除", example = "0")
    private Integer isDel;
}

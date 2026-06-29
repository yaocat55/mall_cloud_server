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
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    private Integer isDel;
}

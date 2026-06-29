package cn.net.mall.product.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "商品浏览记录DTO")
@Data
public class ProductViewRecordDTO extends BaseEntity {
    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    /**
     * 访问次数
     */
    @Schema(description = "访问次数", example = "0")
    private int viewCount;
}

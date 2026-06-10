package cn.net.mall.product.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "商品浏览记录DTO")
@Data
public class ProductViewRecordDTO extends BaseEntity {
    /**
     * 用户ID
     */
    @Schema(name = "用户ID")
    private Long userId;

    /**
     * 商品ID
     */
    @Schema(name = "商品ID")
    private Long productId;

    /**
     * 访问次数
     */
    @Schema(name = "访问次数")
    private int viewCount;
}

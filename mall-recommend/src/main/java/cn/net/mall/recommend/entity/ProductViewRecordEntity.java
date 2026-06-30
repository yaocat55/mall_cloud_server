package cn.net.mall.recommend.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "ProductViewRecord信息")

public class ProductViewRecordEntity extends BaseEntity {
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "商品ID")
    private Long productId;
    @Schema(description = "view Count")
    private Integer viewCount;
}

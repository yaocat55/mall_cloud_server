package cn.net.mall.recommend.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "商品收藏")

public class ProductFavoritesEntity extends BaseEntity {
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "商品ID")
    private Long productId;
}

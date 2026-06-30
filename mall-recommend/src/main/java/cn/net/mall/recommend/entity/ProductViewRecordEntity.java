package cn.net.mall.recommend.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "ProductViewRecord信息")

public class ProductViewRecordEntity extends BaseEntity {
    private Long userId;
    private Long productId;
    private Integer viewCount;
}

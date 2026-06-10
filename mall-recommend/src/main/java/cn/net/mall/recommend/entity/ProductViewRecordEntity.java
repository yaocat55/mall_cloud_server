package cn.net.mall.recommend.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.Data;

@Data
public class ProductViewRecordEntity extends BaseEntity {
    private Long userId;
    private Long productId;
    private Integer viewCount;
}

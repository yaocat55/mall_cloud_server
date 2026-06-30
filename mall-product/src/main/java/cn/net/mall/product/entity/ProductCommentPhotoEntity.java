package cn.net.mall.product.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "商品评论图片")

public class ProductCommentPhotoEntity extends BaseEntity {
    private Long commentId;
    private Long productId;
    private String url;
    private Integer sort;
}

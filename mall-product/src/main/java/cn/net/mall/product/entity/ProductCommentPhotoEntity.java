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
    @Schema(description = "comment Id")
    private Long commentId;
    @Schema(description = "商品ID")
    private Long productId;
    @Schema(description = "url")
    private String url;
    @Schema(description = "排序")
    private Integer sort;
}

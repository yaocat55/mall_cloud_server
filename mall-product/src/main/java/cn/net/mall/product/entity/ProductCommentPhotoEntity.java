package cn.net.mall.product.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductCommentPhotoEntity extends BaseEntity {
    private Long commentId;
    private Long productId;
    private String url;
    private Integer sort;
}

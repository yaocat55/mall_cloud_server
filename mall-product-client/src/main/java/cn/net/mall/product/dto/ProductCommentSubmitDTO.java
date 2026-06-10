package cn.net.mall.product.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;

import java.util.List;

@Data
public class ProductCommentSubmitDTO {
    private Long productId;
    private Long parentId;
    private Integer rating;
    private Integer type;
    @ValidSensitiveWordField
    private String content;
    private List<String> photos;
}

package cn.net.mall.product.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "商品评论提交")

public class ProductCommentSubmitDTO {
    private Long productId;
    private Long parentId;
    private Integer rating;
    private Integer type;
    @ValidSensitiveWordField
    private String content;
    private List<String> photos;
}

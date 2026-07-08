package cn.net.mall.product.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "商品评论提交", example = "0")

public class ProductCommentSubmitDTO {
    @Schema(description = "商品ID", example = "0")
    private Long productId;
    @Schema(description = "上级ID", example = "0")
    private Long parentId;
    @Schema(description = "评分", example = "0")
    private Integer rating;
    @Schema(description = "类型", example = "0")
    private Integer type;
    @ValidSensitiveWordField
    @Schema(description = "内容", example = "string")
    private String content;
    @Schema(description = "photos", example = "string")
    private List<String> photos;
}

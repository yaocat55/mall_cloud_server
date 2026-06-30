package cn.net.mall.product.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "商品评论提交")

public class ProductCommentSubmitDTO {
    @Schema(description = "商品ID")
    private Long productId;
    @Schema(description = "上级ID")
    private Long parentId;
    @Schema(description = "评分")
    private Integer rating;
    @Schema(description = "类型")
    private Integer type;
    @ValidSensitiveWordField
    @Schema(description = "内容")
    private String content;
    @Schema(description = "photos")
    private List<String> photos;
}

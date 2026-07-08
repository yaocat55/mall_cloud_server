package cn.net.mall.order.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单评价商品项", example = "string")

public class OrderEvaluateProductItemDTO {
    @Schema(description = "商品ID", example = "string")
    private String productId;
    @Schema(description = "评分", example = "0")
    private Integer rating;
    @ValidSensitiveWordField
    @Schema(description = "内容", example = "string")
    private String content;
    @Schema(description = "图片URL列表", example = "string")
    private List<String> images;
    @Schema(description = "是否匿名", example = "true")
    private Boolean isAnonymous;
}

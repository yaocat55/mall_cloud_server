package cn.net.mall.order.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单评价商品项")

public class OrderEvaluateProductItemDTO {
    @Schema(description = "商品ID")
    private String productId;
    @Schema(description = "评分")
    private Integer rating;
    @ValidSensitiveWordField
    @Schema(description = "内容")
    private String content;
    @Schema(description = "图片URL列表")
    private List<String> images;
    @Schema(description = "是否匿名")
    private Boolean isAnonymous;
}

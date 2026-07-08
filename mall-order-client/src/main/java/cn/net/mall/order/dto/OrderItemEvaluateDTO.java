package cn.net.mall.order.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单项评价", example = "0")

public class OrderItemEvaluateDTO {
    @Schema(description = "order Item Id", example = "0")
    private Long orderItemId;
    @Schema(description = "商品ID", example = "0")
    private Long productId;
    @Schema(description = "评分", example = "0")
    private Integer rating;
    @Schema(description = "类型", example = "0")
    private Integer type;
    @ValidSensitiveWordField
    @Schema(description = "内容", example = "string")
    private String content;
}

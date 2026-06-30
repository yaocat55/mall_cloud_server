package cn.net.mall.order.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单项评价")

public class OrderItemEvaluateDTO {
    @Schema(description = "order Item Id")
    private Long orderItemId;
    @Schema(description = "商品ID")
    private Long productId;
    @Schema(description = "评分")
    private Integer rating;
    @Schema(description = "类型")
    private Integer type;
    @ValidSensitiveWordField
    @Schema(description = "内容")
    private String content;
}

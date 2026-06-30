package cn.net.mall.order.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单项评价")

public class OrderItemEvaluateDTO {
    private Long orderItemId;
    private Long productId;
    private Integer rating;
    private Integer type;
    @ValidSensitiveWordField
    private String content;
}

package cn.net.mall.order.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;

@Data
public class OrderItemEvaluateDTO {
    private Long orderItemId;
    private Long productId;
    private Integer rating;
    private Integer type;
    @ValidSensitiveWordField
    private String content;
}

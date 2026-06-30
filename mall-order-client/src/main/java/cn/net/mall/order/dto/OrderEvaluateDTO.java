package cn.net.mall.order.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单评价请求")

public class OrderEvaluateDTO {
    private String tradeCode;
    @NotEmpty(message = "商品评价列表不能为空")
    private List<OrderEvaluateProductItemDTO> productCommentList;
    private Integer logisticsScore;
    private Integer serviceScore;
    private List<OrderItemEvaluateDTO> items;
}

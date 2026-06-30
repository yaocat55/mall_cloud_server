package cn.net.mall.order.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单评价请求")

public class OrderEvaluateDTO {
    @Schema(description = "交易编号")
    private String tradeCode;
    @NotEmpty(message = "商品评价列表不能为空")
    @Schema(description = "商品评论列表")
    private List<OrderEvaluateProductItemDTO> productCommentList;
    @Schema(description = "物流评分")
    private Integer logisticsScore;
    @Schema(description = "服务评分")
    private Integer serviceScore;
    @Schema(description = "items")
    private List<OrderItemEvaluateDTO> items;
}

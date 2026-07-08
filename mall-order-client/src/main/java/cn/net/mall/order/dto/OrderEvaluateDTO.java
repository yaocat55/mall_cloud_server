package cn.net.mall.order.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单评价请求", example = "string")

public class OrderEvaluateDTO {
    @Schema(description = "交易编号", example = "string")
    private String tradeCode;
    @NotEmpty(message = "商品评价列表不能为空")
    @Schema(description = "商品评论列表", example = "string")
    private List<OrderEvaluateProductItemDTO> productCommentList;
    @Schema(description = "物流评分", example = "0")
    private Integer logisticsScore;
    @Schema(description = "服务评分", example = "0")
    private Integer serviceScore;
    @Schema(description = "items", example = "string")
    private List<OrderItemEvaluateDTO> items;
}

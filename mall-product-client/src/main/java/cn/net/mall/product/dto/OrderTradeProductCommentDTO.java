package cn.net.mall.product.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单商品评价")

public class OrderTradeProductCommentDTO {
    @Schema(description = "交易编号")
    private String tradeCode;
    @Schema(description = "订单ID")
    private Long orderId;
    @NotEmpty(message = "商品评价列表不能为空")
    @Schema(description = "商品评论列表")
    private List<ProductCommentSubmitDTO> productCommentList;
}

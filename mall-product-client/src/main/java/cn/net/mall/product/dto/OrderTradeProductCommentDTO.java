package cn.net.mall.product.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单商品评价", example = "string")

public class OrderTradeProductCommentDTO {
    @Schema(description = "交易编号", example = "string")
    private String tradeCode;
    @Schema(description = "订单ID", example = "0")
    private Long orderId;
    @NotEmpty(message = "商品评价列表不能为空")
    @Schema(description = "商品评论列表", example = "string")
    private List<ProductCommentSubmitDTO> productCommentList;
}

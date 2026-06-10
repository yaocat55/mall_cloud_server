package cn.net.mall.product.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderTradeProductCommentDTO {
    private String tradeCode;
    private Long orderId;
    @NotEmpty(message = "商品评价列表不能为空")
    private List<ProductCommentSubmitDTO> productCommentList;
}

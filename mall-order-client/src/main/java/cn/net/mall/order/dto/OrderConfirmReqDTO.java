package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "订单确认请求DTO")
public class OrderConfirmReqDTO {
    @Schema(description = "购物车ID集合")
    private List<OrderConfirmReqItemDTO> items;
    
    @Schema(description = "选择的优惠券ID集合（订单级）", example = "0")
    private List<Long> couponIds;

    @Data
    static public class OrderConfirmReqItemDTO {
        @Schema(description = "购物车ID", example = "0")
        private Long shoppingCartId;
        @Schema(description = "优惠券ID", example = "1")
        private Long couponId;
    }
}

package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "订单确认请求DTO")
public class OrderConfirmReqDTO {
    @Schema(name = "购物车ID集合")
    private List<OrderConfirmReqItemDTO> items;
    
    @Schema(name = "选择的优惠券ID集合（订单级）")
    private List<Long> couponIds;

    @Data
    static public class OrderConfirmReqItemDTO {
        @Schema(name = "购物车ID")
        private Long shoppingCartId;
        @Schema(name = "优惠券ID")
        private Long couponId;
    }
}

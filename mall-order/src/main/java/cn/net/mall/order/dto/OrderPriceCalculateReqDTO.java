package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "订单价格计算请求DTO")
public class OrderPriceCalculateReqDTO {

    @Schema(description = "商品列表")
    private List<Item> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        @Schema(description = "商品原价", example = "99.99")
        private BigDecimal price;
        
        @Schema(description = "使用的优惠券ID", example = "1")
        private Long couponId;
    }
}

package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(name = "订单价格计算请求DTO")
public class OrderPriceCalculateReqDTO {

    @Schema(name = "商品列表")
    private List<Item> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        @Schema(name = "商品原价")
        private BigDecimal price;
        
        @Schema(name = "使用的优惠券ID")
        private Long couponId;
    }
}

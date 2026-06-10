package cn.net.mall.marketing.entity.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(name = "订单价格计算请求")
public class OrderPriceCalculateReq {

    @Schema(name = "商品列表")
    private List<Item> items;

    @Data
    public static class Item {
        @Schema(name = "商品原价")
        private BigDecimal price;
        
        @Schema(name = "使用的优惠券ID")
        private Long couponId;
    }
}

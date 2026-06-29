package cn.net.mall.marketing.entity.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "订单价格计算请求")
public class OrderPriceCalculateReq {

    @Schema(description = "商品列表")
    private List<Item> items;

    @Data
    public static class Item {
        @Schema(description = "商品原价", example = "99.99")
        private BigDecimal price;
        
        @Schema(description = "使用的优惠券ID", example = "1")
        private Long couponId;
    }
}

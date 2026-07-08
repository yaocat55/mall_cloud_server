package cn.net.mall.marketing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "订单价格计算请求DTO", example = "string")
public class OrderPriceCalculateReqDTO {

    @Schema(description = "商品列表", example = "string")
    private List<Item> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        @Schema(description = "skuID", example = "0")
        private Long skuId;

        @Schema(description = "商品ID", example = "1")
        private Long productId;

        @Schema(description = "商品原价", example = "99.99")
        private BigDecimal price;

        @Schema(description = "商品数量", example = "0")
        private Integer count;
        
        @Schema(description = "使用的优惠券ID", example = "1")
        private Long couponId;
    }
}

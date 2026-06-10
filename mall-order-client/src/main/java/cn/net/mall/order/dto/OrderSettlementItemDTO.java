package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单结算项请求参数
 */
@Data
@Schema(description = "订单结算项请求参数")
public class OrderSettlementItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "购物车ID")
    private Long shoppingCartId;

    @Schema(description = "优惠券ID")
    private Long couponId;
}

package cn.net.mall.order.dto;

import cn.net.mall.product.dto.ShoppingCartBuyDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单结算响应对象
 */
@Data
@Schema(description = "订单结算响应对象")
public class OrderSettlementVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "收货地址")
    private OrderDeliveryAddressDTO deliveryAddress;

    @Schema(description = "订单明细")
    private List<OrderItemDTO> orderItems;

    @Schema(description = "订单优惠券信息")
    private OrderCouponDTO orderCoupon;

    @Schema(description = "结算项集合")
    private List<OrderSettlementItemDTO> items;

    @Schema(description = "总金额")
    private BigDecimal totalMoney;

    @Schema(description = "最终支付金额")
    private BigDecimal finalMoney;

    @Schema(description = "优惠金额")
    private BigDecimal subtractMoney;

    @Schema(description = "商品件数")
    private Integer totalCount;

    @Schema(description = "结算确认Token/Code")
    private String tradeCode;
}

package cn.net.mall.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 购物车实体
 *
 * @date 2024/9/11 下午4:29
 */
@Data
@Schema(description = "购物车结算")

public class ShoppingCartBuyDTO {

    /**
     * 购物车中的优惠券商品列表
     */
    @Schema(description = "coupon Group Product Web Entity List")
    private List<CouponGroupProductDTO> couponGroupProductWebEntityList;

    /**
     * 总金额
     */
    @Schema(description = "total Money")
    private BigDecimal totalMoney = BigDecimal.ZERO;

    /**
     * 最终支付金额
     */
    private BigDecimal finalMoney = BigDecimal.ZERO;

    /**
     * 优惠金额
     */
    @Schema(description = "优惠金额")
    private BigDecimal subtractMoney = BigDecimal.ZERO;
}

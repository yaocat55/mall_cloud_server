package cn.net.mall.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车实体
 *
 * @date 2024/9/11 下午4:29
 */
@Data
public class ShoppingCartBuyDTO {

    /**
     * 购物车中的优惠券商品列表
     */
    private List<CouponGroupProductDTO> couponGroupProductWebEntityList;

    /**
     * 总金额
     */
    private BigDecimal totalMoney = BigDecimal.ZERO;

    /**
     * 最终支付金额
     */
    private BigDecimal finalMoney = BigDecimal.ZERO;

    /**
     * 优惠金额
     */
    private BigDecimal subtractMoney = BigDecimal.ZERO;
}

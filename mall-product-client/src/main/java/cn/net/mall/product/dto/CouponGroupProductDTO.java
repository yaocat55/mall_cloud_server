package cn.net.mall.product.dto;

import lombok.Data;

import java.util.List;

/**
 * 优惠券商品组
 *
 * @date 2024/9/18 下午6:16
 */
@Data
public class CouponGroupProductDTO {

    /**
     * 使用优惠券
     */
    private CouponDTO couponWebEntity;

    /**
     * 购物车中的商品列表
     */
    private List<ShoppingCartProductDTO> shoppingCartList;


}

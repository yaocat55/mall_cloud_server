package cn.net.mall.product.entity.shopping.web;

import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 优惠券商品组
 *
 * @date 2024/9/18 下午6:16
 */
@Data
@Schema(description = "优惠券分组商品")

public class CouponGroupProductWebEntity {

    /**
     * 使用优惠券
     */
    private CouponWebEntity couponWebEntity;

    /**
     * 购物车中的商品列表
     */
    private List<ShoppingCartProductWebEntity> shoppingCartList;


}

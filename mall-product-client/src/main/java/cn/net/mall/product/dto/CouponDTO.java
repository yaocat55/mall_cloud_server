package cn.net.mall.product.dto;

import lombok.Data;

/**
 * 优惠券Web实体 (Mock for ShoppingCart)
 */
@Data
public class CouponDTO {
    private Long id;
    private Integer type;
    private Integer minProductCount;
    // 添加其他可能需要的字段，根据 ShoppingCartService 的使用情况
}

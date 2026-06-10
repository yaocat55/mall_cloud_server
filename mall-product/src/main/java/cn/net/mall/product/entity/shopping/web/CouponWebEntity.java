package cn.net.mall.product.entity.shopping.web;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 优惠券Web实体 (Mock for ShoppingCart)
 */
@Data
public class CouponWebEntity {
    private Long id;
    private Integer type;
    private Integer minProductCount;
    // 添加其他可能需要的字段，根据 ShoppingCartService 的使用情况
}

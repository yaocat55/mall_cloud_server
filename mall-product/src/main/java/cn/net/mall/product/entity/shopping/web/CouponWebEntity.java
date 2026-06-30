package cn.net.mall.product.entity.shopping.web;

import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 优惠券Web实体 (Mock for ShoppingCart)
 */
@Data
@Schema(description = "优惠券")

public class CouponWebEntity {
    @Schema(description = "系统ID")
    private Long id;
    @Schema(description = "类型")
    private Integer type;
    @Schema(description = "min Product Count")
    private Integer minProductCount;
    // 添加其他可能需要的字段，根据 ShoppingCartService 的使用情况
}

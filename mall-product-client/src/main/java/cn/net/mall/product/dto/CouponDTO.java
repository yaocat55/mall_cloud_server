package cn.net.mall.product.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 优惠券Web实体 (Mock for ShoppingCart)
 */
@Data
@Schema(description = "优惠券信息", example = "0")

public class CouponDTO {
    @Schema(description = "系统ID", example = "0")
    private Long id;
    @Schema(description = "类型", example = "0")
    private Integer type;
    @Schema(description = "min Product Count", example = "0")
    private Integer minProductCount;
    // 添加其他可能需要的字段，根据 ShoppingCartService 的使用情况
}

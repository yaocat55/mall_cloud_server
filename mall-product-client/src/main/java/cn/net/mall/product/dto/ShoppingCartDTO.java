package cn.net.mall.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 购物车实体
 *
 * @date 2024-08-30 18:03:40
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShoppingCartDTO extends UserProductDTO {

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    private Integer quantity;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;
}

package cn.net.mall.product.entity.shopping;

import cn.net.mall.product.entity.UserProductEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import cn.net.mall.entity.BaseEntity;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 购物车实体
 *
 * @date 2024-08-30 18:03:40
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShoppingCartEntity extends UserProductEntity {

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

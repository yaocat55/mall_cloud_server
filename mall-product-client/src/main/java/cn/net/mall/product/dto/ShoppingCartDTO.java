package cn.net.mall.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "购物车商品 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShoppingCartDTO extends UserProductDTO {

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Schema(description = "商品数量", example = "1")
    private Integer quantity;

    /**
     * 总金额
     */
    @Schema(description = "总金额，单位：分", example = "9999")
    private BigDecimal totalAmount;
}

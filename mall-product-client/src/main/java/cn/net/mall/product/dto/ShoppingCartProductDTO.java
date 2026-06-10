package cn.net.mall.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 购物车实体
 *
 * @date 2024-08-30 18:03:40
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShoppingCartProductDTO {

    /**
     * 唯一标识
     */
    private String uuid;

    /**
     * 系统ID
     */
    private Long id;

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    private String productName;

    /**
     * 规格
     */
    @Schema(description = "规格")
    private String model;

    /**
     * 价格
     */
    @Schema(description = "价格")
    private BigDecimal price;

    /**
     * 到手价
     */
    @Schema(description = "到手价")
    private BigDecimal payPrice;

    /**
     * 封面
     */
    @Schema(description = "封面")
    private String cover;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    private Integer quantity;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否选中
     */
    private boolean checked;
}

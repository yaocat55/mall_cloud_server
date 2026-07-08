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
    @Schema(description = "唯一标识", example = "string")
    private String uuid;

    /**
     * 系统ID
     */
    @Schema(description = "系统ID", example = "0")
    private Long id;

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", example = "0")
    private Long productId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "0")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称", example = "admin")
    private String userName;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称", example = "-")
    private String productName;

    /**
     * 规格
     */
    @Schema(description = "规格", example = "型号")
    private String model;

    /**
     * 价格
     */
    @Schema(description = "价格", example = "99.99")
    private BigDecimal price;

    /**
     * 到手价
     */
    @Schema(description = "到手价", example = "0")
    private BigDecimal payPrice;

    /**
     * 封面
     */
    @Schema(description = "封面", example = "-")
    private String cover;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Schema(description = "数量", example = "0")
    private Integer quantity;

    /**
     * 库存
     */
    @Schema(description = "库存", example = "0")
    private Integer stock;

    /**
     * 总金额
     */
    @Schema(description = "总金额", example = "0.00")
    private BigDecimal totalAmount;

    /**
     * 支付金额
     */
    @Schema(description = "支付金额", example = "0.00")
    private BigDecimal payAmount;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-01")
    private Date createTime;

    /**
     * 是否选中
     */
    @Schema(description = "checked", example = "true")
    private boolean checked;
}

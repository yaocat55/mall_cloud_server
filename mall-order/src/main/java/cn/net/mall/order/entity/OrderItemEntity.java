package cn.net.mall.order.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单明细实体
 */
@Schema(description = "订单明细实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderItemEntity extends BaseEntity {

    /**
     * 订单ID
     */
    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    /**
     * 订单编码
     */
    @Schema(description = "订单编码", example = "-")
    private String orderCode;

    /**
     * 订单编码hash
     */
    @Schema(description = "订单编码hash", example = "0")
    private Integer codeHash;

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称", example = "-")
    private String productName;

    /**
     * 商品规格
     */
    @Schema(description = "商品规格", example = "型号")
    private String model;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @Schema(description = "单价", example = "99.99")
    private BigDecimal price;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Schema(description = "数量", example = "10")
    private Integer quantity;

    /**
     * 金额
     */
    @NotNull(message = "金额不能为空")
    @Schema(description = "金额", example = "99.99")
    private BigDecimal amount;

    /**
     * 封面图片url
     */
    @Schema(description = "封面图片url", example = "https://example.com/cover.png")
    private String coverUrl;
}

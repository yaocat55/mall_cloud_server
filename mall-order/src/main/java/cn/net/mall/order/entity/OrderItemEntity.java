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
@Schema(name = "订单明细实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderItemEntity extends BaseEntity {

    /**
     * 订单ID
     */
    @Schema(name = "订单ID")
    private Long orderId;

    /**
     * 订单编码
     */
    @Schema(name = "订单编码")
    private String orderCode;

    /**
     * 订单编码hash
     */
    @Schema(name = "订单编码hash")
    private Integer codeHash;

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    @Schema(name = "商品ID")
    private Long productId;

    /**
     * 商品名称
     */
    @Schema(name = "商品名称")
    private String productName;

    /**
     * 商品规格
     */
    @Schema(name = "商品规格")
    private String model;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @Schema(name = "单价")
    private BigDecimal price;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Schema(name = "数量")
    private Integer quantity;

    /**
     * 金额
     */
    @NotNull(message = "金额不能为空")
    @Schema(name = "金额")
    private BigDecimal amount;

    /**
     * 封面图片url
     */
    @Schema(name = "封面图片url")
    private String coverUrl;
}

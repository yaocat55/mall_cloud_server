package cn.net.mall.order.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "订单明细传输对象")
public class OrderItemDTO extends BaseEntity {
    @Schema(description = "订单ID")
    private Long orderId;
    @Schema(description = "商品ID")
    private Long productId;
    @Schema(description = "商品名称")
    private String productName;
    @Schema(description = "商品图片")
    private String productPicture;
    @Schema(description = "商品价格")
    private BigDecimal productPrice;
    @Schema(description = "购买数量")
    private Integer productQuantity;
    @Schema(description = "商品属性")
    private String productAttribute;
    @Schema(description = "到手价")
    private BigDecimal payPrice;
    @Schema(description = "金额")
    private BigDecimal amount;
    @Schema(description = "支付金额")
    private BigDecimal payAmount;
}

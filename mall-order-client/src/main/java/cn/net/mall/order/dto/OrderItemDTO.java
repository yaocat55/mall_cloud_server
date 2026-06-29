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
    @Schema(description = "订单ID", example = "1")
    private Long orderId;
    @Schema(description = "商品ID", example = "1")
    private Long productId;
    @Schema(description = "商品名称", example = "-")
    private String productName;
    @Schema(description = "商品图片", example = "-")
    private String productPicture;
    @Schema(description = "商品价格", example = "0")
    private BigDecimal productPrice;
    @Schema(description = "购买数量", example = "0")
    private Integer productQuantity;
    @Schema(description = "商品属性", example = "-")
    private String productAttribute;
    @Schema(description = "到手价", example = "0")
    private BigDecimal payPrice;
    @Schema(description = "金额", example = "99.99")
    private BigDecimal amount;
    @Schema(description = "支付金额", example = "0")
    private BigDecimal payAmount;
}

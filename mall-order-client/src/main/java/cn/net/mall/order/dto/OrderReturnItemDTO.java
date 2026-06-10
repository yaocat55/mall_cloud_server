package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "退货明细DTO")
public class OrderReturnItemDTO {
    @Schema(description = "订单ID")
    private Long orderId;
    @Schema(description = "订单明细ID")
    private Long orderItemId;
    @Schema(description = "退货申请ID")
    private Long returnApplyId;
    @Schema(description = "商品ID")
    private Long productId;
    @Schema(description = "商品名称")
    private String productName;
    @Schema(description = "商品规格")
    private String productModel;
    @Schema(description = "商品单价")
    private BigDecimal productPrice;
    @Schema(description = "退货数量")
    private Integer quantity;
    @Schema(description = "商品数量(前端展示字段)")
    private Integer productQuantity;
    @Schema(description = "金额")
    private BigDecimal amount;
    @Schema(description = "商品图片")
    private String productPicture;
}

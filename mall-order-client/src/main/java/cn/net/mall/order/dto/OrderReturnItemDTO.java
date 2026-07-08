package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "退货明细DTO", example = "0")
public class OrderReturnItemDTO {
    @Schema(description = "订单ID", example = "1")
    private Long orderId;
    @Schema(description = "订单明细ID", example = "0")
    private Long orderItemId;
    @Schema(description = "退货申请ID", example = "0")
    private Long returnApplyId;
    @Schema(description = "商品ID", example = "1")
    private Long productId;
    @Schema(description = "商品名称", example = "-")
    private String productName;
    @Schema(description = "商品规格", example = "-")
    private String productModel;
    @Schema(description = "商品单价", example = "0")
    private BigDecimal productPrice;
    @Schema(description = "退货数量", example = "10")
    private Integer quantity;
    @Schema(description = "商品数量(前端展示字段)", example = "0")
    private Integer productQuantity;
    @Schema(description = "金额", example = "99.99")
    private BigDecimal amount;
    @Schema(description = "商品图片", example = "-")
    private String productPicture;
}

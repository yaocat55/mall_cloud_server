package cn.net.mall.order.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "退货明细实体")
public class OrderReturnItemEntity extends BaseEntity {

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

    @Schema(description = "金额")
    private BigDecimal amount;
}


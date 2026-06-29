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

    @Schema(description = "金额", example = "99.99")
    private BigDecimal amount;
}


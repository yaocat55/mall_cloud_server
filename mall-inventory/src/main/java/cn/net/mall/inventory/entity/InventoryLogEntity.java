package cn.net.mall.inventory.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存流水实体.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "库存流水实体")
public class InventoryLogEntity extends BaseEntity {

    @Schema(description = "商品/SKU ID")
    private Long productId;

    @Schema(description = "批次ID")
    private Long batchId;

    @Schema(description = "流水类型：FREEZE/CONFIRM/UNFREEZE/RETURN/INBOUND")
    private String type;

    @Schema(description = "变动数量")
    private Integer quantity;

    @Schema(description = "变动前值")
    private Integer beforeVal;

    @Schema(description = "变动后值")
    private Integer afterVal;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "发货单ID")
    private Long shipmentId;

    @Schema(description = "备注")
    private String remark;
}

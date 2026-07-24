package cn.net.mall.inventory.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存批次查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "库存批次查询条件")
public class InventoryBatchConditionEntity extends RequestConditionEntity {

    @Schema(description = "商品/SKU ID")
    private Long productId;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "状态 1:正常 2:已完成 3:已过期")
    private Integer status;
}

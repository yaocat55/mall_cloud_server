package cn.net.mall.inventory.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存批次实体.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "库存批次实体")
public class InventoryBatchEntity extends BaseEntity {

    @Schema(description = "商品/SKU ID")
    private Long productId;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "入库数量")
    private Integer quantity;

    @Schema(description = "可用数量")
    private Integer available;

    @Schema(description = "供应商")
    private String supplier;

    @Schema(description = "采购单价")
    private BigDecimal purchasePrice;

    @Schema(description = "仓库/库位")
    private String warehouse;

    @Schema(description = "入库时间")
    private Date inboundTime;

    @Schema(description = "过期时间")
    private Date expireTime;

    @Schema(description = "状态 1:正常 2:已完成 3:已过期")
    private Integer status;
}

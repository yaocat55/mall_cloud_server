package cn.net.mall.inventory.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存实体.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "库存实体")
public class InventoryEntity extends BaseEntity {

    @Schema(description = "商品/SKU ID")
    private Long productId;

    @Schema(description = "总入库量")
    private Integer quantity;

    @Schema(description = "冻结库存")
    private Integer frozen;

    @Schema(description = "可用库存")
    private Integer available;

    @Schema(description = "已售数量")
    private Integer saleCount;

    @Schema(description = "乐观锁版本号")
    private Integer version;
}

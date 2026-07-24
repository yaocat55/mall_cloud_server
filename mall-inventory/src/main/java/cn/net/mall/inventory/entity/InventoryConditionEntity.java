package cn.net.mall.inventory.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "库存查询条件")
public class InventoryConditionEntity extends RequestConditionEntity {

    @Schema(description = "商品/SKU ID")
    private Long productId;

    @Schema(description = "商品名称（模糊）")
    private String productName;
}

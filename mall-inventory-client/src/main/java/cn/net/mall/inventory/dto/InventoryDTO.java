package cn.net.mall.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 库存 DTO.
 */
@Data
@Schema(description = "库存信息")
public class InventoryDTO {
    @Schema(description = "商品/SKU ID", example = "1")
    private Long productId;

    @Schema(description = "总入库量", example = "1000")
    private Integer quantity;

    @Schema(description = "冻结库存", example = "10")
    private Integer frozen;

    @Schema(description = "可用库存", example = "990")
    private Integer available;

    @Schema(description = "已售数量", example = "500")
    private Integer saleCount;
}

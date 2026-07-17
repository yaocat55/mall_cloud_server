package cn.net.mall.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 入库请求.
 */
@Data
@Schema(description = "入库请求")
public class InventoryInboundDTO {

    @NotNull
    @Schema(description = "商品/SKU ID", example = "1")
    private Long productId;

    @Min(1)
    @Schema(description = "入库数量", example = "500")
    private Integer quantity;

    @NotBlank
    @Schema(description = "批次号", example = "PO20260717-001")
    private String batchNo;

    @Schema(description = "供应商", example = "富士康")
    private String supplier;

    @Schema(description = "采购单价", example = "3999.00")
    private BigDecimal purchasePrice;

    @Schema(description = "仓库/库位", example = "A区-01")
    private String warehouse;
}

package cn.net.mall.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 回库请求（售后退货时调用）.
 */
@Data
@Schema(description = "回库请求（售后退货时调用）")
public class InventoryReturnDTO {

    @NotNull
    @Schema(description = "商品/SKU ID", example = "1")
    private Long productId;

    @Min(1)
    @Schema(description = "回库数量", example = "1")
    private Integer quantity;

    @Schema(description = "订单ID", example = "20260717001")
    private Long orderId;

    @Schema(description = "备注", example = "退货回库")
    private String remark;
}

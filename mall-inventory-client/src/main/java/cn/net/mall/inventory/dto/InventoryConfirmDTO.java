package cn.net.mall.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 确认扣减请求（支付成功时调用）.
 */
@Data
@Schema(description = "确认扣减请求（支付回调时调用）")
public class InventoryConfirmDTO {

    @NotNull
    @Schema(description = "商品/SKU ID", example = "1")
    private Long productId;

    @Min(1)
    @Schema(description = "数量", example = "1")
    private Integer quantity;

    @Schema(description = "订单ID", example = "20260717001")
    private Long orderId;
}

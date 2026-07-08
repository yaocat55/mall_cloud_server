package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "订单明细查询请求DTO", example = "string")
public class TradeItemReqDTO {
    @Schema(description = "订单编码", example = "TC202401010001")
    private String tradeCode;
    @Schema(description = "订单明细ID", example = "0")
    private Long itemId;
}

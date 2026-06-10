package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "订单明细查询请求DTO")
public class TradeItemReqDTO {
    @Schema(description = "订单编码")
    private String tradeCode;
    @Schema(description = "订单明细ID")
    private Long itemId;
}

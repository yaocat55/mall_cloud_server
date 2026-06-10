package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单明细返回DTO")
public class TradeItemDTO {
    @Schema(description = "订单明细ID")
    private Long id;
    @Schema(description = "商品ID")
    private Long productId;
    @Schema(description = "商品名称")
    private String productName;
    @Schema(description = "商品规格")
    private String model;
    @Schema(description = "单价")
    private BigDecimal price;
    @Schema(description = "数量")
    private Integer quantity;
    @Schema(description = "金额")
    private BigDecimal amount;
    @Schema(description = "封面图片url")
    private String coverUrl;
}

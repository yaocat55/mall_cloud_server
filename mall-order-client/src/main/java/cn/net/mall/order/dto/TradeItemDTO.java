package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单明细返回DTO")
public class TradeItemDTO {
    @Schema(description = "订单明细ID", example = "1")
    private Long id;
    @Schema(description = "商品ID", example = "1")
    private Long productId;
    @Schema(description = "商品名称", example = "-")
    private String productName;
    @Schema(description = "商品规格", example = "型号")
    private String model;
    @Schema(description = "单价", example = "99.99")
    private BigDecimal price;
    @Schema(description = "数量", example = "10")
    private Integer quantity;
    @Schema(description = "金额", example = "99.99")
    private BigDecimal amount;
    @Schema(description = "封面图片url", example = "https://example.com/cover.png")
    private String coverUrl;
}

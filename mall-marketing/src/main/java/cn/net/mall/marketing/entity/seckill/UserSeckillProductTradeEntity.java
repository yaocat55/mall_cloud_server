package cn.net.mall.marketing.entity.seckill;

import cn.net.mall.entity.SignEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "UserSeckillProductTrade信息")

public class UserSeckillProductTradeEntity extends SignEntity {

    @Schema(description = "trade Id")
    private Long tradeId;
    @Schema(description = "编码")
    private String code;
    @Schema(description = "用户名")
    private String userName;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "总金额")
    private BigDecimal totalAmount;
    @Schema(description = "payment Amount")
    private BigDecimal paymentAmount;
    @Schema(description = "seckill Product Id")
    private Long seckillProductId;
    @Schema(description = "商品ID")
    private Long productId;
    @Schema(description = "product Name")
    private String productName;
    @Schema(description = "model")
    private String model;
    @Schema(description = "价格")
    private BigDecimal price;
    @Schema(description = "cost Price")
    private BigDecimal costPrice;
    @Schema(description = "数量")
    private Integer quantity;
    @Schema(description = "order Time")
    private Date orderTime;
}

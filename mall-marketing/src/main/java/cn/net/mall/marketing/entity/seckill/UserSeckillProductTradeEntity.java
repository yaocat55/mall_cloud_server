package cn.net.mall.marketing.entity.seckill;

import cn.net.mall.entity.SignEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "UserSeckillProductTrade信息")

public class UserSeckillProductTradeEntity extends SignEntity {

    private Long tradeId;
    private String code;
    private String userName;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal paymentAmount;
    private Long seckillProductId;
    private Long productId;
    private String productName;
    private String model;
    private BigDecimal price;
    private BigDecimal costPrice;
    private Integer quantity;
    private Date orderTime;
}

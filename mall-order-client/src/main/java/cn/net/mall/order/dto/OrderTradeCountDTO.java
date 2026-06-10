package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户订单数量统计
 */
@Data
@Schema(description = "用户订单数量统计")
public class OrderTradeCountDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "待付款数量")
    private Integer toPayCount;

    @Schema(description = "待发货数量")
    private Integer toDeliveryCount;

    @Schema(description = "待收货数量")
    private Integer toReceiveCount;

    @Schema(description = "待评价数量")
    private Integer toCommentCount;
}

package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单结算请求参数
 */
@Data
@Schema(description = "订单结算请求参数")
public class OrderSettlementDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "结算项集合")
    private List<OrderSettlementItemDTO> items;
}

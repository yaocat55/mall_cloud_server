package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单提交请求参数
 */
@Data
@Schema(description = "订单提交请求参数")
public class OrderSubmitDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单编码/确认Token")
    private String tradeCode;

    @Schema(description = "收货地址ID")
    private Long deliveryAddressId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "结算项集合")
    private List<OrderSettlementItemDTO> items;
}

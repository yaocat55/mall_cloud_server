package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Schema(description = "订单详情DTO")
public class TradeDetailDTO {
    @Schema(description = "订单编码")
    private String code;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "用户名称")
    private String userName;
    @Schema(description = "订单状态")
    private Integer orderStatus;
    @Schema(description = "支付状态")
    private Integer payStatus;
    @Schema(description = "下单时间")
    private Date orderTime;
    @Schema(description = "总金额")
    private BigDecimal totalAmount;
    @Schema(description = "付款金额")
    private BigDecimal payAmount;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "订单明细列表")
    private List<TradeItemDTO> orderItemList;
    @Schema(description = "订单收货地址")
    private OrderDeliveryAddressDTO orderDeliveryAddress;
}

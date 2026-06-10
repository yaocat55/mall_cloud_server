package cn.net.mall.order.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "订单传输对象")
public class OrderDTO extends BaseEntity {
    @Schema(description = "订单编码")
    private String code;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "用户名称")
    private String userName;
    @Schema(description = "下单时间")
    private Date orderTime;
    @Schema(description = "订单状态")
    private Integer orderStatus;
    @Schema(description = "支付状态")
    private Integer payStatus;
    @Schema(description = "总金额")
    private BigDecimal totalAmount;
    @Schema(description = "付款金额")
    private BigDecimal payAmount;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "订单明细列表")
    private List<OrderItemDTO> orderItemList;
    @Schema(description = "订单收货地址")
    private OrderDeliveryAddressDTO orderDeliveryAddress;
}

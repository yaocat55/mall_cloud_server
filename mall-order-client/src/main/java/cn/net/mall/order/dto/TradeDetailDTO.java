package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Schema(description = "订单详情DTO", example = "string")
public class TradeDetailDTO {
    @Schema(description = "订单编码", example = "CODE_001")
    private String code;
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    @Schema(description = "用户名称", example = "admin")
    private String userName;
    @Schema(description = "订单状态", allowableValues = {"1=待付款", "2=已支付", "3=已发货", "4=已完成", "5=已取消"}, example = "1")
    private Integer orderStatus;
    @Schema(description = "支付状态", allowableValues = {"1=未支付", "2=已支付", "3=已退款", "4=支付失败"}, example = "1")
    private Integer payStatus;
    @Schema(description = "下单时间", example = "2024-01-01")
    private Date orderTime;
    @Schema(description = "总金额", example = "99.99")
    private BigDecimal totalAmount;
    @Schema(description = "付款金额", example = "0")
    private BigDecimal payAmount;
    @Schema(description = "备注", example = "备注")
    private String remark;
    @Schema(description = "订单明细列表", example = "string")
    private List<TradeItemDTO> orderItemList;
    @Schema(description = "订单收货地址", example = "string")
    private OrderDeliveryAddressDTO orderDeliveryAddress;
}

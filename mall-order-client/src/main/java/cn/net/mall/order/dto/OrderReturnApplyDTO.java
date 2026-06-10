package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Schema(description = "退货申请DTO")
public class OrderReturnApplyDTO {
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "订单ID")
    private Long orderId;
    @Schema(description = "订单编码")
    private String orderCode;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "退货原因")
    private String reason;
    @Schema(description = "问题描述")
    private String description;
    @Schema(description = "申请状态")
    private Integer applyStatus;
    @Schema(description = "拟退款金额")
    private BigDecimal refundAmount;
    @Schema(description = "申请时间")
    private Date applyTime;
    @Schema(description = "审核时间")
    private Date auditTime;
    @Schema(description = "收货时间(逆向)")
    private Date receiveTime;
    @Schema(description = "明细列表")
    private List<OrderReturnItemDTO> items;

    @Schema(description = "凭证图片URL列表")
    private List<String> voucherUrls;
}

package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Schema(description = "退货申请DTO", example = "0")
public class OrderReturnApplyDTO {
    @Schema(description = "ID", example = "1")
    private Long id;
    @Schema(description = "订单ID", example = "1")
    private Long orderId;
    @Schema(description = "订单编码", example = "-")
    private String orderCode;
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    @Schema(description = "退货原因", example = "-")
    private String reason;
    @Schema(description = "问题描述", example = "描述信息")
    private String description;
    @Schema(description = "申请状态", allowableValues = {"1=待审核", "2=已通过", "3=已拒绝"}, example = "1")
    private Integer applyStatus;
    @Schema(description = "拟退款金额", example = "0")
    private BigDecimal refundAmount;
    @Schema(description = "申请时间", example = "2024-01-01")
    private Date applyTime;
    @Schema(description = "审核时间", example = "2024-01-01")
    private Date auditTime;
    @Schema(description = "收货时间(逆向)", example = "2024-01-01")
    private Date receiveTime;
    @Schema(description = "明细列表", example = "string")
    private List<OrderReturnItemDTO> items;

    @Schema(description = "凭证图片URL列表", example = "-")
    private List<String> voucherUrls;
}

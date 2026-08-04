package cn.net.mall.pay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 申请退款入参.
 *
 * <p>由业务方（mall-order）售后审核通过后调用。</p>
 */
@Data
@Schema(description = "申请退款入参")
public class PayRefundDTO {

    /** 支付订单号 */
    @NotBlank(message = "支付订单号不能为空")
    @Schema(description = "支付订单号")
    private String payOrderNo;

    /** 业务方订单号（审核判定用：在 pay_order 查不到则进人工审核） */
    @NotBlank(message = "业务方订单号不能为空")
    @Schema(description = "业务方订单号")
    private String bizOrderNo;

    /** 业务类型 */
    @NotBlank(message = "业务类型不能为空")
    @Schema(description = "业务类型", example = "MALL_ORDER")
    private String bizType;

    /** 退款申请人 ID（审核判定用） */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "退款申请人ID")
    private Long userId;

    /** 退款金额（分） */
    @NotNull(message = "退款金额不能为空")
    @Schema(description = "退款金额（分）")
    private Long refundAmount;

    /** 退款原因 */
    @Schema(description = "退款原因")
    private String refundReason;

    /** 退款类型：1用户申请 2系统自动 3人工介入（默认1） */
    @Schema(description = "退款类型", example = "1")
    private Integer refundType = 1;
}

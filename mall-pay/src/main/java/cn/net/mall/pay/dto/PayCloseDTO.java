package cn.net.mall.pay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 关闭未支付单入参.
 */
@Data
@Schema(description = "关闭未支付单入参")
public class PayCloseDTO {

    /** 支付订单号 */
    @NotBlank(message = "支付订单号不能为空")
    @Schema(description = "支付订单号")
    private String payOrderNo;

    /** 关闭原因（用户取消/超时） */
    @Schema(description = "关闭原因")
    private String reason;
}

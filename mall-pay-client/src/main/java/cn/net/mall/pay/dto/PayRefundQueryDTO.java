package cn.net.mall.pay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 查询退款结果入参.
 */
@Data
@Schema(description = "查询退款结果入参")
public class PayRefundQueryDTO {

    /** 退款单号 */
    @NotBlank(message = "退款单号不能为空")
    @Schema(description = "退款单号")
    private String refundNo;
}

package cn.net.mall.pay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

/**
 * 支付web实体
 */
@Schema(description = "支付web实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PayWebDTO {
    /**
     * 订单code
     */
    @Schema(description = "订单code", example = "TC202401010001")
    @NotBlank(message = "tradeCode不能为空")
    private String tradeCode;
}

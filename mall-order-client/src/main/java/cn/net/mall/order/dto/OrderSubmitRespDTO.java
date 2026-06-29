package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "订单提交响应参数")
public class OrderSubmitRespDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    @Schema(description = "订单编码", example = "CODE_001")
    private String code;

    @Schema(description = "应付金额", example = "0")
    private BigDecimal payAmount;
}

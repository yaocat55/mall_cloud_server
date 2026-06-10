package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "退货申请入参DTO")
public class OrderReturnApplyReqDTO {
    @Schema(description = "订单编码")
    private String tradeCode;
    @Schema(description = "订单明细ID")
    private String tradeItemId;
    @Schema(description = "退款类型/退货原因编码")
    private Integer refundType;
    @Schema(description = "凭证图片URL列表")
    private List<String> images;
    @Schema(description = "问题描述/说明")
    private String content;
}

package cn.net.mall.order.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "退货申请查询条件DTO")
public class OrderReturnConditionDTO extends RequestConditionEntity {
    @Schema(description = "订单ID")
    private Long orderId;
    @Schema(description = "订单编码")
    private String orderCode;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "申请状态")
    private Integer applyStatus;
}


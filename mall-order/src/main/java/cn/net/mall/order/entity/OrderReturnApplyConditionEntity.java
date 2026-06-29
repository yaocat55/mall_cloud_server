package cn.net.mall.order.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "退货申请查询条件")
public class OrderReturnApplyConditionEntity extends RequestConditionEntity {

    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    @Schema(description = "订单编码", example = "-")
    private String orderCode;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "申请状态", example = "0")
    private Integer applyStatus;
}


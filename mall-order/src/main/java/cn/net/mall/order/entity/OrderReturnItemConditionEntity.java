package cn.net.mall.order.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "退货明细查询条件")
public class OrderReturnItemConditionEntity extends RequestConditionEntity {

    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    @Schema(description = "退货申请ID", example = "0")
    private Long returnApplyId;

    @Schema(description = "订单明细ID", example = "0")
    private Long orderItemId;
}


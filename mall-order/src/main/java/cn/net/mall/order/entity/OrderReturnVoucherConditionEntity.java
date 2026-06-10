package cn.net.mall.order.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "退货凭证查询条件")
public class OrderReturnVoucherConditionEntity extends RequestConditionEntity {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "退货申请ID")
    private Long returnApplyId;
}


package cn.net.mall.order.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "退货凭证实体")
public class OrderReturnVoucherEntity extends BaseEntity {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "退货申请ID")
    private Long returnApplyId;

    @Schema(description = "凭证图片URL")
    private String url;
}


package cn.net.mall.order.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "退货凭证实体")
public class OrderReturnVoucherEntity extends BaseEntity {

    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    @Schema(description = "退货申请ID", example = "0")
    private Long returnApplyId;

    @Schema(description = "凭证图片URL", example = "https://example.com/image.png")
    private String url;
}


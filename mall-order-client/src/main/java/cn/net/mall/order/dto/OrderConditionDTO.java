package cn.net.mall.order.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "订单查询条件传输对象")
public class OrderConditionDTO extends RequestConditionEntity {
    @Schema(description = "订单编码")
    private String code;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "订单状态")
    private Integer orderStatus;
    @Schema(description = "关键字")
    private String keyword;
}

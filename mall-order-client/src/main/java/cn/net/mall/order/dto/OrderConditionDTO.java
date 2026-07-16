package cn.net.mall.order.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "订单查询条件传输对象", example = "string")
public class OrderConditionDTO extends RequestConditionEntity {
    @Schema(description = "订单编码", example = "CODE_001")
    private String code;
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    @Schema(description = "订单状态", allowableValues = {"1=待付款", "2=已支付", "3=已发货", "4=已完成", "5=已取消", "6=已退货", "7=售后中"}, example = "1")
    private Integer orderStatus;
    @Schema(description = "关键字", example = "-")
    private String keyword;
}

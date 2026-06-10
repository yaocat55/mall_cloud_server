package cn.net.mall.order.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "退货申请实体")
public class OrderReturnApplyEntity extends BaseEntity {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编码")
    private String orderCode;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "退货原因")
    private String reason;

    @Schema(description = "问题描述")
    private String description;

    @Schema(description = "申请状态 1:申请中 2:已拒绝 3:待寄回 4:待质检 5:待入库 6:待退款 7:已完成 8:已取消")
    private Integer applyStatus;

    @Schema(description = "拟退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "申请时间")
    private Date applyTime;

    @Schema(description = "审核时间")
    private Date auditTime;

    @Schema(description = "收货时间(逆向)")
    private Date receiveTime;
}


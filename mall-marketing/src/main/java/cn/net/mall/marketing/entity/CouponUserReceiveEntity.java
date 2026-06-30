package cn.net.mall.marketing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 优惠券领取实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-09-13 15:38:33
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "CouponUserReceive信息")

public class CouponUserReceiveEntity extends CouponUserEntity {

    /**
     * 使用状态 1:已使用 0:未使用
     */
    @Schema(description = "use Status")
    private Integer useStatus;

    /**
     * 使用时间
     */
    @Schema(description = "use Time")
    private Date useTime;

    /**
     * 优惠券实体
     */
    @Schema(description = "coupon Entity")
    private CouponEntity couponEntity;

    /**
     * 优惠券发放实体
     */
    @Schema(description = "coupon User Provide Entity")
    private CouponUserProvideEntity couponUserProvideEntity;
}

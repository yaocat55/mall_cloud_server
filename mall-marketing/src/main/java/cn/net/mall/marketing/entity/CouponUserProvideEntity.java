package cn.net.mall.marketing.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优惠券发放实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-09-13 15:38:33
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CouponUserProvideEntity extends CouponUserEntity {

    /**
     * 有效状态 1:有效 0:无效
     */
    @Schema(description = "valid Status")
    private Integer validStatus;

    /**
     * 已领取数量
     */
    @Schema(description = "已领取数量", example = "0")
    private Integer receiveCount;
}

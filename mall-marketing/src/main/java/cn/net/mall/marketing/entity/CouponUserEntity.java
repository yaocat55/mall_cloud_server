package cn.net.mall.marketing.entity;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 优惠券用户实体
 *
 * @date 2024/9/13 下午6:32
 */
@Data
@Schema(description = "CouponUser信息")

public class CouponUserEntity extends UserProductEntity {

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券名称
     */
    private String couponName;
}

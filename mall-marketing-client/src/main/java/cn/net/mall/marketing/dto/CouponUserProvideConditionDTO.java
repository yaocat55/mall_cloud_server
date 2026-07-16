package cn.net.mall.marketing.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 发券记录查询条件 DTO
 *
 * @date 2026-07-15
 */
@Data
@Schema(description = "发券记录查询条件")
public class CouponUserProvideConditionDTO extends RequestConditionEntity {

    @Schema(description = "系统ID列表")
    private List<Long> idList;

    @Schema(description = "系统ID")
    private Long id;

    @Schema(description = "优惠券ID")
    private Long couponId;

    @Schema(description = "优惠券ID列表")
    private List<Long> couponIdList;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "有效状态 1:有效 0:无效")
    private Integer validStatus;

    @Schema(description = "已领取数量")
    private Integer receiveCount;
}

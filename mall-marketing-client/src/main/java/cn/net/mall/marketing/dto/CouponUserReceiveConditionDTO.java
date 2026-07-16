package cn.net.mall.marketing.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 领券记录查询条件 DTO
 *
 * @date 2026-07-15
 */
@Data
@Schema(description = "领券记录查询条件")
public class CouponUserReceiveConditionDTO extends RequestConditionEntity {

    @Schema(description = "系统ID列表")
    private List<Long> idList;

    @Schema(description = "系统ID")
    private Long id;

    @Schema(description = "优惠券ID")
    private Long couponId;

    @Schema(description = "优惠券ID列表")
    private List<Long> couponIdList;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "使用状态 1:已使用 0:未使用")
    private Integer useStatus;

    @Schema(description = "使用时间")
    private Date useTime;
}

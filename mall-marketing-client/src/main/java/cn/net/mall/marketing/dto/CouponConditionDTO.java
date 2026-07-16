package cn.net.mall.marketing.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 优惠券查询条件 DTO
 *
 * @date 2026-07-15
 */
@Data
@Schema(description = "优惠券查询条件")
public class CouponConditionDTO extends RequestConditionEntity {

    @Schema(description = "系统ID列表")
    private List<Long> idList;

    @Schema(description = "系统ID")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "类型 1：现金券 2：阶梯满减 3：每满减 4：通用折扣 5：满N件折扣 6：满Y元折扣")
    private Integer type;

    @Schema(description = "图片地址")
    private String photoUrl;

    @Schema(description = "领券开始时间")
    private Date receiveStartTime;

    @Schema(description = "领券结束时间")
    private Date receiveEndTime;

    @Schema(description = "使用开始时间")
    private Date useStartTime;

    @Schema(description = "使用结束时间")
    private Date useEndTime;

    @Schema(description = "优惠券数量")
    private Integer quantity;

    @Schema(description = "优惠金额")
    private Integer offMoney;

    @Schema(description = "折扣")
    private Integer discount;

    @Schema(description = "最低使用金额")
    private Integer minMoney;

    @Schema(description = "最少商品件数")
    private Integer minProductCount;

    @Schema(description = "每日限额")
    private Integer limitCountOneDay;

    @Schema(description = "有效状态 1:有效 0:无效")
    private Integer validStatus;
}

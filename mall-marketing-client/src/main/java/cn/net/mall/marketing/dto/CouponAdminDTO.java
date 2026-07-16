package cn.net.mall.marketing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 优惠券管理操作 DTO（新增 / 修改）
 *
 * @date 2026-07-15
 */
@Data
@Schema(description = "优惠券管理操作")
public class CouponAdminDTO {

    @Schema(description = "系统ID（修改时必传）")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "类型", allowableValues = {"1=现金券", "2=阶梯满减", "3=每满减", "4=通用折扣", "5=满N件折扣", "6=满Y元折扣"}, example = "1")
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

    @Schema(description = "优惠金额，比如：满100，减40，这里就是40")
    private Integer offMoney;

    @Schema(description = "折扣，百分之多少，比如：8折，就填入80")
    private Integer discount;

    @Schema(description = "最低使用金额，比如：满100，减40，这里就是100")
    private Integer minMoney;

    @Schema(description = "最少商品件数，比如：2件或者3件")
    private Integer minProductCount;

    @Schema(description = "每日限额，每天领取多少张优惠券")
    private Integer limitCountOneDay;

    @Schema(description = "有效状态", allowableValues = {"0=无效", "1=有效"}, example = "1")
    private Integer validStatus;
}

package cn.net.mall.marketing.entity.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 优惠券web实体
 *
 * @date 2024/9/15 下午7:41
 */
@Schema(description = "优惠券web实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CouponWebEntity {

    /**
     * 优惠券发放系统ID
     */
    @NotNull(message = "id不能为空")
    @Schema(description = "优惠券发放系统ID", example = "1")
    private Long id;

    /**
     * 优惠券系统ID
     */
    @Schema(description = "优惠券系统ID", example = "1")
    private Long couponId;

    /**
     * 优惠券名称
     */
    @Schema(description = "优惠券名称", example = "测试数据")
    private String name;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称", example = "-")
    private String productName;

    /**
     * 类型 1：现金券 2：阶梯满减 3：每满减 4：通用折扣 5：满N件折扣 6：满Y元折扣
     */
    @Schema(description = "优惠券名称", example = "1")
    private Integer type;

    /**
     * 图片地址
     */
    @Schema(description = "图片地址", example = "-")
    private String photoUrl;

    /**
     * 领券开始时间
     */
    @Schema(description = "领券开始时间", example = "2024-01-01")
    private Date receiveStartTime;

    /**
     * 领券结束时间
     */
    @Schema(description = "领券结束时间", example = "2024-01-01")
    private Date receiveEndTime;

    /**
     * 使用开始时间
     */
    @Schema(description = "使用开始时间", example = "-")
    private String useStartTimeStr;

    /**
     * 使用结束时间
     */
    @Schema(description = "使用结束时间", example = "-")
    private String useEndTimeStr;

    /**
     * 优惠券总数量
     */
    @Schema(description = "优惠券总数量", example = "10")
    private Integer quantity;

    /**
     * 已领取数量
     */
    @Schema(description = "已领取数量", example = "0")
    private Integer receiveCount;

    /**
     * 优惠金额，比如：满100，减40， 这里就是40
     */
    @Schema(description = "优惠金额，比如：满100，减40， 这里就是40", example = "0")
    private Integer offMoney;

    /**
     * 折扣，百分之多少，比如：8折，就填入80
     */
    @Schema(description = "折扣，百分之多少，比如：8折，就填入80", example = "10.00")
    private Integer discount;

    /**
     * 最低使用金额，比如：满100，减40， 这里就是100
     */
    @Schema(description = "最低使用金额，比如：满100，减40， 这里就是100", example = "0")
    private Integer minMoney;

    /**
     * 最少商品件数，比如：2件或者3件
     */
    @Schema(description = "最少商品件数，比如：2件或者3件", example = "0")
    private Integer minProductCount;

    /**
     * 有效期天数
     */
    @Schema(description = "有效期天数", example = "0")
    private Integer validDays;

    /**
     * 当前用户是否已领取
     */
    @Schema(description = "当前用户是否已领取", example = "false")
    private boolean currentUserReceived;

    /**
     * 当前优惠券是否已使用
     */
    @Schema(description = "当前优惠券是否已使用", example = "false")
    private boolean hasUsed;
}

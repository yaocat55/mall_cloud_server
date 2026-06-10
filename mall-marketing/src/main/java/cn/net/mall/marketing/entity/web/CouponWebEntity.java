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
@Schema(name = "优惠券web实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CouponWebEntity {

    /**
     * 优惠券发放系统ID
     */
    @NotNull(message = "id不能为空")
    @Schema(name ="优惠券发放系统ID")
    private Long id;

    /**
     * 优惠券系统ID
     */
    @Schema(name ="优惠券系统ID")
    private Long couponId;

    /**
     * 优惠券名称
     */
    @Schema(name ="优惠券名称")
    private String name;

    /**
     * 用户ID
     */
    @Schema(name ="用户ID")
    private Long userId;

    /**
     * 商品ID
     */
    @Schema(name ="商品ID")
    private Long productId;

    /**
     * 商品名称
     */
    @Schema(name ="商品名称")
    private String productName;

    /**
     * 类型 1：现金券 2：阶梯满减 3：每满减 4：通用折扣 5：满N件折扣 6：满Y元折扣
     */
    @Schema(name ="优惠券名称")
    private Integer type;

    /**
     * 图片地址
     */
    @Schema(name ="图片地址")
    private String photoUrl;

    /**
     * 领券开始时间
     */
    @Schema(name ="领券开始时间")
    private Date receiveStartTime;

    /**
     * 领券结束时间
     */
    @Schema(name ="领券结束时间")
    private Date receiveEndTime;

    /**
     * 使用开始时间
     */
    @Schema(name ="使用开始时间")
    private String useStartTimeStr;

    /**
     * 使用结束时间
     */
    @Schema(name ="使用结束时间")
    private String useEndTimeStr;

    /**
     * 优惠券总数量
     */
    @Schema(name ="优惠券总数量")
    private Integer quantity;

    /**
     * 已领取数量
     */
    @Schema(name ="已领取数量")
    private Integer receiveCount;

    /**
     * 优惠金额，比如：满100，减40， 这里就是40
     */
    @Schema(name ="优惠金额，比如：满100，减40， 这里就是40")
    private Integer offMoney;

    /**
     * 折扣，百分之多少，比如：8折，就填入80
     */
    @Schema(name ="折扣，百分之多少，比如：8折，就填入80")
    private Integer discount;

    /**
     * 最低使用金额，比如：满100，减40， 这里就是100
     */
    @Schema(name ="最低使用金额，比如：满100，减40， 这里就是100")
    private Integer minMoney;

    /**
     * 最少商品件数，比如：2件或者3件
     */
    @Schema(name ="最少商品件数，比如：2件或者3件")
    private Integer minProductCount;

    /**
     * 有效期天数
     */
    @Schema(name ="有效期天数")
    private Integer validDays;

    /**
     * 当前用户是否已领取
     */
    @Schema(name ="当前用户是否已领取")
    private boolean currentUserReceived;

    /**
     * 当前优惠券是否已使用
     */
    @Schema(name ="当前优惠券是否已使用")
    private boolean hasUsed;
}

package cn.net.mall.marketing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(name = "优惠券DTO")
public class CouponDTO {
    @Schema(name ="优惠券发放系统ID")
    private Long id;

    @Schema(name ="优惠券系统ID")
    private Long couponId;

    @Schema(name ="优惠券名称")
    private String name;

    @Schema(name ="用户ID")
    private Long userId;

    @Schema(name ="商品ID")
    private Long productId;
    
    @Schema(name ="商品名称")
    private String productName;

    @Schema(name ="类型 1：现金券 2：阶梯满减 3：每满减 4：通用折扣 5：满N件折扣 6：满Y元折扣")
    private Integer type;

    @Schema(name ="图片地址")
    private String photoUrl;

    @Schema(name ="领券开始时间")
    private Date receiveStartTime;

    @Schema(name ="领券结束时间")
    private Date receiveEndTime;

    @Schema(name ="使用开始时间")
    private String useStartTimeStr;

    @Schema(name ="使用结束时间")
    private String useEndTimeStr;

    @Schema(name ="优惠券总数量")
    private Integer quantity;

    @Schema(name ="已领取数量")
    private Integer receiveCount;

    @Schema(name ="优惠金额，比如：满100，减40， 这里就是40")
    private Integer offMoney;

    @Schema(name ="折扣，百分之多少，比如：8折，就填入80")
    private Integer discount;

    @Schema(name ="最低使用金额，比如：满100，减40， 这里就是100")
    private Integer minMoney;

    @Schema(name ="最少商品件数，比如：2件或者3件")
    private Integer minProductCount;
    
    @Schema(name ="有效期天数")
    private Integer validDays;
    
    @Schema(name ="当前用户是否已领取")
    private Boolean currentUserReceived;

    @Schema(name ="当前优惠券是否已使用")
    private Boolean hasUsed;
}

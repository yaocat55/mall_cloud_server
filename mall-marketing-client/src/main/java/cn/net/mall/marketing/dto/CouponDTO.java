package cn.net.mall.marketing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "优惠券DTO", example = "0")
public class CouponDTO {
    @Schema(description = "优惠券发放系统ID", example = "1")
    private Long id;

    @Schema(description = "优惠券系统ID", example = "1")
    private Long couponId;

    @Schema(description = "优惠券名称", example = "测试数据")
    private String name;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "商品ID", example = "1")
    private Long productId;
    
    @Schema(description = "商品名称", example = "-")
    private String productName;

    @Schema(description = "类型 1：现金券 2：阶梯满减 3：每满减 4：通用折扣 5：满N件折扣 6：满Y元折扣", example = "1")
    private Integer type;

    @Schema(description = "图片地址", example = "-")
    private String photoUrl;

    @Schema(description = "领券开始时间", example = "2024-01-01")
    private Date receiveStartTime;

    @Schema(description = "领券结束时间", example = "2024-01-01")
    private Date receiveEndTime;

    @Schema(description = "使用开始时间", example = "-")
    private String useStartTimeStr;

    @Schema(description = "使用结束时间", example = "-")
    private String useEndTimeStr;

    @Schema(description = "优惠券总数量", example = "10")
    private Integer quantity;

    @Schema(description = "已领取数量", example = "0")
    private Integer receiveCount;

    @Schema(description = "优惠金额，比如：满100，减40， 这里就是40", example = "0")
    private Integer offMoney;

    @Schema(description = "折扣，百分之多少，比如：8折，就填入80", example = "10.00")
    private Integer discount;

    @Schema(description = "最低使用金额，比如：满100，减40， 这里就是100", example = "0")
    private Integer minMoney;

    @Schema(description = "最少商品件数，比如：2件或者3件", example = "0")
    private Integer minProductCount;
    
    @Schema(description = "有效期天数", example = "0")
    private Integer validDays;
    
    @Schema(description = "当前用户是否已领取", example = "false")
    private Boolean currentUserReceived;

    @Schema(description = "当前优惠券是否已使用", example = "false")
    private Boolean hasUsed;
}

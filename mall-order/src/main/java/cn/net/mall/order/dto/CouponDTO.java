package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

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

    @Schema(name ="类型")
    private Integer type;

    @Schema(name ="金额")
    private BigDecimal amount;
    
    @Schema(name ="有效天数")
    private Integer validDays;
    
    @Schema(name ="使用开始时间")
    private String useStartTimeStr;
    
    @Schema(name ="使用结束时间")
    private String useEndTimeStr;
    
    @Schema(name ="当前用户是否已领取")
    private Boolean currentUserReceived;
    
    // Add other fields if necessary from CouponWebEntity
    // Assuming type, amount, etc. are sufficient for logic
    
    // For OrderService logic, we might need useStartTime/useEndTime as Date or String?
    // CouponWebEntity uses Strings for display. But logic might need parsing.
}

package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "优惠券DTO")
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

    @Schema(description = "类型", example = "1")
    private Integer type;

    @Schema(description = "金额", example = "99.99")
    private BigDecimal amount;
    
    @Schema(description = "有效天数", example = "0")
    private Integer validDays;
    
    @Schema(description = "使用开始时间", example = "-")
    private String useStartTimeStr;
    
    @Schema(description = "使用结束时间", example = "-")
    private String useEndTimeStr;
    
    @Schema(description = "当前用户是否已领取", example = "false")
    private Boolean currentUserReceived;
    
    // Add other fields if necessary from CouponWebEntity
    // Assuming type, amount, etc. are sufficient for logic
    
    // For OrderService logic, we might need useStartTime/useEndTime as Date or String?
    // CouponWebEntity uses Strings for display. But logic might need parsing.
}

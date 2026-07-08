package cn.net.mall.order.dto;

import cn.net.mall.marketing.dto.CouponDTO;
import cn.net.mall.admin.dto.DeliveryAddressDTO;
import cn.net.mall.product.dto.ShoppingCartDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "订单确认响应DTO", example = "string")
public class OrderConfirmRespDTO {
    
    @Schema(description = "收货地址列表", example = "string")
    private List<DeliveryAddressDTO> addressList;
    
    @Schema(description = "默认收货地址", example = "string")
    private DeliveryAddressDTO defaultAddress;
    
    @Schema(description = "商品列表", example = "string")
    private List<ShoppingCartDTO> items;
    
    @Schema(description = "可用优惠券列表", example = "string")
    private List<CouponDTO> coupons;
    
    @Schema(description = "订单总金额", example = "99.99")
    private BigDecimal totalAmount;
    
    @Schema(description = "应付金额", example = "0")
    private BigDecimal payAmount;
    
    @Schema(description = "优惠金额", example = "0")
    private BigDecimal couponAmount;

    @Schema(description = "订单确认Token", example = "TC202401010001")
    private String tradeCode;
}

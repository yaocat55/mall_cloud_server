package cn.net.mall.order.dto;

import cn.net.mall.marketing.dto.CouponDTO;
import cn.net.mall.auth.dto.DeliveryAddressDTO;
import cn.net.mall.product.dto.ShoppingCartDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(name = "订单确认响应DTO")
public class OrderConfirmRespDTO {
    
    @Schema(name = "收货地址列表")
    private List<DeliveryAddressDTO> addressList;
    
    @Schema(name = "默认收货地址")
    private DeliveryAddressDTO defaultAddress;
    
    @Schema(name = "商品列表")
    private List<ShoppingCartDTO> items;
    
    @Schema(name = "可用优惠券列表")
    private List<CouponDTO> coupons;
    
    @Schema(name = "订单总金额")
    private BigDecimal totalAmount;
    
    @Schema(name = "应付金额")
    private BigDecimal payAmount;
    
    @Schema(name = "优惠金额")
    private BigDecimal couponAmount;

    @Schema(name = "订单确认Token")
    private String tradeCode;
}

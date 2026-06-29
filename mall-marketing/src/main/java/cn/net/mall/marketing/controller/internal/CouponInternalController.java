package cn.net.mall.marketing.controller.internal;

import cn.net.mall.marketing.entity.web.OrderPriceCalculateReq;
import cn.net.mall.marketing.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@Tag(name = "内部服务-优惠券", description = "内部微服务：mall-order 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/coupon")
public class CouponInternalController {
    private final CouponService couponService;
    public CouponInternalController(CouponService couponService) { this.couponService = couponService; }

    @Operation(summary = "计算订单优惠价格", description = "内部服务：由 mall-order 下单时通过 Feign 调用，根据优惠券计算订单优惠金额")
    @PostMapping("/calculateOrderPrice")
    public List<BigDecimal> calculateOrderPrice(@RequestBody OrderPriceCalculateReq req) {
        return couponService.calculateOrderPrice(req);
    }

    @Operation(summary = "核销优惠券", description = "内部服务：由 mall-order 支付完成后通过 Feign 调用，核销已使用的优惠券")
    @PostMapping("/useCoupons")
    public void useCoupons(@RequestBody List<Long> couponIds) { couponService.useCoupons(couponIds); }
}

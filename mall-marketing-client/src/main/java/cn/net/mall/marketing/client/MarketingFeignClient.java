package cn.net.mall.marketing.client;

import cn.net.mall.marketing.dto.CouponDTO;
import cn.net.mall.marketing.dto.CouponReceiveDTO;
import cn.net.mall.marketing.dto.OrderPriceCalculateReqDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;

import static cn.net.mall.marketing.constant.AppConstant.MARKETING_SERVICE_NAME;

/**
 * 营销服务 Feign 客户端
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-order（订单服务）— 下单时计算优惠价格、支付后核销优惠券</li>
 *   <li>mall-mobile-api（BFF 服务）— 获取优惠券列表、领取优惠券</li>
 * </ul>
 */
@FeignClient(value = MARKETING_SERVICE_NAME, contextId = "marketingFeignClient")
public interface MarketingFeignClient {

    /**
     * 获取用户已领取的优惠券列表
     * <p>调用方：mall-mobile-api（BFF 服务）</p>
     */
    @Operation(summary = "获取用户已领取的优惠券列表", description = "由 mall-mobile-api(BFF) 调用，获取当前用户已领取的优惠券列表")
    @GetMapping("/v1/coupon/getUserCouponList")
    List<CouponDTO> getUserCouponList();

    /**
     * 获取可领取的优惠券列表
     * <p>调用方：mall-mobile-api（BFF 服务）</p>
     */
    @Operation(summary = "获取可领取的优惠券列表", description = "由 mall-mobile-api(BFF) 调用，获取当前可领取的优惠券列表")
    @GetMapping("/v1/coupon/getObtainableCouponList")
    List<CouponDTO> getObtainableCouponList();

    /**
     * 计算订单优惠价格
     * <p>调用方：mall-order（订单服务）</p>
     */
    @Operation(summary = "计算订单优惠价格", description = "由 mall-order 下单时调用，根据优惠券计算订单优惠金额")
    @PostMapping("/v1/internal/coupon/calculateOrderPrice")
    List<BigDecimal> calculateOrderPrice(@RequestBody OrderPriceCalculateReqDTO req);

    /**
     * 核销优惠券
     * <p>调用方：mall-order（订单服务）— 支付完成后核销已使用的优惠券</p>
     */
    @Operation(summary = "核销优惠券", description = "由 mall-order 支付完成后调用，核销已使用的优惠券")
    @PostMapping("/v1/internal/coupon/useCoupons")
    void useCoupons(@RequestBody List<Long> couponIds);

    /**
     * 领取优惠券
     * <p>调用方：mall-mobile-api（BFF 服务）</p>
     */
    @Operation(summary = "领取优惠券", description = "由 mall-mobile-api(BFF) 调用，领取指定优惠券")
    @PostMapping("/v1/coupon/receiveCoupon")
    void receiveCoupon(@RequestBody CouponReceiveDTO req);
}

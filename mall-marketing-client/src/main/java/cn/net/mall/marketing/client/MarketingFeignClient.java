package cn.net.mall.marketing.client;

import cn.net.mall.marketing.dto.CouponDTO;
import cn.net.mall.marketing.dto.CouponReceiveDTO;
import cn.net.mall.marketing.dto.OrderPriceCalculateReqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;

import static cn.net.mall.marketing.constant.AppConstant.MARKETING_SERVICE_NAME;

@FeignClient(value = MARKETING_SERVICE_NAME, contextId = "marketingFeignClient")
public interface MarketingFeignClient {

    @GetMapping("/v1/coupon/getUserCouponList")
    List<CouponDTO> getUserCouponList();

    @GetMapping("/v1/coupon/getObtainableCouponList")
    List<CouponDTO> getObtainableCouponList();

    @PostMapping("/v1/coupon/calculateOrderPrice")
    List<BigDecimal> calculateOrderPrice(@RequestBody OrderPriceCalculateReqDTO req);
    
    @PostMapping("/v1/coupon/useCoupons")
    void useCoupons(@RequestBody List<Long> couponIds);

    @PostMapping("/v1/coupon/receiveCoupon")
    void receiveCoupon(@RequestBody CouponReceiveDTO req);
}

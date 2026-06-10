package cn.net.mall.order.client;

import cn.net.mall.order.dto.CouponDTO;
import cn.net.mall.order.dto.OrderPriceCalculateReqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "susan-mall-marketing")
public interface MarketingFeignClient {

    @GetMapping("/v1/coupon/getUserCouponList")
    List<CouponDTO> getUserCouponList();

    @GetMapping("/v1/coupon/getObtainableCouponList")
    List<CouponDTO> getObtainableCouponList();

    @PostMapping("/v1/coupon/calculateOrderPrice")
    List<BigDecimal> calculateOrderPrice(@RequestBody OrderPriceCalculateReqDTO req);
    
    @PostMapping("/v1/coupon/useCoupons")
    void useCoupons(@RequestBody List<Long> couponIds);
}

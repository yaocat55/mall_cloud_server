package cn.net.mall.mobile.controller.mobile;

import cn.net.mall.marketing.client.MarketingFeignClient;
import cn.net.mall.marketing.dto.CouponDTO;
import cn.net.mall.marketing.dto.CouponReceiveDTO;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mobile/v1/coupon")
@RequiredArgsConstructor
@Tag(name = "移动端-优惠券", description = "优惠券查询、领取接口")
public class MobileCouponController {

    private final MarketingFeignClient marketingFeignClient;

    @Operation(summary = "获取用户优惠券列表")
    @GetMapping("/user/list")
    public ApiResult<List> getUserCouponList() {
        return ApiResultUtil.success(marketingFeignClient.getUserCouponList());
    }

    @Operation(summary = "获取可领取优惠券列表")
    @GetMapping("/obtainable")
    public ApiResult<List> getObtainableList() {
        return ApiResultUtil.success(marketingFeignClient.getObtainableCouponList());
    }

    @Operation(summary = "领取优惠券")
    @PostMapping("/receive")
    public ApiResult<Void> receive(@RequestBody CouponReceiveDTO dto) {
        marketingFeignClient.receiveCoupon(dto);
        return ApiResultUtil.success();
    }
}

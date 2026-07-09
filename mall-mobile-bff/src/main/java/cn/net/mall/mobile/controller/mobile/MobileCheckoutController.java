package cn.net.mall.mobile.controller.mobile;

import cn.net.mall.customer.client.AddressFeignClient;
import cn.net.mall.marketing.client.MarketingFeignClient;
import cn.net.mall.marketing.dto.CouponDTO;
import cn.net.mall.order.client.OrderFeignClient;
import cn.net.mall.order.dto.OrderConfirmReqDTO;
import cn.net.mall.order.dto.OrderConfirmRespDTO;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 移动端结算 BFF 控制器
 * 聚合订单确认 + 收货地址 + 可用优惠券
 */
@Slf4j
@RestController
@RequestMapping("/mobile/v1/checkout")
@RequiredArgsConstructor
@Tag(name = "移动端-结算", description = "结算预览——聚合订单确认信息、地址、优惠券")
public class MobileCheckoutController {

    private final OrderFeignClient orderFeignClient;
    private final AddressFeignClient addressFeignClient;
    private final MarketingFeignClient marketingFeignClient;

    @Operation(summary = "结算预览", description = "一次性获取订单确认信息 + 收货地址列表 + 可用优惠券")
    @PostMapping("/preview")
    public ApiResult<Map> preview(@RequestBody OrderConfirmReqDTO req) {
        Map result = new LinkedHashMap<>();

        try {
            result.put("orderPreview", orderFeignClient.confirm(req));
        } catch (Exception e) {
            log.warn("获取订单确认信息失败", e);
            result.put("orderPreview", null);
        }

        try {
            result.put("addresses", addressFeignClient.getUserAddressList());
        } catch (Exception e) {
            log.warn("获取收货地址列表失败", e);
            result.put("addresses", Collections.emptyList());
        }

        try {
            result.put("availableCoupons", marketingFeignClient.getObtainableCouponList());
        } catch (Exception e) {
            log.warn("获取可用优惠券失败", e);
            result.put("availableCoupons", Collections.emptyList());
        }

        return ApiResultUtil.success(result);
    }
}

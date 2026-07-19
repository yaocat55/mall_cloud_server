package cn.net.mall.admin.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.marketing.client.MarketingFeignClient;
import cn.net.mall.marketing.dto.CouponConditionDTO;
import cn.net.mall.marketing.dto.CouponUserProvideConditionDTO;
import cn.net.mall.marketing.dto.CouponUserReceiveConditionDTO;
import cn.net.mall.marketing.dto.SeckillProductConditionDTO;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/v1/marketing")
@RequiredArgsConstructor
@Tag(name = "营销管理", description = "优惠券、秒杀管理")
public class AdminMarketingController {
    private final MarketingFeignClient marketingFeignClient;

    @Operation(summary = "分页查询优惠券", description = "根据条件分页查询优惠券列表，支持多条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/coupon/page")
    public ApiResult<ResponsePageEntity<?>> searchCouponPage(@RequestBody CouponConditionDTO c) { return ApiResultUtil.success(marketingFeignClient.searchByPage(c)); }

    // ========== 秒杀商品 (seckillProduct) ==========

    @Operation(summary = "分页查询秒杀商品", description = "根据条件分页查询秒杀商品列表，支持多条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/seckill/page")
    public ApiResult<ResponsePageEntity<?>> searchSeckillPage(@RequestBody SeckillProductConditionDTO c) { return ApiResultUtil.success(marketingFeignClient.searchSeckillPage(c)); }

    @Operation(summary = "查询秒杀商品详情", description = "根据 ID 查询单个秒杀商品的详细信息", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/seckill/detail")
    public ApiResult<Object> findSeckillById(@RequestParam("id") Long id) { return ApiResultUtil.success(marketingFeignClient.findSeckillById(id)); }

    // ========== 发券记录 (couponUserProvide) ==========

    @Operation(summary = "分页查询发券记录", description = "根据条件分页查询发券记录列表，支持多条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserProvide/page")
    public ApiResult<ResponsePageEntity<?>> searchProvidePage(@RequestBody CouponUserProvideConditionDTO c) { return ApiResultUtil.success(marketingFeignClient.searchProvidePage(c)); }

    // ========== 领券记录 (couponUserReceive) ==========

    @Operation(summary = "分页查询领券记录", description = "根据条件分页查询领券记录列表，支持多条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserReceive/page")
    public ApiResult<ResponsePageEntity<?>> searchReceivePage(@RequestBody CouponUserReceiveConditionDTO c) { return ApiResultUtil.success(marketingFeignClient.searchReceivePage(c)); }
}

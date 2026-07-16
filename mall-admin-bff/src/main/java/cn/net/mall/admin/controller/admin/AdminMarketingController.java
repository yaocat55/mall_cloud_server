package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.dto.IdsDTO;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.marketing.client.MarketingFeignClient;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import cn.net.mall.marketing.dto.CouponConditionDTO;
import cn.net.mall.marketing.dto.CouponAdminDTO;
import cn.net.mall.marketing.dto.SeckillProductConditionDTO;
import cn.net.mall.marketing.dto.SeckillProductAdminDTO;
import cn.net.mall.marketing.dto.CouponUserProvideConditionDTO;
import cn.net.mall.marketing.dto.CouponUserReceiveConditionDTO;
import cn.net.mall.marketing.dto.CouponUserProvideDTO;
import cn.net.mall.marketing.dto.CouponUserReceiveDTO;

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
    @Operation(summary = "新增优惠券", description = "新增一条优惠券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/coupon/insert")
    public ApiResult<Integer> insertCoupon(@RequestBody CouponAdminDTO e) { return ApiResultUtil.success(marketingFeignClient.insert(e)); }
    @Operation(summary = "修改优惠券", description = "修改一条已有的优惠券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/coupon/update")
    public ApiResult<Integer> updateCoupon(@RequestBody CouponAdminDTO e) { return ApiResultUtil.success(marketingFeignClient.update(e)); }
    @Operation(summary = "删除优惠券", description = "根据 ID 列表批量删除优惠券", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/coupon/delete")
    public ApiResult<Integer> deleteCoupon(@RequestBody IdsDTO dto) { return ApiResultUtil.success(marketingFeignClient.deleteByIds(dto.getIds())); }

    // ========== 秒杀商品 (seckillProduct) ==========

    @Operation(summary = "分页查询秒杀商品", description = "根据条件分页查询秒杀商品列表，支持多条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/seckill/page")
    public ApiResult<ResponsePageEntity<?>> searchSeckillPage(@RequestBody SeckillProductConditionDTO c) { return ApiResultUtil.success(marketingFeignClient.searchSeckillPage(c)); }

    @Operation(summary = "新增秒杀商品", description = "新增一条秒杀商品记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/seckill/insert")
    public ApiResult<Integer> insertSeckill(@RequestBody SeckillProductAdminDTO e) { return ApiResultUtil.success(marketingFeignClient.insertSeckill(e)); }

    @Operation(summary = "修改秒杀商品", description = "修改一条已有的秒杀商品记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/seckill/update")
    public ApiResult<Integer> updateSeckill(@RequestBody SeckillProductAdminDTO e) { return ApiResultUtil.success(marketingFeignClient.updateSeckill(e)); }

    @Operation(summary = "删除秒杀商品", description = "根据 ID 列表批量删除秒杀商品", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/seckill/delete")
    public ApiResult<Integer> deleteSeckill(@RequestBody IdsDTO dto) { return ApiResultUtil.success(marketingFeignClient.deleteSeckillByIds(dto.getIds())); }

    @Operation(summary = "查询秒杀商品详情", description = "根据 ID 查询单个秒杀商品的详细信息", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/seckill/detail")
    public ApiResult<Object> findSeckillById(@RequestParam("id") Long id) { return ApiResultUtil.success(marketingFeignClient.findSeckillById(id)); }

    // ========== 发券记录 (couponUserProvide) ==========

    @Operation(summary = "分页查询发券记录", description = "根据条件分页查询发券记录列表，支持多条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserProvide/page")
    public ApiResult<ResponsePageEntity<?>> searchProvidePage(@RequestBody CouponUserProvideConditionDTO c) { return ApiResultUtil.success(marketingFeignClient.searchProvidePage(c)); }

    @Operation(summary = "新增发券记录", description = "新增一条发券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserProvide/insert")
    public ApiResult<Integer> insertProvide(@RequestBody CouponUserProvideDTO e) { return ApiResultUtil.success(marketingFeignClient.insertProvide(e)); }

    @Operation(summary = "修改发券记录", description = "修改一条已有的发券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserProvide/update")
    public ApiResult<Integer> updateProvide(@RequestBody CouponUserProvideDTO e) { return ApiResultUtil.success(marketingFeignClient.updateProvide(e)); }

    @Operation(summary = "删除发券记录", description = "根据 ID 列表批量删除发券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserProvide/delete")
    public ApiResult<Integer> deleteProvide(@RequestBody IdsDTO dto) { return ApiResultUtil.success(marketingFeignClient.deleteProvideByIds(dto.getIds())); }

    // ========== 领券记录 (couponUserReceive) ==========

    @Operation(summary = "分页查询领券记录", description = "根据条件分页查询领券记录列表，支持多条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserReceive/page")
    public ApiResult<ResponsePageEntity<?>> searchReceivePage(@RequestBody CouponUserReceiveConditionDTO c) { return ApiResultUtil.success(marketingFeignClient.searchReceivePage(c)); }

    @Operation(summary = "新增领券记录", description = "新增一条领券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserReceive/insert")
    public ApiResult<Integer> insertReceive(@RequestBody CouponUserReceiveDTO e) { return ApiResultUtil.success(marketingFeignClient.insertReceive(e)); }

    @Operation(summary = "修改领券记录", description = "修改一条已有的领券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserReceive/update")
    public ApiResult<Integer> updateReceive(@RequestBody CouponUserReceiveDTO e) { return ApiResultUtil.success(marketingFeignClient.updateReceive(e)); }

    @Operation(summary = "删除领券记录", description = "根据 ID 列表批量删除领券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserReceive/delete")
    public ApiResult<Integer> deleteReceive(@RequestBody IdsDTO dto) { return ApiResultUtil.success(marketingFeignClient.deleteReceiveByIds(dto.getIds())); }
}

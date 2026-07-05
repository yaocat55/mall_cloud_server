package cn.net.mall.admin.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.marketing.client.MarketingFeignClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/v1/marketing")
@RequiredArgsConstructor
@Tag(name = "管理后台-营销管理", description = "优惠券、秒杀等营销管理接口，需携带 Bearer Token")
public class AdminMarketingController {
    private final MarketingFeignClient marketingFeignClient;

    @Operation(summary = "分页查询优惠券", description = "根据条件分页查询优惠券列表，支持多条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/coupon/page")
    public ResponsePageEntity<?> searchCouponPage(@RequestBody Map c) { return marketingFeignClient.searchByPage(c); }
    @Operation(summary = "新增优惠券", description = "新增一条优惠券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/coupon/insert")
    public int insertCoupon(@RequestBody Object e) { return marketingFeignClient.insert(e); }
    @Operation(summary = "修改优惠券", description = "修改一条已有的优惠券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/coupon/update")
    public int updateCoupon(@RequestBody Object e) { return marketingFeignClient.update(e); }
    @Operation(summary = "删除优惠券", description = "根据 ID 列表批量删除优惠券", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/coupon/delete")
    public int deleteCoupon(@RequestBody @NotNull List ids) { return marketingFeignClient.deleteByIds(ids); }

    // ========== 秒杀商品 (seckillProduct) ==========

    @Operation(summary = "分页查询秒杀商品", description = "根据条件分页查询秒杀商品列表，支持多条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/seckill/page")
    public ResponsePageEntity<?> searchSeckillPage(@RequestBody Map c) { return marketingFeignClient.searchSeckillPage(c); }

    @Operation(summary = "新增秒杀商品", description = "新增一条秒杀商品记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/seckill/insert")
    public int insertSeckill(@RequestBody Object e) { return marketingFeignClient.insertSeckill(e); }

    @Operation(summary = "修改秒杀商品", description = "修改一条已有的秒杀商品记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/seckill/update")
    public int updateSeckill(@RequestBody Object e) { return marketingFeignClient.updateSeckill(e); }

    @Operation(summary = "删除秒杀商品", description = "根据 ID 列表批量删除秒杀商品", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/seckill/delete")
    public int deleteSeckill(@RequestBody @NotNull List ids) { return marketingFeignClient.deleteSeckillByIds(ids); }

    @Operation(summary = "查询秒杀商品详情", description = "根据 ID 查询单个秒杀商品的详细信息", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/seckill/detail")
    public Object findSeckillById(@RequestParam("id") Long id) { return marketingFeignClient.findSeckillById(id); }

    // ========== 发券记录 (couponUserProvide) ==========

    @Operation(summary = "分页查询发券记录", description = "根据条件分页查询发券记录列表，支持多条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserProvide/page")
    public ResponsePageEntity<?> searchProvidePage(@RequestBody Map c) { return marketingFeignClient.searchProvidePage(c); }

    @Operation(summary = "新增发券记录", description = "新增一条发券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserProvide/insert")
    public int insertProvide(@RequestBody Object e) { return marketingFeignClient.insertProvide(e); }

    @Operation(summary = "修改发券记录", description = "修改一条已有的发券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserProvide/update")
    public int updateProvide(@RequestBody Object e) { return marketingFeignClient.updateProvide(e); }

    @Operation(summary = "删除发券记录", description = "根据 ID 列表批量删除发券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserProvide/delete")
    public int deleteProvide(@RequestBody @NotNull List ids) { return marketingFeignClient.deleteProvideByIds(ids); }

    // ========== 领券记录 (couponUserReceive) ==========

    @Operation(summary = "分页查询领券记录", description = "根据条件分页查询领券记录列表，支持多条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserReceive/page")
    public ResponsePageEntity<?> searchReceivePage(@RequestBody Map c) { return marketingFeignClient.searchReceivePage(c); }

    @Operation(summary = "新增领券记录", description = "新增一条领券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserReceive/insert")
    public int insertReceive(@RequestBody Object e) { return marketingFeignClient.insertReceive(e); }

    @Operation(summary = "修改领券记录", description = "修改一条已有的领券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserReceive/update")
    public int updateReceive(@RequestBody Object e) { return marketingFeignClient.updateReceive(e); }

    @Operation(summary = "删除领券记录", description = "根据 ID 列表批量删除领券记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/couponUserReceive/delete")
    public int deleteReceive(@RequestBody @NotNull List ids) { return marketingFeignClient.deleteReceiveByIds(ids); }
}

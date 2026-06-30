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

    @Operation(summary = "分页查询优惠券", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/coupon/page")
    public ResponsePageEntity<?> searchCouponPage(@RequestBody Map<String, Object> c) { return null; }
    @Operation(summary = "新增优惠券", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/coupon/insert")
    public int insertCoupon(@RequestBody Object e) { return 0; }
    @Operation(summary = "修改优惠券", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/coupon/update")
    public int updateCoupon(@RequestBody Object e) { return 0; }
    @Operation(summary = "删除优惠券", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/coupon/delete")
    public int deleteCoupon(@RequestBody @NotNull List<Long> ids) { return 0; }
}
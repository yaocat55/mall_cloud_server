package cn.net.mall.marketing.controller.internal;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.marketing.dto.CouponReceiveDTO;
import cn.net.mall.marketing.entity.CouponConditionEntity;
import cn.net.mall.marketing.entity.CouponEntity;
import cn.net.mall.marketing.entity.web.CouponWebEntity;
import cn.net.mall.marketing.entity.web.OrderPriceCalculateReq;
import cn.net.mall.marketing.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "内部服务-优惠券", description = "内部微服务：mall-order / admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/coupon")
public class CouponInternalController {
    private final CouponService couponService;

    public CouponInternalController(CouponService couponService) {
        this.couponService = couponService;
    }

    // ========== 订单服务专用（mall-order） ==========

    @Operation(summary = "计算订单优惠价格", description = "内部服务：由 mall-order 下单时通过 Feign 调用，根据优惠券计算订单优惠金额")
    @PostMapping("/calculateOrderPrice")
    public List<BigDecimal> calculateOrderPrice(@RequestBody OrderPriceCalculateReq req) {
        return couponService.calculateOrderPrice(req);
    }

    @Operation(summary = "核销优惠券", description = "内部服务：由 mall-order 支付完成后通过 Feign 调用，核销已使用的优惠券")
    @PostMapping("/useCoupons")
    public void useCoupons(@RequestBody List<Long> couponIds) {
        couponService.useCoupons(couponIds);
    }

    // ========== 通用 CRUD（admin-bff 等） ==========

    @Operation(summary = "通过id查询优惠券信息", description = "内部服务：根据ID查询优惠券信息")
    @GetMapping("/findById")
    public CouponEntity findById(Long id) {
        return couponService.findById(id);
    }

    @Operation(summary = "分页查询优惠券列表", description = "内部服务：根据条件分页查询优惠券列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CouponEntity> searchByPage(@RequestBody CouponConditionEntity condition) {
        return couponService.searchByPage(condition);
    }

    @Operation(summary = "新增优惠券", description = "内部服务：新增优惠券")
    @PostMapping("/insert")
    public int insert(@RequestBody CouponEntity entity) {
        return couponService.insert(entity);
    }

    @Operation(summary = "修改优惠券", description = "内部服务：修改优惠券")
    @PostMapping("/update")
    public int update(@RequestBody CouponEntity entity) {
        return couponService.update(entity);
    }

    @Operation(summary = "批量删除优惠券", description = "内部服务：批量删除优惠券")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return couponService.deleteByIds(ids);
    }

    @Operation(summary = "获取可领取的优惠券列表", description = "内部服务：获取当前可领取的优惠券列表")
    @GetMapping("/obtainableList")
    public List<CouponWebEntity> getObtainableCouponList() {
        return couponService.getObtainableCouponList();
    }

    @Operation(summary = "获取用户已领取的优惠券列表", description = "内部服务：获取当前用户已领取的优惠券列表")
    @GetMapping("/userList")
    public List<CouponWebEntity> getUserCouponList() {
        return couponService.getUserCouponList();
    }

    @Operation(summary = "领取优惠券", description = "内部服务：领取指定优惠券")
    @PostMapping("/receiveCoupon")
    public void receiveCoupon(@RequestBody CouponReceiveDTO req) {
        CouponWebEntity entity = new CouponWebEntity();
        entity.setId(req.getId());
        couponService.receiveCoupon(entity);
    }
}

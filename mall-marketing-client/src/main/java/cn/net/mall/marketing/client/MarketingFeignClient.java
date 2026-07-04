package cn.net.mall.marketing.client;

import cn.net.mall.marketing.dto.CouponDTO;
import cn.net.mall.marketing.dto.CouponReceiveDTO;
import cn.net.mall.marketing.dto.OrderPriceCalculateReqDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.net.mall.entity.ResponsePageEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    // ========== 优惠券 (coupon) 管理端 ==========

    @Operation(summary = "分页查询优惠券（管理端）", description = "根据条件分页查询优惠券列表，支持多条件筛选，请求参数包含 pageNo、pageSize 及可选筛选条件")
    @PostMapping("/v1/coupon/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map<String, Object> condition);

    @Operation(summary = "新增优惠券（管理端）", description = "新增一条优惠券记录，请求体包含优惠券名称、类型、面值、使用条件等字段")
    @PostMapping("/v1/coupon/insert")
    int insert(@RequestBody Object entity);

    @Operation(summary = "修改优惠券（管理端）", description = "修改一条已有的优惠券记录，请求体包含要修改的优惠券字段")
    @PostMapping("/v1/coupon/update")
    int update(@RequestBody Object entity);

    @Operation(summary = "删除优惠券（管理端）", description = "根据 ID 列表批量删除优惠券")
    @PostMapping("/v1/coupon/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);

    // ========== 秒杀商品 (seckillProduct) 管理端 ==========

    @Operation(summary = "分页查询秒杀商品（管理端）", description = "根据条件分页查询秒杀商品列表，请求参数包含 pageNo、pageSize 及可选筛选条件")
    @PostMapping("/v1/seckillProduct/searchByPage")
    ResponsePageEntity<?> searchSeckillPage(@RequestBody Map<String, Object> condition);

    @Operation(summary = "新增秒杀商品（管理端）", description = "新增一条秒杀商品记录，请求体包含秒杀商品ID、秒杀价、库存、场次等字段")
    @PostMapping("/v1/seckillProduct/insert")
    int insertSeckill(@RequestBody Object entity);

    @Operation(summary = "修改秒杀商品（管理端）", description = "修改一条已有的秒杀商品记录，请求体包含要修改的秒杀商品字段")
    @PostMapping("/v1/seckillProduct/update")
    int updateSeckill(@RequestBody Object entity);

    @Operation(summary = "删除秒杀商品（管理端）", description = "根据 ID 列表批量删除秒杀商品")
    @PostMapping("/v1/seckillProduct/deleteByIds")
    int deleteSeckillByIds(@RequestBody @NotNull List<Long> ids);

    @Operation(summary = "查询秒杀商品详情（管理端）", description = "根据 ID 查询单个秒杀商品的详细信息")
    @GetMapping("/v1/seckillProduct/findById")
    Object findSeckillById(@RequestParam("id") Long id);

    // ========== 发券记录 (couponUserProvide) 管理端 ==========

    @Operation(summary = "分页查询发券记录（管理端）", description = "根据条件分页查询发券记录列表，支持多条件筛选")
    @PostMapping("/v1/couponUserProvide/searchByPage")
    ResponsePageEntity<?> searchProvidePage(@RequestBody Map<String, Object> condition);

    @Operation(summary = "新增发券记录（管理端）", description = "新增一条发券记录")
    @PostMapping("/v1/couponUserProvide/insert")
    int insertProvide(@RequestBody Object entity);

    @Operation(summary = "修改发券记录（管理端）", description = "修改一条已有的发券记录")
    @PostMapping("/v1/couponUserProvide/update")
    int updateProvide(@RequestBody Object entity);

    @Operation(summary = "删除发券记录（管理端）", description = "根据 ID 列表批量删除发券记录")
    @PostMapping("/v1/couponUserProvide/deleteByIds")
    int deleteProvideByIds(@RequestBody @NotNull List<Long> ids);

    // ========== 领券记录 (couponUserReceive) 管理端 ==========

    @Operation(summary = "分页查询领券记录（管理端）", description = "根据条件分页查询领券记录列表，支持多条件筛选")
    @PostMapping("/v1/couponUserReceive/searchByPage")
    ResponsePageEntity<?> searchReceivePage(@RequestBody Map<String, Object> condition);

    @Operation(summary = "新增领券记录（管理端）", description = "新增一条领券记录")
    @PostMapping("/v1/couponUserReceive/insert")
    int insertReceive(@RequestBody Object entity);

    @Operation(summary = "修改领券记录（管理端）", description = "修改一条已有的领券记录")
    @PostMapping("/v1/couponUserReceive/update")
    int updateReceive(@RequestBody Object entity);

    @Operation(summary = "删除领券记录（管理端）", description = "根据 ID 列表批量删除领券记录")
    @PostMapping("/v1/couponUserReceive/deleteByIds")
    int deleteReceiveByIds(@RequestBody @NotNull List<Long> ids);
}

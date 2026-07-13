package cn.net.mall.mobile.controller.mobile;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.order.client.OrderFeignClient;
import cn.net.mall.order.dto.*;
import cn.net.mall.pay.client.PayFeignClient;
import cn.net.mall.pay.dto.PayWebDTO;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/mobile/v1/order")
@RequiredArgsConstructor
@Tag(name = "移动端-订单", description = "订单创建、支付、查询聚合接口")
public class MobileOrderController {

    private final OrderFeignClient orderFeignClient;
    private final PayFeignClient payFeignClient;

    @Operation(summary = "确认订单", description = "预下单，获取订单确认信息（金额、优惠等）")
    @PostMapping("/confirm")
    public ApiResult<OrderConfirmRespDTO> confirm(@RequestBody OrderConfirmReqDTO req) {
        return ApiResultUtil.success(orderFeignClient.confirm(req));
    }

    @Operation(summary = "提交订单")
    @PostMapping("/submit")
    public ApiResult<OrderSubmitRespDTO> submit(@RequestBody OrderSubmitDTO dto) {
        return ApiResultUtil.success(orderFeignClient.submit(dto));
    }

    @Operation(summary = "分页查询订单")
    @PostMapping("/page")
    public ApiResult<ResponsePageEntity> search(@RequestBody OrderConditionDTO condition) {
        return ApiResultUtil.success(orderFeignClient.search(condition));
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/detail/{code}")
    public ApiResult<TradeDetailDTO> getDetail(@PathVariable("code") String code) {
        return ApiResultUtil.success(orderFeignClient.getDetailByCode(code));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel")
    public ApiResult<Void> cancel(@RequestBody OrderOperateDTO dto) {
        orderFeignClient.cancel(dto);
        return ApiResultUtil.success();
    }

    @Operation(summary = "确认收货")
    @PostMapping("/confirmReceive")
    public ApiResult<Void> confirmReceive(@RequestBody OrderOperateDTO dto) {
        orderFeignClient.confirmReceive(dto);
        return ApiResultUtil.success();
    }

    @Operation(summary = "评价订单")
    @PostMapping("/evaluate")
    public ApiResult<Void> evaluate(@RequestBody OrderEvaluateDTO dto) {
        orderFeignClient.evaluate(dto);
        return ApiResultUtil.success();
    }

    @Operation(summary = "模拟支付")
    @PostMapping("/pay/mock")
    public ApiResult<Boolean> mockPay(@RequestBody PayWebDTO dto) {
        return ApiResultUtil.success(payFeignClient.mockPay(dto));
    }

    @Operation(summary = "获取用户各状态订单数量")
    @GetMapping("/count")
    public ApiResult<OrderTradeCountDTO> getOrderCount() {
        return ApiResultUtil.success(orderFeignClient.getUserOrderTradeCount());
    }

    @Operation(summary = "申请退款")
    @PostMapping("/return/apply")
    public ApiResult<Long> applyReturn(@RequestBody OrderReturnApplyDTO dto) {
        IdDTO result = orderFeignClient.applyReturn(dto);
        return ApiResultUtil.success(result != null ? result.getId() : null);
    }

    @Operation(summary = "查询退款记录")
    @PostMapping("/return/page")
    public ApiResult<ResponsePageEntity> searchReturn(@RequestBody OrderReturnConditionDTO condition) {
        return ApiResultUtil.success(orderFeignClient.searchReturn(condition));
    }

    @Operation(summary = "获取订单商品项")
    @PostMapping("/tradeItem")
    public ApiResult<TradeItemDTO> getTradeItem(@RequestBody TradeItemReqDTO req) {
        return ApiResultUtil.success(orderFeignClient.getTradeItem(req));
    }
}

package cn.net.mall.mobile.controller.mobile;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.order.client.OrderFeignClient;
import cn.net.mall.order.dto.*;
import cn.net.mall.pay.client.PayFeignClient;
import cn.net.mall.pay.dto.PayWebDTO;
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
    public OrderConfirmRespDTO confirm(@RequestBody OrderConfirmReqDTO req) {
        return orderFeignClient.confirm(req);
    }

    @Operation(summary = "提交订单")
    @PostMapping("/submit")
    public OrderSubmitRespDTO submit(@RequestBody OrderSubmitDTO dto) {
        return orderFeignClient.submit(dto);
    }

    @Operation(summary = "分页查询订单")
    @PostMapping("/page")
    public ResponsePageEntity<OrderDTO> search(@RequestBody OrderConditionDTO condition) {
        return orderFeignClient.search(condition);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/detail/{code}")
    public TradeDetailDTO getDetail(@PathVariable("code") String code) {
        return orderFeignClient.getDetailByCode(code);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel")
    public void cancel(@RequestBody OrderOperateDTO dto) {
        orderFeignClient.cancel(dto);
    }

    @Operation(summary = "确认收货")
    @PostMapping("/confirmReceive")
    public void confirmReceive(@RequestBody OrderOperateDTO dto) {
        orderFeignClient.confirmReceive(dto);
    }

    @Operation(summary = "评价订单")
    @PostMapping("/evaluate")
    public void evaluate(@RequestBody OrderEvaluateDTO dto) {
        orderFeignClient.evaluate(dto);
    }

    @Operation(summary = "模拟支付")
    @PostMapping("/pay/mock")
    public Boolean mockPay(@RequestBody PayWebDTO dto) {
        return payFeignClient.mockPay(dto);
    }

    @Operation(summary = "获取用户各状态订单数量")
    @GetMapping("/count")
    public OrderTradeCountDTO getOrderCount() {
        return orderFeignClient.getUserOrderTradeCount();
    }

    @Operation(summary = "申请退款")
    @PostMapping("/return/apply")
    public Long applyReturn(@RequestBody OrderReturnApplyDTO dto) {
        return orderFeignClient.applyReturn(dto);
    }

    @Operation(summary = "查询退款记录")
    @PostMapping("/return/page")
    public ResponsePageEntity<OrderReturnApplyDTO> searchReturn(@RequestBody OrderReturnConditionDTO condition) {
        return orderFeignClient.searchReturn(condition);
    }

    @Operation(summary = "获取订单商品项")
    @PostMapping("/tradeItem")
    public TradeItemDTO getTradeItem(@RequestBody TradeItemReqDTO req) {
        return orderFeignClient.getTradeItem(req);
    }
}

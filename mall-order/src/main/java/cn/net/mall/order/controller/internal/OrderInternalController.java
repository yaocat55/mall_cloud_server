package cn.net.mall.order.controller.internal;

import cn.net.mall.order.dto.OrderDTO;
import cn.net.mall.order.dto.TradeDetailDTO;
import cn.net.mall.order.entity.OrderEntity;
import cn.net.mall.order.service.OrderService;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "内部服务-订单", description = "内部微服务：mall-pay 通过 Feign 调用")
@RestController
@RequestMapping("/v1/mobile/trade")
public class OrderInternalController {
    private final OrderService orderService;
    public OrderInternalController(OrderService orderService) { this.orderService = orderService; }

    @Operation(summary = "根据编码查询订单详情", description = "内部服务：由 mall-pay 支付回调时通过 Feign 调用，根据订单编码获取详情")
    @GetMapping("/getDetailByCode/{code}")
    public TradeDetailDTO getDetailByCode(@PathVariable("code") String code) { return orderService.getDetailByCode(code); }

    @Operation(summary = "根据编码查询订单", description = "内部服务：由 mall-pay 支付回调时通过 Feign 调用，根据订单编码获取基本信息")
    @GetMapping("/getTrade/{code}")
    public OrderDTO getTrade(@PathVariable("code") String code) { return orderService.getTradeByCode(code); }

    @Operation(summary = "创建订单", description = "内部服务：由 mall-mobile-api(BFF) 通过 Feign 调用，创建新订单")
    @PostMapping("/create")
    public Long create(@RequestBody OrderDTO orderDTO) {
        OrderEntity entity = new OrderEntity();
        BeanUtil.copyProperties(orderDTO, entity);
        if (orderDTO.getPayAmount() != null) entity.setPaymentAmount(orderDTO.getPayAmount());
        return orderService.createOrder(entity);
    }
}

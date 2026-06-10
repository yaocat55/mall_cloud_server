package cn.net.mall.order.controller.mobile;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.order.dto.OrderDTO;
import cn.net.mall.order.dto.OrderReturnApplyDTO;
import cn.net.mall.order.dto.OrderReturnApplyReqDTO;
import cn.net.mall.order.dto.OrderReturnConditionDTO;
import cn.net.mall.order.dto.TradeItemDTO;
import cn.net.mall.order.dto.TradeItemReqDTO;
import cn.net.mall.order.entity.OrderReturnApplyConditionEntity;
import cn.net.mall.order.entity.OrderReturnApplyEntity;
import cn.net.mall.order.entity.OrderReturnItemEntity;
import cn.net.mall.order.service.OrderService;
import cn.net.mall.order.service.OrderReturnApplyService;
import cn.net.mall.order.entity.OrderReturnVoucherEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/mobile/trade/return")
@Tag(name = "退货控制层")
@RequiredArgsConstructor
public class ReturnController {

    private final OrderReturnApplyService orderReturnApplyService;
    private final OrderService orderService;

    @PostMapping("/apply")
    @Operation(summary = "提交退货申请")
    public Long apply(@RequestBody OrderReturnApplyReqDTO req) {
        OrderDTO order = orderService.getTradeByCode(req.getTradeCode());
        OrderReturnApplyEntity entity = new OrderReturnApplyEntity();
        entity.setOrderId(order.getId());
        entity.setOrderCode(order.getCode());
        if (req.getRefundType() != null) {
            entity.setReason(String.valueOf(req.getRefundType()));
        }
        entity.setDescription(req.getContent());

        List<OrderReturnItemEntity> items = new ArrayList<>();
        if (req.getTradeItemId() != null && !req.getTradeItemId().trim().isEmpty()) {
            String[] ids = req.getTradeItemId().split(",");
            for (String raw : ids) {
                String s = raw == null ? null : raw.trim();
                if (s == null || s.isEmpty()) {
                    continue;
                }
                TradeItemReqDTO itemReq = new TradeItemReqDTO();
                itemReq.setTradeCode(req.getTradeCode());
                itemReq.setItemId(Long.parseLong(s));
                TradeItemDTO item = orderService.getTradeItem(itemReq);
                if (item == null) {
                    continue;
                }
                OrderReturnItemEntity it = new OrderReturnItemEntity();
                it.setOrderId(order.getId());
                it.setOrderItemId(item.getId());
                it.setProductId(item.getProductId());
                it.setProductName(item.getProductName());
                it.setProductModel(item.getModel());
                it.setProductPrice(item.getPrice());
                it.setQuantity(1);
                it.setAmount(item.getPrice());
                items.add(it);
            }
        }

        List<OrderReturnVoucherEntity> vouchers = null;
        if (req.getImages() != null) {
            vouchers = req.getImages().stream().filter(x -> x != null && !x.trim().isEmpty()).map(url -> {
                OrderReturnVoucherEntity v = new OrderReturnVoucherEntity();
                v.setUrl(url.trim());
                return v;
            }).toList();
        }
        if (!items.isEmpty()) {
            BigDecimal refundAmount = BigDecimal.ZERO;
            for (OrderReturnItemEntity it : items) {
                BigDecimal amt = it.getAmount() != null ? it.getAmount() : it.getProductPrice();
                if (amt != null) {
                    refundAmount = refundAmount.add(amt);
                }
            }
            entity.setRefundAmount(refundAmount);
        }
        return orderReturnApplyService.createApply(order, entity, items, vouchers);
    }

    @PostMapping("/search")
    @Operation(summary = "查询退货申请列表")
    public ResponsePageEntity<OrderReturnApplyDTO> search(@RequestBody OrderReturnConditionDTO condition) {
        OrderReturnApplyConditionEntity cond = BeanUtil.toBean(condition, OrderReturnApplyConditionEntity.class);
        ResponsePageEntity<OrderReturnApplyEntity> page = orderReturnApplyService.search(cond);
        List<OrderReturnApplyDTO> list = BeanUtil.copyToList(page.getData(), OrderReturnApplyDTO.class);
        return ResponsePageEntity.build(condition, page.getTotalCount(), list);
    }

    @GetMapping("/detail/{code}")
    @Operation(summary = "查看退货申请详情")
    public OrderReturnApplyDTO detail(@PathVariable("code") String code) {
        return orderReturnApplyService.getDetailByCode(code);
    }
}

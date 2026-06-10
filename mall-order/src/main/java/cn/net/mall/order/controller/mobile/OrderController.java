package cn.net.mall.order.controller.mobile;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.annotation.ValidSensitiveWord;
import cn.net.mall.entity.RequestPageEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.order.dto.OrderConfirmReqDTO;
import cn.net.mall.order.dto.OrderConfirmRespDTO;
import cn.net.mall.order.dto.*;
import cn.net.mall.order.entity.OrderEntity;
import cn.net.mall.order.es.document.OrderDocument;
import cn.net.mall.order.service.OrderService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单 控制层
 */
@RestController
@RequestMapping("/v1/mobile/trade")
@Tag(name = "订单控制层")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/getProductCommentByTradeCode/{code}")
    @Operation(summary = "根据订单编码获取商品评价列表")
    public List<OrderProductCommentViewDTO> getProductCommentByTradeCode(@PathVariable("code") String code) {
        return orderService.getProductCommentByTradeCode(code);
    }

    /**
     * 根据ID查询订单
     *
     * @param id 订单ID
     * @return 订单信息
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "根据ID查询订单")
    public OrderDTO detail(@PathVariable("id") Long id) {
        OrderEntity entity = orderService.findById(id);
        if (entity == null) {
            return null;
        }
        OrderDTO dto = new OrderDTO();
        BeanUtil.copyProperties(entity, dto);
        dto.setPayAmount(entity.getPaymentAmount());
        if (entity.getOrderItemList() != null) {
            List<OrderItemDTO> itemDtos = entity.getOrderItemList().stream().map(item -> {
                OrderItemDTO itemDto = new OrderItemDTO();
                BeanUtil.copyProperties(item, itemDto);
                itemDto.setProductPrice(item.getPrice());
                itemDto.setProductQuantity(item.getQuantity());
                itemDto.setProductAttribute(item.getModel());
                itemDto.setAmount(item.getAmount());
                itemDto.setProductPicture(item.getCoverUrl());
                return itemDto;
            }).collect(Collectors.toList());
            dto.setOrderItemList(itemDtos);
        }
        if (entity.getOrderDeliveryAddress() != null) {
            OrderDeliveryAddressDTO addressDTO = new OrderDeliveryAddressDTO();
            BeanUtil.copyProperties(entity.getOrderDeliveryAddress(), addressDTO);
            addressDTO.setReceiverProvince(entity.getOrderDeliveryAddress().getProvince());
            addressDTO.setReceiverCity(entity.getOrderDeliveryAddress().getCity());
            addressDTO.setReceiverRegion(entity.getOrderDeliveryAddress().getDistrict());
            addressDTO.setReceiverDetailAddress(entity.getOrderDeliveryAddress().getDetailAddress());
            dto.setOrderDeliveryAddress(addressDTO);
        }
        return dto;
    }

    /**
     * 分页查询订单
     *
     * @param condition 查询条件
     * @return 订单列表
     */
    @PostMapping("/search")
    @Operation(summary = "分页查询订单")
    public ResponsePageEntity<OrderDTO> search(@RequestBody OrderConditionDTO condition) {
        ResponsePageEntity<OrderDocument> esResult = orderService.searchMyOrdersFromEs(condition);

        List<OrderDTO> dtoList = new ArrayList<>();
        if (esResult.getData() != null) {
            for (OrderDocument doc : esResult.getData()) {
                OrderDTO dto = new OrderDTO();
                BeanUtil.copyProperties(doc, dto);
                dto.setPayAmount(doc.getPaymentAmount());

                if (doc.getOrderItemList() != null) {
                    List<OrderItemDTO> itemDtos = doc.getOrderItemList().stream().map(itemDoc -> {
                        OrderItemDTO itemDto = new OrderItemDTO();
                        itemDto.setProductId(itemDoc.getProductId());
                        itemDto.setProductName(itemDoc.getProductName());
                        itemDto.setProductAttribute(itemDoc.getModel());
                        itemDto.setProductPrice(itemDoc.getPrice());
                        itemDto.setProductQuantity(itemDoc.getQuantity());
                        itemDto.setAmount(itemDoc.getAmount());
                        itemDto.setProductPicture(itemDoc.getCoverUrl());
                        return itemDto;
                    }).collect(Collectors.toList());
                    dto.setOrderItemList(itemDtos);
                }
                if (doc.getOrderDeliveryAddress() != null) {
                    OrderDeliveryAddressDTO addressDTO = new OrderDeliveryAddressDTO();
                    addressDTO.setOrderId(doc.getOrderDeliveryAddress().getOrderId());
                    addressDTO.setReceiverName(doc.getOrderDeliveryAddress().getReceiverName());
                    addressDTO.setReceiverPhone(doc.getOrderDeliveryAddress().getReceiverPhone());
                    addressDTO.setReceiverProvince(doc.getOrderDeliveryAddress().getReceiverProvince());
                    addressDTO.setReceiverCity(doc.getOrderDeliveryAddress().getReceiverCity());
                    addressDTO.setReceiverRegion(doc.getOrderDeliveryAddress().getReceiverRegion());
                    addressDTO.setReceiverDetailAddress(doc.getOrderDeliveryAddress().getReceiverDetailAddress());
                    dto.setOrderDeliveryAddress(addressDTO);
                }
                dtoList.add(dto);
            }
        }

        RequestPageEntity requestPageEntity = new RequestPageEntity();
        requestPageEntity.setPageNo(condition.getPageNo());
        requestPageEntity.setPageSize(condition.getPageSize());

        return ResponsePageEntity.build(requestPageEntity, esResult.getTotalCount(), dtoList);
    }

    /**
     * 从ES查询我的订单列表
     *
     * @param page 页码
     * @param size 每页数量
     * @return 订单列表
     */
    @GetMapping("/my/es")
    @Operation(summary = "从ES查询我的订单列表")
    public ResponsePageEntity<OrderDocument> searchMyOrdersFromEs(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return orderService.searchMyOrdersFromEs(page, size);
    }

    /**
     * 修改订单
     *
     * @param orderEntity 订单信息
     * @return 结果
     */
    @PostMapping("/update")
    @Operation(summary = "修改订单")
    public int update(@RequestBody OrderEntity orderEntity) {
        return orderService.update(orderEntity);
    }

    /**
     * 批量删除订单
     *
     * @param ids ID集合
     * @return 结果
     */
    @PostMapping("/delete")
    @Operation(summary = "批量删除订单")
    public int delete(@RequestBody List<Long> ids) {
        return orderService.deleteByIds(ids);
    }

    /**
     * 订单确认/预览
     *
     * @param req 确认请求
     * @return 确认结果
     */
    @PostMapping("/confirm")
    @Operation(summary = "订单确认/预览")
    public OrderConfirmRespDTO confirm(@RequestBody OrderConfirmReqDTO req) {
        return orderService.confirm(req);
    }

    /**
     * 提交订单
     *
     * @param submitDTO 提交参数
     * @return 提交结果
     */
    @PostMapping("/submit")
    @Operation(summary = "提交订单")
    public OrderSubmitRespDTO submit(@RequestBody OrderSubmitDTO submitDTO) {
        return orderService.submit(submitDTO);
    }

    /**
     * 获取用户订单数量统计
     *
     * @return 统计结果
     */
    @GetMapping("/getUserOrderCount")
    @Operation(summary = "获取用户订单数量统计")
    public OrderTradeCountDTO getUserOrderCount() {
        return orderService.getUserOrderCount();
    }

    /**
     * 取消订单
     *
     * @param operateDTO 操作参数
     */
    @PostMapping("/cancel")
    @Operation(summary = "取消订单")
    public void cancel(@RequestBody OrderOperateDTO operateDTO) {
        orderService.cancel(operateDTO);
    }

    /**
     * 确认收货
     *
     * @param operateDTO 操作参数
     */
    @PostMapping("/confirmReceive")
    @Operation(summary = "确认收货")
    public void confirmReceive(@RequestBody OrderOperateDTO operateDTO) {
        orderService.confirmReceive(operateDTO);
    }

    @PostMapping("/delivery")
    @Operation(summary = "已发货")
    public void delivery(@RequestBody OrderOperateDTO operateDTO) {
        orderService.delivery(operateDTO);
    }

    @PostMapping("/evaluate")
    @Operation(summary = "订单评价")
    @ValidSensitiveWord
    public void evaluate(@RequestBody @Valid OrderEvaluateDTO evaluateDTO) {
        orderService.evaluate(evaluateDTO);
    }

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public Long create(@RequestBody OrderDTO orderDTO) {
        OrderEntity entity = new OrderEntity();
        BeanUtil.copyProperties(orderDTO, entity);
        if (orderDTO.getPayAmount() != null) {
            entity.setPaymentAmount(orderDTO.getPayAmount());
        }
        return orderService.createOrder(entity);
    }

    @PostMapping("/getTradeItem")
    @Operation(summary = "获取订单明细")
    public TradeItemDTO getTradeItem(@RequestBody TradeItemReqDTO req) {
        return orderService.getTradeItem(req);
    }

    @GetMapping("/getUserOrderTradeCount")
    @Operation(summary = "获取用户订单数量统计")
    public OrderTradeCountDTO getUserOrderTradeCount() {
        return orderService.getUserOrderTradeCount();
    }

    @GetMapping("/getDetail/{code}")
    @Operation(summary = "根据code查询订单详情")
    public TradeDetailDTO getDetail(@PathVariable("code") String code) {
        return orderService.getDetailByCode(code);
    }


    @GetMapping("/getTrade/{code}")
    @Operation(summary = "根据code查询订单")
    public OrderDTO getTrade(@PathVariable("code") String code) {
        return orderService.getTradeByCode(code);
    }
}

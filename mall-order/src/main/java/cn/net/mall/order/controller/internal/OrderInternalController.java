package cn.net.mall.order.controller.internal;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.order.dto.*;
import cn.net.mall.order.entity.OrderConditionEntity;
import cn.net.mall.order.entity.OrderDeliveryAddressConditionEntity;
import cn.net.mall.order.entity.OrderDeliveryAddressEntity;
import cn.net.mall.order.entity.OrderEntity;
import cn.net.mall.order.mapper.OrderMapper;
import cn.net.mall.order.service.OrderDeliveryAddressService;
import cn.net.mall.order.service.OrderService;
import cn.net.mall.util.FillUserUtil;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 内部服务-订单管理.
 *
 * <p>供 admin-bff 通过 Feign 调用，无 @PreAuthorize 校验，信任内部调用方。</p>
 */
@Tag(name = "内部服务-订单管理", description = "内部微服务：供 admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/order")
@RequiredArgsConstructor
public class OrderInternalController {

    private final OrderService orderService;
    private final OrderDeliveryAddressService orderDeliveryAddressService;
    private final OrderMapper orderMapper;

    // ========== 订单基础操作 ==========

    @Operation(summary = "分页查询订单（内部）", description = "无条件限制，查询全部订单")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<OrderDTO> searchByPage(@RequestBody OrderConditionDTO condition) {
        OrderConditionEntity entity = new OrderConditionEntity();
        BeanUtil.copyProperties(condition, entity);
        ResponsePageEntity<OrderEntity> page = orderService.searchByPage(entity);
        List<OrderDTO> dtoList = page.getData().stream()
                .map(e -> BeanUtil.toBean(e, OrderDTO.class))
                .collect(Collectors.toList());
        return ResponsePageEntity.build(condition, page.getTotalCount(), dtoList);
    }

    @Operation(summary = "根据编码查询订单详情（内部）", description = "根据订单编码获取完整详情（含明细、收货地址），无用户过滤")
    @GetMapping("/findDetailByCode/{code}")
    public TradeDetailDTO findDetailByCode(@PathVariable("code") String code) {
        OrderConditionEntity cond = new OrderConditionEntity();
        cond.setCode(code);
        List<OrderEntity> list = orderMapper.searchByCondition(cond);
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException("订单不存在");
        }
        return orderService.getDetailByCode(code);
    }

    @Operation(summary = "创建订单（内部）", description = "内部服务调用，创建新订单")
    @PostMapping("/insert")
    public IdDTO insert(@RequestBody OrderDTO orderDTO) {
        OrderEntity entity = new OrderEntity();
        BeanUtil.copyProperties(orderDTO, entity);
        if (orderDTO.getPayAmount() != null) {
            entity.setPaymentAmount(orderDTO.getPayAmount());
        }
        return new IdDTO(orderService.createOrder(entity));
    }

    @Operation(summary = "修改订单（内部）", description = "更新订单信息")
    @PostMapping("/update")
    public RowsDTO update(@RequestBody OrderDTO orderDTO) {
        OrderEntity entity = new OrderEntity();
        BeanUtil.copyProperties(orderDTO, entity);
        if (orderDTO.getPayAmount() != null) {
            entity.setPaymentAmount(orderDTO.getPayAmount());
        }
        return new RowsDTO(orderService.update(entity));
    }

    @Operation(summary = "批量删除订单（内部）")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteByIds(@RequestBody List<Long> ids) {
        return new RowsDTO(orderService.deleteByIds(ids));
    }

    @Operation(summary = "取消订单（内部）", description = "按订单编码取消订单，无用户过滤")
    @PostMapping("/cancel")
    public RowsDTO cancel(@RequestBody OrderOperateDTO operateDTO) {
        OrderConditionEntity cond = new OrderConditionEntity();
        cond.setCode(operateDTO.getTradeCode());
        List<OrderEntity> orders = orderMapper.searchByCondition(cond);
        if (CollectionUtils.isEmpty(orders)) {
            throw new BusinessException("订单不存在");
        }
        OrderEntity order = orders.get(0);
        if (order.getOrderStatus() != 1) {
            throw new BusinessException("当前状态无法取消");
        }
        OrderEntity updateEntity = new OrderEntity();
        updateEntity.setId(order.getId());
        updateEntity.setOrderStatus(5);
        orderService.update(updateEntity);
        return new RowsDTO(1);
    }

    @Operation(summary = "确认收货（内部）", description = "按订单编码确认收货，无用户过滤")
    @PostMapping("/confirmReceive")
    public RowsDTO confirmReceive(@RequestBody OrderOperateDTO operateDTO) {
        OrderConditionEntity cond = new OrderConditionEntity();
        cond.setCode(operateDTO.getTradeCode());
        List<OrderEntity> orders = orderMapper.searchByCondition(cond);
        if (CollectionUtils.isEmpty(orders)) {
            throw new BusinessException("订单不存在");
        }
        OrderEntity order = orders.get(0);
        if (order.getOrderStatus() != 3) {
            throw new BusinessException("当前状态无法确认收货");
        }
        OrderEntity updateEntity = new OrderEntity();
        updateEntity.setId(order.getId());
        updateEntity.setOrderStatus(4);
        orderService.update(updateEntity);
        return new RowsDTO(1);
    }

    // ========== 配送地址 (deliveryAddress) ==========

    @Operation(summary = "分页查询配送地址（内部）")
    @PostMapping("/deliveryAddress/searchByPage")
    public ResponsePageEntity<OrderDeliveryAddressEntity> searchDeliveryAddressPage(
            @RequestBody OrderDeliveryAddressConditionEntity condition) {
        return orderDeliveryAddressService.searchByPage(condition);
    }

    @Operation(summary = "新增配送地址（内部）")
    @PostMapping("/deliveryAddress/insert")
    public RowsDTO insertDeliveryAddress(@RequestBody OrderDeliveryAddressEntity entity) {
        FillUserUtil.fillCreateUserInfo(entity);
        return new RowsDTO(orderDeliveryAddressService.insert(entity));
    }

    @Operation(summary = "修改配送地址（内部）")
    @PostMapping("/deliveryAddress/update")
    public RowsDTO updateDeliveryAddress(@RequestBody OrderDeliveryAddressEntity entity) {
        FillUserUtil.fillUpdateUserInfo(entity);
        return new RowsDTO(orderDeliveryAddressService.update(entity));
    }

    @Operation(summary = "删除配送地址（内部）")
    @PostMapping("/deliveryAddress/deleteByIds")
    public RowsDTO deleteDeliveryAddressByIds(@RequestBody List<Long> ids) {
        return new RowsDTO(orderDeliveryAddressService.deleteByIds(ids));
    }

    @Operation(summary = "查询配送地址详情（内部）")
    @GetMapping("/deliveryAddress/findById")
    public OrderDeliveryAddressEntity findDeliveryAddressById(@RequestParam("id") Long id) {
        return orderDeliveryAddressService.findById(id);
    }
}

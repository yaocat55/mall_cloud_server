package cn.net.mall.order.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.order.entity.OrderEntity;
import cn.net.mall.order.es.document.OrderDeliveryAddressDocument;
import cn.net.mall.order.es.document.OrderDocument;
import cn.net.mall.order.es.document.OrderItemDocument;
import cn.net.mall.order.es.repository.OrderEsRepository;
import cn.net.mall.order.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单创建事件监听器
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedListener {

    private final OrderEsRepository orderEsRepository;

    @Async
    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received order created event for order id: {}", event.getOrderEntity().getId());
        try {
            OrderEntity order = event.getOrderEntity();
            OrderDocument document = new OrderDocument();
            document.setId(order.getId());
            document.setCode(order.getCode());
            document.setUserId(order.getUserId());
            document.setUserName(order.getUserName());
            document.setOrderTime(order.getOrderTime());
            document.setOrderStatus(order.getOrderStatus());
            document.setPayStatus(order.getPayStatus());
            document.setTotalAmount(order.getTotalAmount());
            document.setPaymentAmount(order.getPaymentAmount());
            document.setRemark(order.getRemark());
            document.setCreateTime(order.getCreateTime());

            // 转换订单明细
            if (order.getOrderItemList() != null) {
                List<OrderItemDocument> items = order.getOrderItemList().stream().map(item -> {
                    OrderItemDocument itemDoc = new OrderItemDocument();
                    itemDoc.setId(item.getId());
                    itemDoc.setProductId(item.getProductId());
                    itemDoc.setProductName(item.getProductName());
                    itemDoc.setModel(item.getModel());
                    itemDoc.setPrice(item.getPrice());
                    itemDoc.setQuantity(item.getQuantity());
                    itemDoc.setAmount(item.getAmount());
                    itemDoc.setCoverUrl(item.getCoverUrl());
                    return itemDoc;
                }).collect(Collectors.toList());
                document.setOrderItemList(items);
            }

            if (order.getOrderDeliveryAddress() != null) {
                OrderDeliveryAddressDocument addressDoc = new OrderDeliveryAddressDocument();
                addressDoc.setOrderId(order.getOrderDeliveryAddress().getOrderId());
                addressDoc.setReceiverName(order.getOrderDeliveryAddress().getReceiverName());
                addressDoc.setReceiverPhone(order.getOrderDeliveryAddress().getReceiverPhone());
                addressDoc.setReceiverProvince(order.getOrderDeliveryAddress().getProvince());
                addressDoc.setReceiverCity(order.getOrderDeliveryAddress().getCity());
                addressDoc.setReceiverRegion(order.getOrderDeliveryAddress().getDistrict());
                addressDoc.setReceiverDetailAddress(order.getOrderDeliveryAddress().getDetailAddress());
                addressDoc.setPostCode(order.getOrderDeliveryAddress().getPostCode());
                document.setOrderDeliveryAddress(addressDoc);
            }

            orderEsRepository.save(document);
            log.info("Order {} synced to ES successfully", order.getId());
        } catch (Exception e) {
            log.error("Failed to sync order to ES", e);
        }
    }
}

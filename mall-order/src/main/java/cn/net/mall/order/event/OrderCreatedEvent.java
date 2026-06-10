package cn.net.mall.order.event;

import cn.net.mall.order.entity.OrderEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 订单创建事件
 */
@Getter
public class OrderCreatedEvent extends ApplicationEvent {

    private final OrderEntity orderEntity;

    public OrderCreatedEvent(Object source, OrderEntity orderEntity) {
        super(source);
        this.orderEntity = orderEntity;
    }
}

package cn.net.mall.order.message;

import cn.net.mall.mq.consumer.AbstractMqConsumer;
import cn.net.mall.mq.core.MqMessage;
import cn.net.mall.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RocketMQMessageListener(topic = "ORDER_TIMEOUT_CANCEL_TOPIC", consumerGroup = "order_timeout_cancel_group",
        selectorExpression = "CANCEL_TIMEOUT", consumeMode = ConsumeMode.CONCURRENTLY, messageModel = MessageModel.CLUSTERING)
public class OrderTimeoutCancelConsumer
        extends AbstractMqConsumer<OrderTimeoutCancelMessage>
        implements RocketMQPushConsumerLifecycleListener {

    private final OrderService orderService;

    public OrderTimeoutCancelConsumer(OrderService orderService, ObjectMapper objectMapper) {
        super(OrderTimeoutCancelMessage.class, objectMapper);
        this.orderService = orderService;
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        log.info("[MQ] OrderTimeoutCancelConsumer 设置消费起点为最早 offset, group={}", consumer.getConsumerGroup());
    }

    @Override
    protected void onPayload(OrderTimeoutCancelMessage msg, MqMessage<OrderTimeoutCancelMessage> envelope, MessageExt raw) {
        Long orderId = msg != null ? msg.getOrderId() : null;
        String code = msg != null ? msg.getCode() : null;

        boolean ok = false;
        if (orderId != null) {
            ok = orderService.cancelTimeoutById(orderId);
        }
        if (!ok && code != null) {
            ok = orderService.cancelTimeoutByCode(code);
        }
        log.info("[MQ] 订单超时自动取消结果 ok={} msgId={} orderId={} code={}",
                ok, envelope.getMsgId(), orderId, code);
    }
}

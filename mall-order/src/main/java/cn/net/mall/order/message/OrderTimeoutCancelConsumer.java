package cn.net.mall.order.message;

import cn.net.mall.order.service.OrderService;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

@Slf4j
@Service
@AllArgsConstructor
@RocketMQMessageListener(topic = "ORDER_TIMEOUT_CANCEL_TOPIC", consumerGroup = "order_timeout_cancel_group",
        selectorExpression = "CANCEL_TIMEOUT", consumeMode = ConsumeMode.CONCURRENTLY, messageModel = MessageModel.CLUSTERING)
public class OrderTimeoutCancelConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    private final OrderService orderService;

    @PostConstruct
    public void init() {
        log.info("OrderTimeoutCancelConsumer初始化完成，订阅 topic=ORDER_TIMEOUT_CANCEL_TOPIC, tag=CANCEL_TIMEOUT, group=order_timeout_cancel_group");
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        log.info("OrderTimeoutCancelConsumer设置消费起点为最早offset，group={}, topic={}", consumer.getConsumerGroup(), "ORDER_TIMEOUT_CANCEL_TOPIC");
    }

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            String topic = messageExt.getTopic();
            String tags = messageExt.getTags();
            String keys = messageExt.getKeys();
            String msgId = messageExt.getMsgId();
            int delayLevel = messageExt.getDelayTimeLevel();
            String bodyStr = new String(messageExt.getBody());
            log.info("接收MQ消息: topic={}, tags={}, keys={}, msgId={}, delayLevel={}, body={}", topic, tags, keys, msgId, delayLevel, bodyStr);

            OrderTimeoutCancelMessage msg = tryParse(bodyStr);

            boolean ok = false;
            Long orderId = msg != null ? msg.getOrderId() : null;
            String code = msg != null ? msg.getCode() : null;
            if (orderId != null) {
                ok = orderService.cancelTimeoutById(orderId);
            }
            if (!ok && code != null) {
                ok = orderService.cancelTimeoutByCode(code);
            }
            log.info("订单超时自动取消结果 ok={}, id={}, code={}, msgId={}", ok, orderId, code, msgId);
        } catch (Exception e) {
            log.error("订单超时自动取消失败，messageExt={}", messageExt, e);
        }
    }

    private OrderTimeoutCancelMessage tryParse(String bodyStr) {
        OrderTimeoutCancelMessage msg = null;
        try {
            msg = JSONUtil.toBean(bodyStr, OrderTimeoutCancelMessage.class);
        } catch (Exception ignored) {
            try {
                String normalized = bodyStr;
                if (normalized.startsWith("\"") && normalized.endsWith("\"")) {
                    normalized = normalized.substring(1, normalized.length() - 1);
                    normalized = normalized.replace("\\\"", "\"").replace("\\\\", "\\");
                }
                ObjectMapper mapper = new ObjectMapper();
                msg = mapper.readValue(normalized, OrderTimeoutCancelMessage.class);
            } catch (JsonProcessingException ex) {
                log.error("订单超时取消消息JSON反序列化失败，body={}", bodyStr, ex);
            }
        }
        return msg;
    }
}

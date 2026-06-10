package cn.net.mall.order.config;

import cn.net.mall.order.message.OrderTimeoutCancelConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RocketMQConsumerConfig {

    @Value("${rocketmq.name-server:}")
    private String nameServer;

    private final BusinessConfig businessConfig;
    private final OrderTimeoutCancelConsumer orderTimeoutCancelConsumer;

    @Bean(destroyMethod = "shutdown")
    public DefaultMQPushConsumer orderTimeoutCancelConsumerStarter() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("order_timeout_cancel_group");
        consumer.setNamesrvAddr(nameServer);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(businessConfig.getOrderTimeoutCancelTopic(), businessConfig.getOrderTimeoutCancelTag());
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                try {
                    orderTimeoutCancelConsumer.onMessage(msg);
                } catch (Exception e) {
                    log.error("订单超时取消消费者处理失败，msgId={}", msg.getMsgId(), e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        log.info("手动启动订单超时取消消费者成功，namesrv={}, topic={}, tag={}", nameServer, businessConfig.getOrderTimeoutCancelTopic(), businessConfig.getOrderTimeoutCancelTag());
        return consumer;
    }
}

package cn.net.mall.order.helper;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MqHelper {
    private final ObjectProvider<RocketMQTemplate> rocketMQTemplateProvider;
    public MqHelper(ObjectProvider<RocketMQTemplate> rocketMQTemplateProvider) {
        this.rocketMQTemplateProvider = rocketMQTemplateProvider;
    }

    public void send(String topic, Object message) {
        try {
            RocketMQTemplate rocketMQTemplate = rocketMQTemplateProvider.getIfAvailable();
            if (rocketMQTemplate == null) {
                log.warn("RocketMQTemplate不存在，跳过发送，topic={}, message={}", topic, message);
                return;
            }
            rocketMQTemplate.asyncSend(topic, message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("消息发送成功, topic:{},message:{}", topic, message);
                }
                @Override
                public void onException(Throwable throwable) {
                    log.error("消息发送失败, topic:{},throwable:{}", topic, throwable);
                }
            });
        } catch (Exception e) {
            log.error("消息发送失败，原因：", e);
        }
    }

    public void sendDelay(String topic, Object data, int delayLevel) {
        sendDelayWithTag(topic, null, data, delayLevel, null);
    }

    public void sendDelay(String topic, String tag, Object data, int delayLevel, String keys) {
        sendDelayWithTag(topic, tag, data, delayLevel, keys);
    }

    private void sendDelayWithTag(String topic, String tag, Object data, int delayLevel, String keys) {
        try {
            RocketMQTemplate rocketMQTemplate = rocketMQTemplateProvider.getIfAvailable();
            if (rocketMQTemplate == null) {
                log.warn("RocketMQTemplate不存在，跳过发送，topic={}, message={}", topic, data);
                return;
            }
            String destination = (tag != null && !tag.isEmpty()) ? (topic + ":" + tag) : topic;
            Map<String, Object> headerMap = new HashMap<>();
            headerMap.put(MessageConst.PROPERTY_DELAY_TIME_LEVEL, String.valueOf(delayLevel));
            if (keys != null && !keys.isEmpty()) {
                headerMap.put(RocketMQHeaders.KEYS, keys);
            }
            MessageBuilder<Object> builder = MessageBuilder.withPayload(data);
            for (Map.Entry<String, Object> en : headerMap.entrySet()) {
                builder.setHeader(en.getKey(), en.getValue());
            }
            builder.setHeader(MessageHeaders.CONTENT_TYPE, "application/json");
            Message<Object> message = builder.build();
            rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("延迟消息发送成功, destination:{},message:{}", destination, data);
                }
                @Override
                public void onException(Throwable throwable) {
                    log.error("延迟消息发送失败, destination:{},throwable:{}", destination, throwable);
                }
            }, 3000, delayLevel);
        } catch (Exception e) {
            log.error("延迟消息发送失败，原因：", e);
        }
    }
}

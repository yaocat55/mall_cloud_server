package cn.net.mall.basic.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Collections;


/**
 * mq帮助类
 *
 * @date 2024/3/10 下午4:27
 */
@Slf4j
@Component
public class MqHelper {
    private final ObjectProvider<RocketMQTemplate> rocketMQTemplateProvider;
    public MqHelper(ObjectProvider<RocketMQTemplate> rocketMQTemplateProvider) {
        this.rocketMQTemplateProvider = rocketMQTemplateProvider;
    }

    /**
     * 发送RocketMQ消息
     *
     * @param topic 主题
     * @param data  消息
     */
    public void send(String topic, Object data, int delayLevel) {
        try {
            RocketMQTemplate rocketMQTemplate = rocketMQTemplateProvider.getIfAvailable();
            if (rocketMQTemplate == null) {
                log.warn("RocketMQTemplate不存在，跳过发送，topic={}, message={}", topic, data);
                return;
            }
            MessageHeaders headers = new MessageHeaders(
                    Collections.singletonMap(
                            MessageConst.PROPERTY_DELAY_TIME_LEVEL, String.valueOf(delayLevel)
                    )
            );
            org.springframework.messaging.Message<Object> message = MessageBuilder.createMessage(data, headers);
            rocketMQTemplate.asyncSend(topic, message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("延迟消息发送成功, topic:{},message:{}", topic, data);
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error("延迟消息发送失败, topic:{},throwable:{}", topic, throwable);
                }
            }, 3000, delayLevel);

        } catch (Exception e) {
            log.error("延迟消息发送失败，原因：", e);
        }
    }


    /**
     * 发送RocketMQ消息
     *
     * @param topic   主题
     * @param message 消息
     */
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
}

package cn.net.mall.mq.producer;

import cn.net.mall.mq.core.MqMessage;
import cn.net.mall.mq.core.MqSendOptions;
import cn.net.mall.mq.core.MqSendResult;
import cn.net.mall.mq.hook.MqMessageHook;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 统一 MQ 生产者，覆盖 RocketMQ 全部发送模式。
//
// 发送模式一览：
//   异步（最常用） send(topic, payload)  默认模式，回调自动日志
//   同步           syncSend(topic, payload)  阻塞等待 broker 响应
//   单向           sendOneWay(topic, payload)  不关心结果，最高吞吐
//   延时           send(topic, payload, options.delayLevel(N))  18 级延时
//   批量           sendBatch(topic, payloads)  多消息一次网络请求
//   顺序           sendOrderly(topic, payload, hashKey)  同 hashKey 严格有序
//   事务           sendTransaction(topic, payload, listener)  半消息 + 本地事务
//   SQL 过滤       options.properties("region","east")  消费端 SQL92 过滤
//   加密/压缩      MqMessageHook SPI  beforeSend/afterReceive
//   消息轨迹       MqMessageHook SPI  注入 traceId
//
// 日志格式：[MQ] 发送成功 msgId=x source=xxx topic=xxx tag=xxx keys=xxx delayLv=N msgType=xxx
@Slf4j
@Component
public class MqProducer {

    private final ObjectProvider<RocketMQTemplate> rocketMQTemplateProvider;
    private final ObjectMapper objectMapper;
    private final String source;
    private final List<MqMessageHook> hooks;

    public MqProducer(ObjectProvider<RocketMQTemplate> rocketMQTemplateProvider,
                      ObjectMapper objectMapper,
                      @Value("${spring.application.name:unknown}") String source,
                      List<MqMessageHook> hooks) {
        this.rocketMQTemplateProvider = rocketMQTemplateProvider;
        this.objectMapper = objectMapper;
        this.source = source;
        this.hooks = hooks != null ? hooks.stream()
                .sorted((a, b) -> Integer.compare(a.order(), b.order()))
                .collect(Collectors.toList()) : List.of();
    }

    // ==================== 异步（最常用） ====================

    public void send(String topic, Object payload) {
        send(topic, payload, MqSendOptions.defaults());
    }

    public void send(String topic, String tag, Object payload) {
        send(topic, payload, MqSendOptions.ofTag(tag));
    }

    public void send(String topic, String tag, Object payload, String keys) {
        send(topic, payload, MqSendOptions.of(tag, keys));
    }

    // 异步发送（完整选项），支持 tag/keys/delayLevel/properties
    public void send(String topic, Object payload, MqSendOptions options) {
        sendAsyncInternal(topic, payload, options, null);
    }

    // 异步发送 + 自定义回调
    public void send(String topic, Object payload, MqSendOptions options, SendCallback callback) {
        sendAsyncInternal(topic, payload, options, callback);
    }

    // ==================== 同步 ====================

    public MqSendResult syncSend(String topic, Object payload) {
        return syncSend(topic, payload, MqSendOptions.defaults());
    }

    // 同步发送，阻塞等待 broker 响应，默认超时 3s，高并发慎用
    public MqSendResult syncSend(String topic, Object payload, MqSendOptions options) {
        RocketMQTemplate rocketMQTemplate = rocketMQTemplateProvider.getIfAvailable();
        if (rocketMQTemplate == null) {
            return MqSendResult.fail("?", "RocketMQTemplate 不存在");
        }
        MqMessage<Object> envelope = MqMessage.of(source, payload);
        String msgId = envelope.getMsgId();
        Message<?> message = buildMessage(topic, envelope, options);
        try {
            SendResult result = rocketMQTemplate.syncSend(buildDestination(topic, options), message, getTimeout(options));
            logSuccess(msgId, topic, envelope, options);
            return MqSendResult.ok(msgId, result);
        } catch (Exception e) {
            logFail(msgId, topic, envelope, options, e);
            return MqSendResult.fail(msgId, e.getMessage());
        }
    }

    // ==================== 单向 ====================

    public void sendOneWay(String topic, Object payload) {
        sendOneWay(topic, payload, MqSendOptions.defaults());
    }

    // 不等待 broker 确认，适合日志/埋点类高吞吐场景
    public void sendOneWay(String topic, Object payload, MqSendOptions options) {
        RocketMQTemplate rocketMQTemplate = rocketMQTemplateProvider.getIfAvailable();
        if (rocketMQTemplate == null) {
            log.warn("[MQ] RocketMQTemplate 不存在，跳过 sendOneWay topic={}", topic);
            return;
        }
        MqMessage<Object> envelope = MqMessage.of(source, payload);
        String msgId = envelope.getMsgId();
        Message<?> message = buildMessage(topic, envelope, options);
        try {
            rocketMQTemplate.sendOneWay(buildDestination(topic, options), message);
            log.info("[MQ] 单向发送完成 msgId={} topic={} tag={} msgType={}",
                    msgId, topic, desc(options.getTag()), envelope.getMsgType());
        } catch (Exception e) {
            log.error("[MQ] 单向发送异常 msgId={} topic={} tag={}", msgId, topic, desc(options.getTag()), e);
        }
    }

    // ==================== 批量 ====================

    // 多条消息合并为一次网络请求，所有消息必须属于同一个 topic
    public MqSendResult sendBatch(String topic, List<?> payloads, MqSendOptions options) {
        RocketMQTemplate rocketMQTemplate = rocketMQTemplateProvider.getIfAvailable();
        if (rocketMQTemplate == null) {
            return MqSendResult.fail("?", "RocketMQTemplate 不存在");
        }
        if (payloads == null || payloads.isEmpty()) {
            return MqSendResult.fail("?", "payloads 为空");
        }
        List<Message<?>> messages = payloads.stream()
                .map(p -> buildMessage(topic, MqMessage.of(source, p), options))
                .collect(Collectors.toList());
        try {
            SendResult result = rocketMQTemplate.syncSend(buildDestination(topic, options), messages, getTimeout(options));
            log.info("[MQ] 批量发送成功 topic={} count={} msgId={}", topic, payloads.size(), result.getMsgId());
            return MqSendResult.ok(result.getMsgId(), result);
        } catch (Exception e) {
            log.error("[MQ] 批量发送失败 topic={} count={}", topic, payloads.size(), e);
            return MqSendResult.fail("?", e.getMessage());
        }
    }

    // ==================== 顺序 ====================

    // 顺序同步发送，同一 hashKey 的消息发往同一队列，消费端严格按发送顺序消费
    public MqSendResult sendOrderly(String topic, Object payload, String hashKey, MqSendOptions options) {
        RocketMQTemplate rocketMQTemplate = rocketMQTemplateProvider.getIfAvailable();
        if (rocketMQTemplate == null) {
            return MqSendResult.fail("?", "RocketMQTemplate 不存在");
        }
        MqMessage<Object> envelope = MqMessage.of(source, payload);
        String msgId = envelope.getMsgId();
        Message<?> message = buildMessage(topic, envelope, options);
        try {
            SendResult result = rocketMQTemplate.syncSendOrderly(
                    buildDestination(topic, options), message, hashKey, getTimeout(options));
            logSuccess(msgId, topic, envelope, options);
            return MqSendResult.ok(msgId, result);
        } catch (Exception e) {
            logFail(msgId, topic, envelope, options, e);
            return MqSendResult.fail(msgId, e.getMessage());
        }
    }

    // 顺序异步发送，发送结果通过 callback 通知
    public void sendOrderlyAsync(String topic, Object payload, String hashKey,
                                  MqSendOptions options, SendCallback callback) {
        RocketMQTemplate rocketMQTemplate = rocketMQTemplateProvider.getIfAvailable();
        if (rocketMQTemplate == null) {
            log.warn("[MQ] RocketMQTemplate 不存在，跳过 sendOrderlyAsync topic={} hashKey={}", topic, hashKey);
            return;
        }
        MqMessage<Object> envelope = MqMessage.of(source, payload);
        String msgId = envelope.getMsgId();
        Message<?> message = buildMessage(topic, envelope, options);
        try {
            rocketMQTemplate.asyncSendOrderly(buildDestination(topic, options), message, hashKey, callback);
            log.info("[MQ] 顺序异步发送已提交 msgId={} topic={} hashKey={} tag={}",
                    msgId, topic, hashKey, desc(options.getTag()));
        } catch (Exception e) {
            log.error("[MQ] 顺序异步发送异常 msgId={} topic={} hashKey={}", msgId, topic, hashKey, e);
        }
    }

    // ==================== 事务 ====================

    public MqSendResult sendTransaction(String topic, Object payload,
                                         MqTransactionListener execListener,
                                         MqTransactionCheckListener checkListener) {
        return sendTransaction(topic, payload, MqSendOptions.defaults(), execListener, checkListener);
    }

    // 半消息机制：先发半消息 → 执行本地事务 → COMMIT/ROLLBACK → broker 回查兜底
    public MqSendResult sendTransaction(String topic, Object payload, MqSendOptions options,
                                         MqTransactionListener execListener,
                                         MqTransactionCheckListener checkListener) {
        RocketMQTemplate rocketMQTemplate = rocketMQTemplateProvider.getIfAvailable();
        if (rocketMQTemplate == null) {
            return MqSendResult.fail("?", "RocketMQTemplate 不存在");
        }
        MqMessage<Object> envelope = MqMessage.of(source, payload);
        String msgId = envelope.getMsgId();
        Message<?> message = buildMessage(topic, envelope, options);
        try {
            RocketMQLocalTransactionListener listener =
                    new MqTransactionListenerAdapter(execListener, checkListener, msgId, log);
            org.apache.rocketmq.client.producer.TransactionSendResult txResult =
                    rocketMQTemplate.sendMessageInTransaction(
                            buildDestination(topic, options), message, listener);
            log.info("[MQ] 事务消息已发送 msgId={} topic={} tag={} msgType={}",
                    msgId, topic, desc(options.getTag()), envelope.getMsgType());
            return MqSendResult.ok(msgId, txResult);
        } catch (Exception e) {
            log.error("[MQ] 事务消息发送失败 msgId={} topic={} tag={}",
                    msgId, topic, desc(options.getTag()), e);
            return MqSendResult.fail(msgId, e.getMessage());
        }
    }

    // ==================== 延时便捷方法（兼容旧 API） ====================

    public void sendDelay(String topic, Object payload, int delayLevel) {
        send(topic, payload, MqSendOptions.builder().delayLevel(delayLevel).build());
    }

    public void sendDelay(String topic, String tag, Object payload, int delayLevel) {
        send(topic, payload, MqSendOptions.builder().tag(tag).delayLevel(delayLevel).build());
    }

    public void sendDelay(String topic, String tag, Object payload, int delayLevel, String keys) {
        send(topic, payload, MqSendOptions.builder().tag(tag).delayLevel(delayLevel).keys(keys).build());
    }

    // ==================== 内部实现 ====================

    private void sendAsyncInternal(String topic, Object payload, MqSendOptions options, SendCallback customCallback) {
        RocketMQTemplate rocketMQTemplate = rocketMQTemplateProvider.getIfAvailable();
        if (rocketMQTemplate == null) {
            log.warn("[MQ] RocketMQTemplate 不存在，跳过发送 topic={}", topic);
            return;
        }
        MqMessage<Object> envelope = MqMessage.of(source, payload);
        String msgId = envelope.getMsgId();
        Message<?> message = buildMessage(topic, envelope, options);

        SendCallback callback = customCallback != null ? customCallback : new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                logSuccess(msgId, topic, envelope, options);
            }
            @Override
            public void onException(Throwable e) {
                logFail(msgId, topic, envelope, options, e);
            }
        };

        try {
            rocketMQTemplate.asyncSend(buildDestination(topic, options), message, callback,
                    getTimeout(options), options.getDelayLevel() != null ? options.getDelayLevel() : 0);
            log.info("[MQ] 异步发送已提交 msgId={} topic={} tag={} keys={} delayLv={} msgType={}",
                    msgId, topic, desc(options.getTag()), desc(options.getKeys()),
                    options.getDelayLevel() != null ? options.getDelayLevel() : 0, envelope.getMsgType());
        } catch (Exception e) {
            log.error("[MQ] 异步发送异常 msgId={} topic={} tag={} keys={} delayLv={}",
                    msgId, topic, desc(options.getTag()), desc(options.getKeys()),
                    options.getDelayLevel() != null ? options.getDelayLevel() : 0, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Message<MqMessage<Object>> buildMessage(String topic, MqMessage<Object> envelope, MqSendOptions options) {
        Object finalPayload = envelope.getPayload();
        for (MqMessageHook hook : hooks) {
            finalPayload = hook.onBeforeSend(finalPayload, options);
        }
        envelope.setPayload(finalPayload);

        Map<String, Object> headerMap = new HashMap<>();
        if (options.getDelayLevel() != null) {
            headerMap.put(MessageConst.PROPERTY_DELAY_TIME_LEVEL, String.valueOf(options.getDelayLevel()));
        }
        if (options.getKeys() != null && !options.getKeys().isEmpty()) {
            headerMap.put(RocketMQHeaders.KEYS, options.getKeys());
        }
        headerMap.put(MessageHeaders.CONTENT_TYPE, "application/json");

        MessageBuilder<MqMessage<Object>> builder = MessageBuilder.withPayload(envelope);
        for (Map.Entry<String, Object> en : headerMap.entrySet()) {
            builder.setHeader(en.getKey(), en.getValue());
        }
        if (options.getProperties() != null) {
            for (Map.Entry<String, String> en : options.getProperties().entrySet()) {
                if (en.getValue() != null) {
                    builder.setHeader(en.getKey(), en.getValue());
                }
            }
        }
        return builder.build();
    }

    private long getTimeout(MqSendOptions options) {
        return options.getTimeout() != null ? options.getTimeout() : 3000L;
    }

    void logSuccess(String msgId, String topic, MqMessage<?> envelope, MqSendOptions options) {
        log.info("[MQ] 发送成功 msgId={} source={} topic={} tag={} keys={} delayLv={} msgType={}",
                msgId, envelope.getSource(), topic, desc(options.getTag()),
                desc(options.getKeys()),
                options.getDelayLevel() != null ? options.getDelayLevel() : 0,
                envelope.getMsgType());
    }

    void logFail(String msgId, String topic, MqMessage<?> envelope, MqSendOptions options, Throwable e) {
        log.error("[MQ] 发送失败 msgId={} source={} topic={} tag={} keys={} delayLv={} msgType={}",
                msgId, envelope.getSource(), topic, desc(options.getTag()),
                desc(options.getKeys()),
                options.getDelayLevel() != null ? options.getDelayLevel() : 0,
                envelope.getMsgType(), e);
    }

    private static String desc(String val) {
        return val != null && !val.isEmpty() ? val : "-";
    }

    private String buildDestination(String topic, MqSendOptions options) {
        String tag = options.getTag();
        return (tag != null && !tag.isEmpty()) ? (topic + ":" + tag) : topic;
    }
}

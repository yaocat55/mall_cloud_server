package cn.net.mall.mq.consumer;

import cn.net.mall.mq.core.MqMessage;
import cn.net.mall.mq.hook.MqMessageHook;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQListener;

import java.util.List;
import java.util.stream.Collectors;

// 统一 MQ 消费者基类，自动拆 MqMessage 信封，子类只关心业务 payload。
//
// 基本用法（payload 是普通 Bean）：
//   @Service
//   @RocketMQMessageListener(topic="MY_TOPIC", consumerGroup="my_group",
//       selectorExpression="MY_TAG", consumeMode=ConsumeMode.CONCURRENTLY,
//       messageModel=MessageModel.CLUSTERING)
//   public class MyConsumer extends AbstractMqConsumer<MyPayload> {
//       public MyConsumer(ObjectMapper om) { super(MyPayload.class, om); }
//
//       protected void onPayload(MyPayload p, MqMessage<MyPayload> env, MessageExt raw) {
//           // 业务处理
//       }
//   }
//
// 泛型用法（payload 是 List<Xxx>）：
//   构造时传 objectMapper.getTypeFactory().constructCollectionType(List.class, Xxx.class)
//
// 重试自定义：
//   重写 shouldRetry(Exception, int) → 返回 false 放弃 | true 重试
//
// Hook 链：通过构造注入 MqMessageHook 列表，消费端按 order 降序执行（解压/解密/轨迹恢复）
@Slf4j
public abstract class AbstractMqConsumer<T> implements RocketMQListener<MessageExt> {

    private final JavaType payloadType;
    private final ObjectMapper objectMapper;
    private final List<MqMessageHook> hooks;

    protected AbstractMqConsumer(Class<T> payloadClazz, ObjectMapper objectMapper) {
        this.payloadType = TypeFactory.defaultInstance().constructType(payloadClazz);
        this.objectMapper = objectMapper;
        this.hooks = List.of();
    }

    protected AbstractMqConsumer(JavaType payloadType, ObjectMapper objectMapper) {
        this.payloadType = payloadType;
        this.objectMapper = objectMapper;
        this.hooks = List.of();
    }

    protected AbstractMqConsumer(Class<T> payloadClazz, ObjectMapper objectMapper, List<MqMessageHook> hooks) {
        this.payloadType = TypeFactory.defaultInstance().constructType(payloadClazz);
        this.objectMapper = objectMapper;
        this.hooks = sortedHooksDesc(hooks);
    }

    protected AbstractMqConsumer(JavaType payloadType, ObjectMapper objectMapper, List<MqMessageHook> hooks) {
        this.payloadType = payloadType;
        this.objectMapper = objectMapper;
        this.hooks = sortedHooksDesc(hooks);
    }

    private static List<MqMessageHook> sortedHooksDesc(List<MqMessageHook> hooks) {
        return hooks != null ? hooks.stream()
                .sorted((a, b) -> Integer.compare(b.order(), a.order())) // 消费端逆向执行
                .collect(Collectors.toList()) : List.of();
    }

    @Override
    public void onMessage(MessageExt messageExt) {
        String msgId = "?";
        String source = "?";
        String msgType = "?";
        String topic = messageExt.getTopic();
        String tag = messageExt.getTags() != null ? messageExt.getTags() : "-";
        String keys = messageExt.getKeys() != null ? messageExt.getKeys() : "-";

        try {
            String bodyStr = new String(messageExt.getBody());

            JsonNode root = objectMapper.readTree(bodyStr);
            JsonNode msgIdNode = root.get("msgId");
            JsonNode sourceNode = root.get("source");
            JsonNode msgTypeNode = root.get("msgType");
            JsonNode payloadNode = root.get("payload");

            if (msgIdNode != null) {
                msgId = msgIdNode.asText();
                source = sourceNode != null ? sourceNode.asText() : "?";
                msgType = msgTypeNode != null ? msgTypeNode.asText() : "?";
            }

            log.info("[MQ] 收到消息 msgId={} source={} topic={} tag={} keys={} msgType={}",
                    msgId, source, topic, tag, keys, msgType);

            MqMessage<T> envelope = new MqMessage<>();
            envelope.setMsgId(msgId);
            envelope.setSource(source);
            envelope.setMsgType(msgType);
            if (root.has("timestamp")) {
                envelope.setTimestamp(root.get("timestamp").asLong());
            }

            Object rawPayload = null;
            if (payloadNode != null && !payloadNode.isNull()) {
                rawPayload = payloadNode;
            }

            // Hook 链逆向执行：解压 → 解密 → 轨迹恢复
            for (MqMessageHook hook : hooks) {
                rawPayload = hook.onAfterReceive(rawPayload, envelope);
            }

            T payload = null;
            if (rawPayload instanceof JsonNode) {
                payload = objectMapper.treeToValue((JsonNode) rawPayload, payloadType);
            } else if (rawPayload instanceof String) {
                payload = objectMapper.readValue((String) rawPayload, payloadType);
            }

            envelope.setPayload(payload);
            onPayload(payload, envelope, messageExt);

        } catch (Exception e) {
            int reconsumeTimes = messageExt.getReconsumeTimes();
            if (!shouldRetry(e, reconsumeTimes)) {
                log.error("[MQ] 消费失败，放弃重试 msgId={} source={} topic={} tag={} reconsume={}",
                        msgId, source, topic, tag, reconsumeTimes, e);
                return; // 不抛异常 → RocketMQ 视为消费成功
            }
            log.error("[MQ] 消费失败，等待重试 msgId={} source={} topic={} tag={} reconsume={}",
                    msgId, source, topic, tag, reconsumeTimes, e);
            throw new RuntimeException("[MQ] 消费消息失败 msgId=" + msgId + " topic=" + topic, e);
        }
    }

    // 子类覆盖以控制重试策略。默认永远重试（RocketMQ 上限 16 次）。
    // 返回 true=抛异常触发重试，false=静默丢弃。
    protected boolean shouldRetry(Exception e, int reconsumeTimes) {
        return true;
    }

    // 模板方法：处理业务消息
    protected abstract void onPayload(T payload, MqMessage<T> envelope, MessageExt raw);
}

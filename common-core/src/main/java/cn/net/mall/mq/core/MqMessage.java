package cn.net.mall.mq.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

// 统一消息信封，所有 MQ 消息都包一层，便于日志追踪和问题排查。
// 生产者侧由 MqProducer 自动包装，消费者侧由 AbstractMqConsumer 自动拆封。
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MqMessage<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // 消息唯一 ID，自动生成，用于日志串联
    private String msgId;

    // 来源服务名 = spring.application.name
    private String source;

    // 消息产生时间戳 epoch millis
    private long timestamp;

    // payload 类型 = 简单类名，便于消费者路由
    private String msgType;

    // 业务消息体
    private T payload;

    // 构造一条信封消息，自动生成 msgId + timestamp + msgType
    public static <T> MqMessage<T> of(String source, T payload) {
        MqMessage<T> msg = new MqMessage<>();
        msg.msgId = UUID.randomUUID().toString().replace("-", "");
        msg.source = source;
        msg.timestamp = System.currentTimeMillis();
        msg.msgType = payload != null ? payload.getClass().getSimpleName() : null;
        msg.payload = payload;
        return msg;
    }
}

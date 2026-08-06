package cn.net.mall.mq.hook;

import cn.net.mall.mq.core.MqMessage;
import cn.net.mall.mq.core.MqSendOptions;

// 消息钩子 SPI —— 在发送前/接收后对 payload 做变换。
// 典型用途：压缩(beforeSend→gzip, afterReceive→gunzip)、
// 加密(beforeSend→encrypt, afterReceive→decrypt)、
// 消息轨迹(beforeSend→注入 traceId, afterReceive→恢复上下文)。
// 实现此接口并注册为 Spring Bean，MqProducer 和 AbstractMqConsumer 自动按 order() 排序链式调用。
public interface MqMessageHook {

    // 发送前回调，在 payload 被包入 MqMessage 信封之前调用。
    // 返回变换后的对象（如压缩后返回 Base64 String）。
    default <T> Object onBeforeSend(T payload, MqSendOptions options) {
        return payload;
    }

    // 接收后回调，在 AbstractMqConsumer 拆封后、onPayload 调用前执行。
    // 返回还原后的对象（如解压后返回 JSON String）。
    default Object onAfterReceive(Object payload, MqMessage<?> envelope) {
        return payload;
    }

    // 执行顺序，越小越先执行。
    // 建议：加密 < 压缩 < 轨迹（加密先行，压缩后加密，轨迹最外）。
    default int order() {
        return 0;
    }
}

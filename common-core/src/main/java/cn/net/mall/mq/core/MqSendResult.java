package cn.net.mall.mq.core;

import lombok.AllArgsConstructor;
import lombok.Data;

// MQ 发送结果，统一封装，屏蔽 RocketMQ 具体类型。
@Data
@AllArgsConstructor
public class MqSendResult {

    // 消息唯一 ID = MqMessage.msgId
    private String msgId;

    // 是否成功
    private boolean success;

    // 失败原因
    private String errorMsg;

    // 底层原生结果（RocketMQ SendResult，调试用）
    private Object rawResult;

    public static MqSendResult ok(String msgId, Object rawResult) {
        return new MqSendResult(msgId, true, null, rawResult);
    }

    public static MqSendResult fail(String msgId, String errorMsg) {
        return new MqSendResult(msgId, false, errorMsg, null);
    }
}

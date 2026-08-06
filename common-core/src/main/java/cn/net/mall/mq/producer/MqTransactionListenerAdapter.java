package cn.net.mall.mq.producer;

import cn.net.mall.mq.core.MqMessage;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.slf4j.Logger;
import org.springframework.messaging.Message;

// 适配器：把业务侧 MqTransactionListener/MqTransactionCheckListener 桥接到 RocketMQ Spring 接口。
// 内部使用，不对外暴露。
class MqTransactionListenerAdapter implements RocketMQLocalTransactionListener {

    private final MqTransactionListener execListener;
    private final MqTransactionCheckListener checkListener;
    private final String msgId;
    private final Logger log;

    MqTransactionListenerAdapter(MqTransactionListener execListener,
                                  MqTransactionCheckListener checkListener,
                                  String msgId, Logger log) {
        this.execListener = execListener;
        this.checkListener = checkListener;
        this.msgId = msgId;
        this.log = log;
    }

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        try {
            Object payload = msg.getPayload();
            MqMessage<?> envelope = payload instanceof MqMessage ? (MqMessage<?>) payload : null;
            LocalTransactionState state = execListener.executeLocalTransaction(
                    envelope != null ? envelope.getPayload() : payload,
                    envelope,
                    null);
            log.info("[MQ] 本地事务执行完成 msgId={} state={}", msgId, state);
            return toRocketMqState(state);
        } catch (Exception e) {
            log.error("[MQ] 本地事务执行失败 msgId={}", msgId, e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        if (checkListener == null) {
            log.warn("[MQ] 未注册回查监听器，返回 UNKNOWN msgId={}", msgId);
            return RocketMQLocalTransactionState.UNKNOWN;
        }
        try {
            LocalTransactionState state = checkListener.checkLocalTransaction(null, null);
            log.info("[MQ] 事务回查完成 msgId={} state={}", msgId, state);
            return toRocketMqState(state);
        } catch (Exception e) {
            log.error("[MQ] 事务回查失败 msgId={}", msgId, e);
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }

    private static RocketMQLocalTransactionState toRocketMqState(LocalTransactionState state) {
        if (state == LocalTransactionState.COMMIT_MESSAGE) {
            return RocketMQLocalTransactionState.COMMIT;
        } else if (state == LocalTransactionState.ROLLBACK_MESSAGE) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.UNKNOWN;
    }
}

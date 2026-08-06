package cn.net.mall.mq.producer;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.MessageExt;

// 事务消息回查监听器。
// 半消息长时间未收到 COMMIT/ROLLBACK 时，broker 回调此接口查询本地事务最终状态。
@FunctionalInterface
public interface MqTransactionCheckListener {

    // 回查本地事务状态，返回 COMMIT 或 ROLLBACK
    LocalTransactionState checkLocalTransaction(MessageExt msg, Object arg);
}

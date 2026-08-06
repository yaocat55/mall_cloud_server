package cn.net.mall.mq.producer;

import cn.net.mall.mq.core.MqMessage;
import cn.net.mall.mq.core.MqSendOptions;
import org.apache.rocketmq.client.producer.LocalTransactionState;

// 事务消息本地事务执行器。
// 实现此接口并传给 MqProducer.sendTransaction，半消息发送成功后执行本地事务，
// 根据结果提交(COMMIT_MESSAGE)或回滚(ROLLBACK_MESSAGE)消息。
@FunctionalInterface
public interface MqTransactionListener {

    // 执行本地事务，抛出异常视为回滚
    LocalTransactionState executeLocalTransaction(Object payload, MqMessage<?> envelope, MqSendOptions options)
            throws Exception;
}

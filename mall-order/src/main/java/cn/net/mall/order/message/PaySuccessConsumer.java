package cn.net.mall.order.message;

import cn.net.mall.mq.consumer.AbstractMqConsumer;
import cn.net.mall.mq.core.MqMessage;
import cn.net.mall.order.entity.OrderConditionEntity;
import cn.net.mall.order.entity.OrderEntity;
import cn.net.mall.order.enums.OrderStatusEnum;
import cn.net.mall.order.service.OrderService;
import cn.net.mall.pay.dto.PayNotifyMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 支付成功通知消费者 —— 监听 mall-pay 发送的支付成功 MQ，更新业务订单支付状态.
 *
 * <p>topic / tag 对齐 pay_biz_config 表里的 MALL_ORDER 配置：
 * topic=PAY_NOTIFY_MALL_ORDER, tag=PAY_SUCCESS.</p>
 */
@Slf4j
@Service
@RocketMQMessageListener(
        topic = "PAY_NOTIFY_MALL_ORDER",
        consumerGroup = "mall_order_pay_success_group",
        selectorExpression = "PAY_SUCCESS",
        consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel = MessageModel.CLUSTERING)
public class PaySuccessConsumer extends AbstractMqConsumer<PayNotifyMessage> {

    private final OrderService orderService;

    public PaySuccessConsumer(OrderService orderService, ObjectMapper objectMapper) {
        super(PayNotifyMessage.class, objectMapper);
        this.orderService = orderService;
    }

    @Override
    protected void onPayload(PayNotifyMessage msg, MqMessage<PayNotifyMessage> envelope, MessageExt raw) {
        if (msg == null || msg.getBizOrderNo() == null) {
            log.warn("[MQ] 支付通知缺少 bizOrderNo，跳过 msgId={}", envelope.getMsgId());
            return;
        }

        // 只处理支付成功通知
        if (!"PAY".equals(msg.getNotifyType()) || msg.getPayStatus() == null || msg.getPayStatus() != 20) {
            log.info("[MQ] 非支付成功通知，跳过 msgId={} notifyType={} payStatus={}",
                    envelope.getMsgId(), msg.getNotifyType(), msg.getPayStatus());
            return;
        }

        // 按订单 code 查询
        OrderConditionEntity condition = new OrderConditionEntity();
        condition.setCode(msg.getBizOrderNo());
        cn.net.mall.entity.ResponsePageEntity<OrderEntity> page = orderService.searchByPage(condition);
        List<OrderEntity> orders = page.getData();
        if (orders.isEmpty()) {
            log.warn("[MQ] 未找到对应订单 msgId={} bizOrderNo={}", envelope.getMsgId(), msg.getBizOrderNo());
            return;
        }

        OrderEntity order = orders.get(0);

        // 幂等：已支付则跳过
        if (OrderStatusEnum.PAY.getValue().equals(order.getOrderStatus())) {
            log.info("[MQ] 订单已是支付状态，跳过 msgId={} orderCode={}", envelope.getMsgId(), order.getCode());
            return;
        }

        // 更新订单状态和支付状态
        order.setOrderStatus(OrderStatusEnum.PAY.getValue());
        order.setPayStatus(2); // 已支付
        orderService.update(order);

        log.info("[MQ] 支付成功通知处理完成 msgId={} orderCode={} payOrderNo={} payAmount={}分",
                envelope.getMsgId(), order.getCode(), msg.getPayOrderNo(), msg.getPayAmount());
    }
}

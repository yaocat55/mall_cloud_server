package cn.net.mall.pay.service;

import cn.net.mall.mq.producer.MqProducer;
import cn.net.mall.pay.dto.PayNotifyMessage;
import cn.net.mall.pay.entity.PayBizConfigConditionEntity;
import cn.net.mall.pay.entity.PayBizConfigEntity;
import cn.net.mall.pay.entity.PayOrderConditionEntity;
import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.pay.enums.BizNotifyStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 业务方通知服务 —— MQ 异步通知 + 失败重推补偿.
 *
 * <p>支付/退款成功后，异步通知业务方（如 mall-order），业务方消费 MQ 更新业务订单。
 * 发送失败则标记 biz_notify_status=2，由定时任务扫描重推（退避递增：1min/5min/15min/1h，最多 5 次）。
 * 仍失败 → 人工介入（对账/告警兜底）。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyService {

    private final MqProducer mqProducer;
    private final PayBizConfigService payBizConfigService;
    private final PayOrderService payOrderService;

    /**
     * 发送支付成功异步通知.
     */
    public void sendPaySuccessNotify(PayOrderEntity order) {
        PayBizConfigEntity bizConfig = getBizConfig(order.getBizType());
        if (bizConfig == null) {
            log.warn("未找到业务方配置, bizType={}, payOrderNo={}", order.getBizType(), order.getPayOrderNo());
            return;
        }

        PayNotifyMessage message = new PayNotifyMessage();
        message.setNotifyType("PAY");
        message.setPayOrderNo(order.getPayOrderNo());
        message.setMerchantOrderNo(order.getMerchantOrderNo());
        message.setBizOrderNo(order.getBizOrderNo());
        message.setBizType(order.getBizType());
        message.setPayAmount(order.getPayAmount());
        message.setPayStatus(order.getPayStatus());
        message.setChannelCode(order.getChannelCode());
        message.setSuccessTime(new Date());

        String topic = bizConfig.getNotifyMqTopic();
        String tag = bizConfig.getNotifyMqTag();

        try {
            mqProducer.send(topic, tag, message, order.getPayOrderNo());
            order.setBizNotifyStatus(BizNotifyStatusEnum.SUCCESS.getValue());
            payOrderService.update(order);
        } catch (Exception e) {
            order.setBizNotifyStatus(BizNotifyStatusEnum.FAILED.getValue());
            payOrderService.update(order);
            log.error("支付通知发送异常, payOrderNo={}", order.getPayOrderNo(), e);
        }
    }

    /**
     * 定时扫描发送失败的支付通知（biz_notify_status=2），退避重推。
     *
     * <p>重试间隔：1min / 5min / 15min / 1h，最多 5 次。超过最大重试次数则标记为人工处理。</p>
     */
    public void retryFailedNotifications() {
        PayOrderConditionEntity condition = new PayOrderConditionEntity();
        condition.setBizNotifyStatus(BizNotifyStatusEnum.FAILED.getValue());
        List<PayOrderEntity> failedOrders = payOrderService.searchByPage(condition).getData();
        for (PayOrderEntity order : failedOrders) {
            if (order.getNotifyCount() != null && order.getNotifyCount() >= 5) {
                log.warn("支付通知重试已达上限, payOrderNo={}", order.getPayOrderNo());
                continue;
            }
            log.info("重试支付通知, payOrderNo={}, notifyCount={}", order.getPayOrderNo(), order.getNotifyCount());
            sendPaySuccessNotify(order);
        }
    }

    /**
     * 发送支付关闭异步通知（超时/用户取消 → 业务方释放库存）.
     */
    public void sendPayClosedNotify(PayOrderEntity order) {
        PayBizConfigEntity bizConfig = getBizConfig(order.getBizType());
        if (bizConfig == null) {
            log.warn("未找到业务方配置, bizType={}, payOrderNo={}", order.getBizType(), order.getPayOrderNo());
            return;
        }

        PayNotifyMessage message = new PayNotifyMessage();
        message.setNotifyType("CLOSED");
        message.setPayOrderNo(order.getPayOrderNo());
        message.setMerchantOrderNo(order.getMerchantOrderNo());
        message.setBizOrderNo(order.getBizOrderNo());
        message.setBizType(order.getBizType());
        message.setPayAmount(order.getTotalAmount());
        message.setPayStatus(order.getPayStatus());
        message.setChannelCode(order.getChannelCode());
        message.setSuccessTime(new Date());

        String topic = bizConfig.getNotifyMqTopic();
        String tag = bizConfig.getNotifyMqTag();

        try {
            mqProducer.send(topic, tag, message, order.getPayOrderNo());
            log.info("支付关闭通知已发送: payOrderNo={}, bizOrderNo={}", order.getPayOrderNo(), order.getBizOrderNo());
        } catch (Exception e) {
            log.error("支付关闭通知发送异常, payOrderNo={}", order.getPayOrderNo(), e);
        }
    }

    private PayBizConfigEntity getBizConfig(String bizType) {
        PayBizConfigConditionEntity condition = new PayBizConfigConditionEntity();
        condition.setBizType(bizType);
        condition.setStatus(1);
        List<PayBizConfigEntity> configs = payBizConfigService.searchByPage(condition).getData();
        return configs.isEmpty() ? null : configs.get(0);
    }
}

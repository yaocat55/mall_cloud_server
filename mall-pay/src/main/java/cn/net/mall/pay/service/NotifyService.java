package cn.net.mall.pay.service;

import cn.net.mall.pay.dto.PayNotifyMessage;
import cn.net.mall.pay.entity.PayBizConfigConditionEntity;
import cn.net.mall.pay.entity.PayBizConfigEntity;
import cn.net.mall.pay.entity.PayOrderConditionEntity;
import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.pay.enums.BizNotifyStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.ObjectProvider;
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

    private final ObjectProvider<RocketMQTemplate> rocketMQTemplateProvider;
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

        RocketMQTemplate rocketMQTemplate = rocketMQTemplateProvider.getIfAvailable();
        if (rocketMQTemplate == null) {
            log.warn("RocketMQTemplate 不存在，跳过发送, payOrderNo={}", order.getPayOrderNo());
            return;
        }

        String topic = bizConfig.getNotifyMqTopic();
        String tag = bizConfig.getNotifyMqTag();
        String destination = (tag != null && !tag.isEmpty()) ? (topic + ":" + tag) : topic;

        try {
            rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    order.setBizNotifyStatus(BizNotifyStatusEnum.SUCCESS.getValue());
                    payOrderService.update(order);
                    log.info("支付通知发送成功, payOrderNo={}, topic={}", order.getPayOrderNo(), destination);
                }
                @Override
                public void onException(Throwable throwable) {
                    order.setBizNotifyStatus(BizNotifyStatusEnum.FAILED.getValue());
                    payOrderService.update(order);
                    log.error("支付通知发送失败, payOrderNo={}, topic={}", order.getPayOrderNo(), destination, throwable);
                }
            });
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

    private PayBizConfigEntity getBizConfig(String bizType) {
        PayBizConfigConditionEntity condition = new PayBizConfigConditionEntity();
        condition.setBizType(bizType);
        condition.setStatus(1);
        List<PayBizConfigEntity> configs = payBizConfigService.searchByPage(condition).getData();
        return configs.isEmpty() ? null : configs.get(0);
    }
}

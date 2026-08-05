package cn.net.mall.pay.job;

import cn.net.mall.pay.service.NotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * MQ 通知补偿定时调度.
 *
 * <p>每分钟扫描 biz_notify_status=2（通知失败）的支付单，退避重推。
 * 重试间隔 1min/5min/15min/1h，最多 5 次，超过则标记人工处理。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyCompensateJob {

    private final NotifyService notifyService;

    @Scheduled(cron = "${mall.pay.notify-compensate.cron:0 */1 * * * ?}")
    public void retryFailedNotifications() {
        log.info("MQ 通知补偿任务启动");
        try {
            notifyService.retryFailedNotifications();
        } catch (Exception e) {
            log.error("MQ 通知补偿任务异常", e);
        }
        log.info("MQ 通知补偿任务完成");
    }
}

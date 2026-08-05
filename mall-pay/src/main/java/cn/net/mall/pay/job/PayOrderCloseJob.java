package cn.net.mall.pay.job;

import cn.net.mall.pay.service.PayCoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 超时关单定时调度.
 *
 * <p>每 N 分钟扫描 expire_time 过期的待支付订单，关闭并通知业务方释放资源。
 * cron 可通过 Nacos 配置 mall.pay.order.close-scan-cron 覆盖，默认每 10 分钟。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PayOrderCloseJob {

    private final PayCoreService payCoreService;

    @Scheduled(cron = "${mall.pay.order.close-scan-cron:0 */10 * * * ?}")
    public void closeExpiredOrders() {
        log.info("超时关单定时任务启动");
        try {
            payCoreService.closeExpiredOrders();
        } catch (Exception e) {
            log.error("超时关单定时任务异常", e);
        }
        log.info("超时关单定时任务完成");
    }
}

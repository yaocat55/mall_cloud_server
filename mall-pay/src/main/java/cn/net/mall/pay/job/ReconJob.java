package cn.net.mall.pay.job;

import cn.net.mall.pay.enums.ChannelCodeEnum;
import cn.net.mall.pay.service.ReconService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * 对账定时调度.
 *
 * <p>默认次日 10:30 触发（两家渠道次日 10 点后账单就绪）。
 * cron 可通过 Nacos 配置 {@code mall.pay.recon.cron} 覆盖。</p>
 *
 * <p>跳过周日（财务系统关闭，前一日的账单不生成）。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReconJob {

    private final ReconService reconService;

    /**
     * 日终对账 — 次日 10:30 执行（默认 cron，Nacos 可覆盖）.
     *
     * <p>遍历所有真实渠道（排除 MOCK），逐一对昨天的交易对账。</p>
     */
    @Scheduled(cron = "${mall.pay.recon.cron:0 30 10 * * ?}")
    public void dailyRecon() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 周日跳过：前一日为周六，财务/支付平台停业，通常无账单
        if (yesterday.getDayOfWeek() == DayOfWeek.SUNDAY) {
            log.info("对账跳过: tradeDate={} 为周日，财务关闭无账单", yesterday);
            return;
        }

        log.info("对账定时任务启动: tradeDate={}", yesterday);

        for (ChannelCodeEnum channel : ChannelCodeEnum.realChannels()) {
            try {
                reconService.reconcile(channel.getCode(), yesterday);
            } catch (Exception e) {
                log.error("对账失败: channelCode={}, tradeDate={}", channel.getCode(), yesterday, e);
            }
        }

        log.info("对账定时任务完成: tradeDate={}", yesterday);
    }
}

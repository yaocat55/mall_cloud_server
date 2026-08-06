package cn.net.mall.pay.service.recon;

import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.pay.mapper.PayOrderMapper;
import cn.net.mall.pay.mapper.PayRefundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 单库版 {@link PlatformDataQuery} 实现.
 *
 * <p>直接查 pay_order / pay_refund 表，适用于当前未分库阶段。</p>
 * <p>未来分库分表后，替换为此接口的新实现即可，对账逻辑无需改动。</p>
 */
@Component
@RequiredArgsConstructor
public class DefaultPlatformDataQuery implements PlatformDataQuery {

    private final PayOrderMapper payOrderMapper;
    private final PayRefundMapper payRefundMapper;

    @Override
    public PlatformSummary sumPayments(String channelCode, LocalDate tradeDate, int bufferHours) {
        Date startTime = localDateToDate(tradeDate);
        Date endTime = localDateToDate(tradeDate.plusDays(1).atTime(bufferHours, 0));
        Map<String, Object> sum = payOrderMapper.sumByChannelAndSuccessTime(channelCode, startTime, endTime);
        long count = ((Number) sum.getOrDefault("platform_count", 0)).longValue();
        long total = ((Number) sum.getOrDefault("platform_total_amount", 0)).longValue();
        return new PlatformSummary(count, total);
    }

    @Override
    public PlatformSummary sumRefunds(String channelCode, LocalDate tradeDate, int bufferHours) {
        Date startTime = localDateToDate(tradeDate);
        Date endTime = localDateToDate(tradeDate.plusDays(1).atTime(bufferHours, 0));
        Map<String, Object> sum = payRefundMapper.sumByChannelAndSuccessTime(channelCode, startTime, endTime);
        long count = ((Number) sum.getOrDefault("refund_count", 0)).longValue();
        long total = ((Number) sum.getOrDefault("refund_total_amount", 0)).longValue();
        return new PlatformSummary(count, total);
    }

    @Override
    public List<PayOrderEntity> loadPayments(String channelCode, LocalDate tradeDate, int bufferHours) {
        Date startTime = localDateToDate(tradeDate);
        Date endTime = localDateToDate(tradeDate.plusDays(1).atTime(bufferHours, 0));
        return payOrderMapper.selectByChannelAndSuccessTime(channelCode, startTime, endTime);
    }

    // ============ private ============

    private Date localDateToDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.of("Asia/Shanghai")).toInstant());
    }

    private Date localDateToDate(LocalDateTime dt) {
        return Date.from(dt.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
    }
}

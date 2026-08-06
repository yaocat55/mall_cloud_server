package cn.net.mall.pay.service;

import cn.net.mall.pay.channel.PayChannelStrategy;
import cn.net.mall.pay.dto.BillRow;
import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.pay.entity.ReconBatchConditionEntity;
import cn.net.mall.pay.entity.ReconBatchEntity;
import cn.net.mall.pay.entity.ReconResultEntity;
import cn.net.mall.pay.entity.ReconTempEntity;
import cn.net.mall.pay.mapper.ReconBatchMapper;
import cn.net.mall.pay.service.recon.PlatformDataQuery;
import cn.net.mall.pay.service.recon.PlatformSummary;
import cn.net.mall.pay.mapper.ReconResultMapper;
import cn.net.mall.pay.mapper.ReconTempMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 对账核心引擎.
 *
 * <p>日终对账流程：下载渠道账单 → 解析 CSV → 灌入临时表 → 总额校验（O(1)）
 * → 若不等，HashMap 内存撮合 → 差异写 recon_result。</p>
 *
 * <p>两层校验设计（见 docs/44）：</p>
 * <ol>
 *   <li>总额校验 — 绝大多数情况下对平，直接关闭批次</li>
 *   <li>逐笔对比 — 仅在总额不等时进入，HashMap 撮合不 JOIN</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReconService {

    private final PayChannelService payChannelService;
    private final ReconBatchMapper reconBatchMapper;
    private final ReconTempMapper reconTempMapper;
    private final ReconResultMapper reconResultMapper;
    private final PlatformDataQuery platformDataQuery;

    /** 对账日缓冲小时数（次日 06:00 前的交易归入对账日） */
    @Value("${mall.pay.recon.buffer-hours:6}")
    private int bufferHours;

    /**
     * 对指定渠道执行日终对账.
     *
     * @param channelCode 渠道编码 ALIPAY / WECHAT_PAY
     * @param tradeDate   对账日（哪天交易的）
     */
    public void reconcile(String channelCode, LocalDate tradeDate) {
        log.info("开始对账: channelCode={}, tradeDate={}", channelCode, tradeDate);

        // Step 0: 幂等检查 — 当天是否已有批次
        if (hasTodayBatch(channelCode, tradeDate)) {
            log.info("渠道 {} 对账日 {} 已有对账批次，跳过（幂等）", channelCode, tradeDate);
            return;
        }

        // Step 1: 创建批次
        String batchNo = generateBatchNo(channelCode, tradeDate);
        ReconBatchEntity batch = createBatch(batchNo, channelCode, tradeDate);

        try {
            PayChannelStrategy strategy = payChannelService.getStrategy(channelCode);
            var config = payChannelService.getConfig(channelCode);

            // Step 2: 下载对账单
            byte[] billContent = strategy.downloadBill(tradeDate, config);
            if (billContent == null || billContent.length == 0) {
                markBatchFailed(batch, "对账单下载为空（可能是周日/节假日无账单）");
                return;
            }

            // Step 3: 解析 CSV → BillRow 列表
            List<BillRow> billRows = strategy.parseBill(billContent);
            if (billRows == null || billRows.isEmpty()) {
                markBatchFailed(batch, "对账单解析结果为空");
                return;
            }

            // Step 4: 批量灌入 recon_temp
            batchInsertTemp(batchNo, billRows);
            batch.setStatus(2); // 已解析入库
            reconBatchMapper.update(batch);

            // Step 5: 总额校验
            boolean matched = totalCheck(batch, channelCode, tradeDate);

            if (matched) {
                // 对平 ✅
                batch.setStatus(4); // 对账完成
                batch.setEndTime(new Date());
                reconBatchMapper.update(batch);
                log.info("对账完成（对平）: batchNo={}, channelCode={}, tradeDate={}", batchNo, channelCode, tradeDate);
            } else {
                // 有差异 ⚠️ 进入逐笔对比
                batch.setStatus(3); // 对账中
                reconBatchMapper.update(batch);
                detailCompare(batch, channelCode, tradeDate);
                batch.setStatus(5); // 有差异待处理
                batch.setEndTime(new Date());
                reconBatchMapper.update(batch);
                log.info("对账完成（有差异）: batchNo={}, channelCode={}, diffCount={}",
                        batchNo, channelCode, batch.getDiffCount());
            }

        } catch (Exception e) {
            log.error("对账异常: batchNo={}", batchNo, e);
            markBatchFailed(batch, "对账异常: " + e.getMessage());
        }
    }

    // ============ 总额校验 ============

    /**
     * 第一级校验：渠道侧 SUM(income) vs 平台侧 SUM(pay_amount)。
     *
     * @return true=对平，false=有差异
     */
    boolean totalCheck(ReconBatchEntity batch, String channelCode, LocalDate tradeDate) {
        // 渠道侧：COUNT(*), SUM(income) from recon_temp
        Map<String, Object> channelSum = reconTempMapper.sumByBatchNo(batch.getBatchNo());
        long channelCount = ((Number) channelSum.getOrDefault("channel_count", 0)).longValue();
        long channelTotal = ((Number) channelSum.getOrDefault("channel_total_amount", 0)).longValue();

        // 渠道侧退款项：filter tradeType=REFUND, SUM(income)
        Map<String, Object> channelRefundMap = reconTempMapper.sumRefundByBatchNo(batch.getBatchNo());
        long channelRefundTotal = -((Number) channelRefundMap.getOrDefault("refund_total_amount", 0)).longValue();

        // 平台侧：SUM(pay_amount) - SUM(refund_amount)
        PlatformSummary platformSum = platformDataQuery.sumPayments(channelCode, tradeDate, bufferHours);
        long platformCount = platformSum.getCount();
        long platformTotal = platformSum.getTotalAmount();

        // cross-validate with pay_refund
        PlatformSummary refundSum = platformDataQuery.sumRefunds(channelCode, tradeDate, bufferHours);
        long refundTotal = refundSum.getTotalAmount();

        // 回写批次
        batch.setChannelCount((int) channelCount);
        batch.setChannelTotalAmount(channelTotal);
        batch.setPlatformCount((int) platformCount);
        batch.setPlatformTotalAmount(platformTotal);
        reconBatchMapper.update(batch);

        log.info("总额校验: channelCount={}, channelTotal={}分, platformCount={}, platformTotal={}分",
                channelCount, channelTotal, platformCount, platformTotal);
        // 提取出 pay_order.refund_amount for this batch
        long platformRefundAmount = 0L;
        if (platformTotal < 0) {
            platformRefundAmount = -platformTotal;
        }

        log.info("refund-count cross-check: pay_order.refund_amount={}分, pay_refund_total={}分, channel_refund_total={}分",
                platformRefundAmount, refundTotal, channelRefundTotal);

        // 总额 + 笔数都一致才算对平
        return channelCount == platformCount && channelTotal == platformTotal;
    }

    // ============ 逐笔对比 ============

    /**
     * 第二级校验：HashMap 内存撮合，不 JOIN。
     */
    void detailCompare(ReconBatchEntity batch, String channelCode, LocalDate tradeDate) {
        // ① 渠道侧全量加载（同一 tradeNo 可能有多行：支付+退款，需合并 income）
        List<ReconTempEntity> tempList = reconTempMapper.selectByBatchNo(batch.getBatchNo());
        Map<String, Long> channelIncomeMap = new HashMap<>(tempList.size());
        Map<String, ReconTempEntity> channelFirstRow = new HashMap<>(tempList.size());
        for (ReconTempEntity t : tempList) {
            if (t.getTradeNo() == null || t.getTradeNo().isEmpty()) continue;
            channelIncomeMap.merge(t.getTradeNo(),
                    t.getIncome() != null ? t.getIncome() : 0L, Long::sum);
            channelFirstRow.putIfAbsent(t.getTradeNo(), t);
        }

        // ② 平台侧全量加载（通过 PlatformDataQuery 接口，分库后可换实现）
        List<PayOrderEntity> orderList = platformDataQuery.loadPayments(channelCode, tradeDate, bufferHours);
        Map<String, Long> platformAmountMap = new HashMap<>(orderList.size());
        Map<String, PayOrderEntity> platformFirstRow = new HashMap<>(orderList.size());
        for (PayOrderEntity o : orderList) {
            if (o.getMerchantOrderNo() == null || o.getMerchantOrderNo().isEmpty()) continue;
            // 平台侧净额 = payAmount - refundAmount，与渠道侧 income（支付减退款）口径对齐
            long payAmt = o.getPayAmount() != null ? o.getPayAmount() : 0L;
            long refundAmt = o.getRefundAmount() != null ? o.getRefundAmount() : 0L;
            platformAmountMap.merge(o.getMerchantOrderNo(), payAmt - refundAmt, Long::sum);
            platformFirstRow.putIfAbsent(o.getMerchantOrderNo(), o);
        }

        List<ReconResultEntity> diffs = new ArrayList<>();

        // ③ 遍历渠道侧
        for (Map.Entry<String, Long> entry : channelIncomeMap.entrySet()) {
            String tradeNo = entry.getKey();
            Long channelSum = entry.getValue();
            Long platformSum = platformAmountMap.get(tradeNo);

            if (platformSum == null) {
                // 渠道有单、平台无 → 回调丢失
                ReconResultEntity diff = buildDiff(batch, channelFirstRow.get(tradeNo), null,
                        "ONLY_CHANNEL", channelSum, null);
                diffs.add(diff);
            } else if (!amountEqual(channelSum, platformSum)) {
                // 金额不一致
                ReconResultEntity diff = buildDiff(batch, channelFirstRow.get(tradeNo),
                        platformFirstRow.get(tradeNo), "AMOUNT_MISMATCH", channelSum, platformSum);
                diffs.add(diff);
            }
            // 金额一致：跳过（对平）
        }

        // ④ 遍历平台侧：找平台有、渠道无
        for (Map.Entry<String, Long> entry : platformAmountMap.entrySet()) {
            String tradeNo = entry.getKey();
            if (!channelIncomeMap.containsKey(tradeNo)) {
                Long platformSum = entry.getValue();
                ReconResultEntity diff = buildDiff(batch, null,
                        platformFirstRow.get(tradeNo), "ONLY_PLATFORM", null, platformSum);
                diffs.add(diff);
            }
        }

        // ⑤ 批量写结果
        int diffCount = diffs.size();
        long diffTotalAmount = diffs.stream()
                .mapToLong(d -> d.getDiffAmount() != null ? d.getDiffAmount() : 0L).sum();

        batch.setDiffCount(diffCount);
        batch.setDiffTotalAmount(diffTotalAmount);
        reconBatchMapper.update(batch);

        for (ReconResultEntity diff : diffs) {
            reconResultMapper.insert(diff);
        }

        log.info("逐笔对比完成: diffCount={}, diffTotalAmount={}分", diffCount, diffTotalAmount);
    }

    // ============ 辅助方法 ============

    /** 检查当天是否已有批次 */
    private boolean hasTodayBatch(String channelCode, LocalDate tradeDate) {
        ReconBatchConditionEntity condition = new ReconBatchConditionEntity();
        condition.setChannelCode(channelCode);
        condition.setTradeDate(localDateToDate(tradeDate));
        Integer count = reconBatchMapper.searchCount(condition);
        return count != null && count > 0;
    }

    /** 生成批次号 RECON{yyyyMMdd}{渠道}{4位随机} */
    private String generateBatchNo(String channelCode, LocalDate tradeDate) {
        String date = tradeDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix = UUID.randomUUID().toString().substring(0, 4);
        return "RECON" + date + channelCode + suffix;
    }

    /** 创建对账批次 */
    private ReconBatchEntity createBatch(String batchNo, String channelCode, LocalDate tradeDate) {
        ReconBatchEntity batch = new ReconBatchEntity();
        batch.setBatchNo(batchNo);
        batch.setChannelCode(channelCode);
        batch.setTradeDate(localDateToDate(tradeDate));
        batch.setStatus(1); // 已下载
        batch.setBeginTime(new Date());
        reconBatchMapper.insert(batch);
        log.info("创建对账批次: batchNo={}", batchNo);
        return batch;
    }

    /** 标记批次失败 */
    private void markBatchFailed(ReconBatchEntity batch, String errorMsg) {
        batch.setStatus(7); // 失败
        batch.setEndTime(new Date());
        batch.setErrorMsg(errorMsg != null && errorMsg.length() > 500
                ? errorMsg.substring(0, 500) : errorMsg);
        reconBatchMapper.update(batch);
        log.warn("对账失败: batchNo={}, errorMsg={}", batch.getBatchNo(), errorMsg);
    }

    /** 批量灌入 recon_temp */
    private void batchInsertTemp(String batchNo, List<BillRow> rows) {
        List<ReconTempEntity> entities = new ArrayList<>(rows.size());
        int lineNo = 0;
        for (BillRow row : rows) {
            lineNo++;
            ReconTempEntity entity = new ReconTempEntity();
            entity.setBatchNo(batchNo);
            entity.setLineNo(lineNo);
            entity.setTradeNo(row.getTradeNo());
            entity.setChannelTradeNo(row.getChannelTradeNo());
            entity.setRefundNo(row.getRefundNo());
            entity.setTradeTime(row.getTradeTime());
            entity.setTradeType(row.getTradeType());
            entity.setAmount(row.getAmount());
            entity.setFee(row.getFee());
            entity.setIncome(row.getIncome());
            entity.setTradeStatus(row.getTradeStatus());
            entity.setPayerAccount(row.getPayerAccount());
            entity.setExtJson(row.getExtJson());
            entity.setCreateTime(new Date());
            entities.add(entity);
        }
        reconTempMapper.batchInsert(entities);
        log.info("批量入库 recon_temp: batchNo={}, rows={}", batchNo, entities.size());
    }

    /** 构建差异记录（传入已合并的金额，避免逐笔对比逻辑重复计算） */
    private ReconResultEntity buildDiff(ReconBatchEntity batch, ReconTempEntity channel,
                                        PayOrderEntity platform, String diffType,
                                        Long channelAmount, Long platformAmount) {
        ReconResultEntity result = new ReconResultEntity();
        result.setBatchNo(batch.getBatchNo());
        result.setChannelCode(batch.getChannelCode());
        result.setDiffType(diffType);
        result.setHandleStatus(0); // 待处理

        if (channel != null) {
            result.setTradeNo(channel.getTradeNo());
            result.setChannelTradeNo(channel.getChannelTradeNo());
            result.setTradeTime(channel.getTradeTime());
            result.setChannelAmount(channelAmount);
        }
        if (platform != null) {
            result.setPayOrderId(platform.getId());
            result.setPayOrderNo(platform.getPayOrderNo());
            result.setPlatformAmount(platformAmount);
        }

        // 差异金额 = 平台侧 - 渠道侧
        result.setDiffAmount((platformAmount != null ? platformAmount : 0L)
                - (channelAmount != null ? channelAmount : 0L));
        return result;
    }

    /** 渠道侧净入账 = income（退款行 income 可能为负）。未使用，保留供将来的费用对账使用。 */
    private Long sumIncome(ReconTempEntity channel) {
        if (channel == null) return 0L;
        return channel.getIncome() != null ? channel.getIncome() : 0L;
    }

    /** 金额比较（null 安全） */
    private boolean amountEqual(Long a, Long b) {
        long va = a != null ? a : 0L;
        long vb = b != null ? b : 0L;
        return va == vb;
    }

    /** LocalDate → Date（东八区 00:00:00） */
    private Date localDateToDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.of("Asia/Shanghai")).toInstant());
    }

    /** LocalDateTime → Date */
    private Date localDateToDate(LocalDateTime dt) {
        return Date.from(dt.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
    }
}

package cn.net.mall.pay.service.recon;

import java.time.LocalDate;

/**
 * 对账平台侧数据查询接口.
 *
 * <p>当前默认实现直接查 pay_order / pay_refund 表（单库）。</p>
 * <p>未来分库分表后，替代实现可：</p>
 * <ul>
 *   <li>遍历各分片查询再聚合</li>
 *   <li>改为查只读副本/TiDB 汇总库</li>
 *   <li>改为查 CDC 汇聚的独立对账库</li>
 * </ul>
 */
public interface PlatformDataQuery {

    /**
     * 按渠道 + 成功时间统计平台侧支付数据（总额校验用）.
     *
     * @return 包含 {@code platform_count} 和 {@code platform_total_amount} 的 map
     */
    PlatformSummary sumPayments(String channelCode, LocalDate tradeDate, int bufferHours);

    /**
     * 按渠道 + 成功时间统计平台侧退款数据（退款交叉验证用）.
     *
     * @return 包含 {@code refund_count} 和 {@code refund_total_amount} 的 map
     */
    PlatformSummary sumRefunds(String channelCode, LocalDate tradeDate, int bufferHours);

    /**
     * 按渠道 + 成功时间加载平台侧支付订单列表（逐笔比对撮合用）.
     */
    java.util.List<cn.net.mall.pay.entity.PayOrderEntity> loadPayments(String channelCode, LocalDate tradeDate, int bufferHours);
}

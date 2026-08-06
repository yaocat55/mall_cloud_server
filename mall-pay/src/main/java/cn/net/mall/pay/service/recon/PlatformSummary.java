package cn.net.mall.pay.service.recon;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 平台侧汇总数据（支付/退款统用）.
 */
@Data
@AllArgsConstructor
public class PlatformSummary {

    /** 笔数 */
    private long count;

    /** 总金额（分） */
    private long totalAmount;
}

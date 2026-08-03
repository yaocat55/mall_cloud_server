package cn.net.mall.pay.support;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额换算工具（分/元）.
 *
 * <p>支付库统一以「分」为存储单位（bigint）。</p>
 * <ul>
 *   <li>元 → 分：用于解析渠道账单（微信账单金额是元）、支付宝展示</li>
 *   <li>分 → 元：用于提交支付宝（total_amount 单位是元，字符串），微信侧原样为分</li>
 * </ul>
 * <p>换算只发生在此处，业务层一律用分。</p>
 */
public final class MoneyUtil {

    private MoneyUtil() {
    }

    /**
     * 元（字符串，如 "399.00"）→ 分（Long）
     */
    public static Long yuanToFen(String yuan) {
        if (yuan == null || yuan.isBlank()) {
            return 0L;
        }
        return new BigDecimal(yuan).multiply(new BigDecimal(100))
                .setScale(0, RoundingMode.HALF_UP).longValue();
    }

    /**
     * 分（Long）→ 元（字符串，如 "399.00"）
     */
    public static String fenToYuan(Long fen) {
        if (fen == null) {
            return "0.00";
        }
        return new BigDecimal(fen).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)
                .toPlainString();
    }
}

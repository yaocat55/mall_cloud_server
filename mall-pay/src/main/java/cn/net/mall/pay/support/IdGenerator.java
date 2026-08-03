package cn.net.mall.pay.support;

import cn.net.mall.workid.IdGenerateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ID 生成器（复用 common-workid 雪花算法）.
 *
 * <p>生成 pay_order 主键、merchant_order_no（提交渠道的商户订单号）、refund_no 等。</p>
 * <p>merchant_order_no 由支付服务生成（雪花派生），满足渠道格式约束（微信 6-32 位字母数字）。</p>
 */
@Component
public class IdGenerator {

    @Autowired
    private IdGenerateHelper idGenerateHelper;

    /**
     * 雪花主键 ID
     */
    public Long nextId() {
        return idGenerateHelper.nextId();
    }

    /**
     * 支付单号（对外交易号）
     */
    public String nextPayOrderNo() {
        return String.valueOf(idGenerateHelper.nextId());
    }

    /**
     * 渠道商户订单号（提交给支付宝/微信的订单号）
     * 前缀 M 固定，保证 6-32 位字母数字，微信侧兼容
     */
    public String nextMerchantOrderNo() {
        return "M" + idGenerateHelper.nextId();
    }

    /**
     * 退款单号
     */
    public String nextRefundNo() {
        return String.valueOf(idGenerateHelper.nextId());
    }

    /**
     * 对账批次号：RECON{yyyyMMdd}{渠道}{序号}
     */
    public String nextBatchNo(String channelCode, String dateStr, int seq) {
        return "RECON" + dateStr + channelCode + seq;
    }
}

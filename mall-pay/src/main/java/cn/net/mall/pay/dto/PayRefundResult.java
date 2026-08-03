package cn.net.mall.pay.dto;

import lombok.Data;

/**
 * 渠道退款申请结果.
 */
@Data
public class PayRefundResult {

    /** 我方退款单号（refund_no） */
    private String refundNo;

    /** 渠道退款单号（channel_refund_no） */
    private String channelRefundNo;

    /** 是否成功 */
    private boolean success;

    /** 失败原因 */
    private String errMsg;
}

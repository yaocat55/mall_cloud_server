package cn.net.mall.pay.dto;

import lombok.Data;

/**
 * 渠道退款结果查询.
 */
@Data
public class PayRefundQueryResult {

    /** 我方退款单号 */
    private String refundNo;

    /** 渠道退款单号 */
    private String channelRefundNo;

    /** 退款状态：SUCCESS / PROCESSING / FAILED */
    private String status;

    /** 是否退款成功 */
    private boolean success;
}

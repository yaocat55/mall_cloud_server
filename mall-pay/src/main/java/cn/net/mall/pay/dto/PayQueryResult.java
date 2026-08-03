package cn.net.mall.pay.dto;

import lombok.Data;

/**
 * 渠道主动查询订单状态结果（兜底轮询用）.
 */
@Data
public class PayQueryResult {

    /** 商户订单号（= merchant_order_no） */
    private String merchantOrderNo;

    /** 渠道交易号 */
    private String channelTradeNo;

    /** 是否支付成功 */
    private boolean success;

    /** 渠道侧实际支付金额（分，成功时才有值） */
    private Long payAmount;

    /** 支付成功时间（渠道侧） */
    private java.util.Date successTime;

    /** 原始响应（排查用） */
    private String rawResponse;
}

package cn.net.mall.pay.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

/**
 * 渠道对账单统一账单行结构.
 *
 * <p>各渠道对账单字段不同，解析策略统一归一化为本结构，写入 recon_temp。</p>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillRow {

    /** 渠道商户订单号（= merchant_order_no，对账匹配键） */
    private String tradeNo;

    /** 渠道交易流水号 */
    private String channelTradeNo;

    /** 渠道退款单号（退款记录行才有） */
    private String refundNo;

    /** 交易时间 */
    private Date tradeTime;

    /** 交易类型：PAY 支付 / REFUND 退款 */
    private String tradeType;

    /** 交易金额（分，正数） */
    private Long amount;

    /** 手续费（分） */
    private Long fee;

    /** 净入账金额（分，amount - fee，可正可负，退款记负） */
    private Long income;

    /** 渠道交易状态：SUCCESS / REFUND / CLOSED */
    private String tradeStatus;

    /** 付款方账号 */
    private String payerAccount;

    /** 渠道特有字段（原样保留） */
    private String extJson;
}

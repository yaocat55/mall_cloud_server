package cn.net.mall.pay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 渠道下单返回的拉起支付参数.
 *
 * <p>支付宝返回 orderStr 字符串；微信返回 prepayId；MOCK 返回预设参数。</p>
 */
@Data
@Schema(description = "渠道下单结果")
public class PayPrepayResult {

    /** 渠道编码：ALIPAY / WECHAT_PAY / WECHAT_MINI / MOCK */
    @Schema(description = "渠道编码")
    private String channelCode;

    /** 拉起支付参数（支付宝 orderStr / 微信 prepay 参数串 / MOCK 固定串） */
    @Schema(description = "拉起支付参数")
    private String prepayParams;

    /** 渠道交易号（MOCK 或部分渠道下单即返回时非空，回调成功后回填 pay_order.channel_trade_no） */
    @Schema(description = "渠道交易号")
    private String channelTradeNo;

    /** 是否成功 */
    @Schema(description = "是否成功")
    private boolean success;

    /** 失败原因（success=false 时有值） */
    @Schema(description = "失败原因")
    private String errMsg;
}

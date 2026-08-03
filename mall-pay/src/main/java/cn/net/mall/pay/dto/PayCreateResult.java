package cn.net.mall.pay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建支付单结果.
 *
 * <p>返回 payOrderNo（供后续查询）+ 渠道拉起支付参数（前端调起 SDK）。</p>
 */
@Data
@Schema(description = "创建支付单结果")
public class PayCreateResult {

    /** 支付订单号（对外交易号） */
    @Schema(description = "支付订单号")
    private String payOrderNo;

    /** 渠道商户订单号 */
    @Schema(description = "渠道商户订单号")
    private String merchantOrderNo;

    /** 渠道编码 */
    @Schema(description = "渠道编码")
    private String channelCode;

    /** 拉起支付参数（支付宝 orderStr / 微信 prepay 参数串 / MOCK 预设串） */
    @Schema(description = "拉起支付参数")
    private String prepayParams;

    /** 支付状态：10待支付 */
    @Schema(description = "支付状态")
    private Integer payStatus;
}

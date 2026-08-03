package cn.net.mall.pay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 支付结果通知消息体（MQ 异步通知业务方用）.
 *
 * <p>支付/退款结果由 pay 服务发 MQ 通知业务方（如 mall-order），业务方消费后更新自己的业务订单。</p>
 */
@Data
@Schema(description = "支付结果通知消息体")
public class PayNotifyMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 支付订单号 */
    @Schema(description = "支付订单号")
    private String payOrderNo;

    /** 渠道商户订单号 */
    @Schema(description = "渠道商户订单号")
    private String merchantOrderNo;

    /** 业务方订单号 */
    @Schema(description = "业务方订单号")
    private String bizOrderNo;

    /** 业务类型：MALL_ORDER / OTHER */
    @Schema(description = "业务类型")
    private String bizType;

    /** 实付金额（分） */
    @Schema(description = "实付金额（分）")
    private Long payAmount;

    /** 支付状态：10待支付 20已支付 30已关闭 40支付失败 */
    @Schema(description = "支付状态")
    private Integer payStatus;

    /** 退款单号（退款通知时非空） */
    @Schema(description = "退款单号")
    private String refundNo;

    /** 退款状态（退款通知时非空）：0待处理 1处理中 2成功 3失败 */
    @Schema(description = "退款状态")
    private Integer refundStatus;

    /** 渠道编码 */
    @Schema(description = "渠道编码")
    private String channelCode;

    /** 成功时间 */
    @Schema(description = "成功时间")
    private Date successTime;

    /** 通知类型：PAY 支付 / REFUND 退款 */
    @Schema(description = "通知类型")
    private String notifyType;
}

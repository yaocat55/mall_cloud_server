package cn.net.mall.pay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建支付单入参.
 *
 * <p>由业务方（mall-order）下单成功后调用，字段见 docs/37 2.2 节「支付服务对业务方的最少依赖字段」。</p>
 * <p>必须字段：bizOrderNo / bizType / userId / totalAmount / channelCode；subject 缺失时用 bizOrderNo 兜底。</p>
 */
@Data
@Schema(description = "创建支付单入参")
public class PayCreateDTO {

    /** 业务方订单号（如商城 trade.code） */
    @NotBlank(message = "业务方订单号不能为空")
    @Schema(description = "业务方订单号", example = "TC202408010001")
    private String bizOrderNo;

    /** 业务类型：MALL_ORDER / OTHER */
    @NotBlank(message = "业务类型不能为空")
    @Schema(description = "业务类型", example = "MALL_ORDER")
    private String bizType;

    /** 支付渠道：ALIPAY / WECHAT_PAY / WECHAT_MINI / MOCK */
    @NotBlank(message = "支付渠道不能为空")
    @Schema(description = "支付渠道", example = "MOCK")
    private String channelCode;

    /** 发起支付的用户 ID */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "10086")
    private Long userId;

    /** 应付金额（分），以用户实际要付的钱为准 */
    @NotNull(message = "金额不能为空")
    @Schema(description = "应付金额（分）", example = "39900")
    private Long totalAmount;

    /** 商品描述（收银台展示），缺失时支付服务用 bizOrderNo 兜底 */
    @Schema(description = "商品描述", example = "商城订单 TC202408010001")
    private String subject;

    /** 微信 openId（小程序支付必填，其他渠道可空） */
    @Schema(description = "微信 openId（小程序支付必填）")
    private String openId;

    /** 客户端 IP（部分渠道风控要求） */
    @Schema(description = "客户端IP", example = "10.0.0.1")
    private String clientIp;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}

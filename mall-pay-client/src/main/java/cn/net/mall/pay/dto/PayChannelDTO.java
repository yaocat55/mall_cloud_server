package cn.net.mall.pay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 可用支付渠道信息（前端收银台渲染用）.
 *
 * <p>只暴露展示所需字段，不含任何密钥（appSecret/privateKey 等）。</p>
 */
@Data
@Schema(description = "可用支付渠道信息")
public class PayChannelDTO {

    /** 渠道编码：ALIPAY / WECHAT_PAY / WECHAT_MINI / MOCK */
    @Schema(description = "渠道编码", example = "ALIPAY")
    private String channelCode;

    /** 渠道名称（收银台展示） */
    @Schema(description = "渠道名称", example = "支付宝")
    private String channelName;

    /** 渠道说明/场景（如 微信小程序支付） */
    @Schema(description = "渠道说明")
    private String description;
}

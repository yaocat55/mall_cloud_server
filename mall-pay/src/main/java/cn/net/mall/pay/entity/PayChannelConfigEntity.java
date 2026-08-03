package cn.net.mall.pay.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 支付渠道配置实体
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Schema(description = "支付渠道配置实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class PayChannelConfigEntity extends BaseEntity {


	/**
	 * 渠道编码：ALIPAY / WECHAT_PAY / WECHAT_MINI / MOCK
	 */
	@Schema(description = "渠道编码：ALIPAY / WECHAT_PAY / WECHAT_MINI / MOCK")
	private String channelCode;

	/**
	 * 渠道名称
	 */
	@Schema(description = "渠道名称")
	private String channelName;

	/**
	 * 应用ID/AppID
	 */
	@Schema(description = "应用ID/AppID")
	private String appId;

	/**
	 * 商户号/Partner
	 */
	@Schema(description = "商户号/Partner")
	private String merchantId;

	/**
	 * AppSecret（AES 加密存储）
	 */
	@Schema(description = "AppSecret（AES 加密存储）")
	private String appSecret;

	/**
	 * 渠道公钥（AES 加密存储）
	 */
	@Schema(description = "渠道公钥（AES 加密存储）")
	private String publicKey;

	/**
	 * 商户私钥/APIv3密钥（AES 加密存储）
	 */
	@Schema(description = "商户私钥/APIv3密钥（AES 加密存储）")
	private String privateKey;

	/**
	 * 支付回调地址
	 */
	@Schema(description = "支付回调地址")
	private String notifyUrl;

	/**
	 * 退款回调地址
	 */
	@Schema(description = "退款回调地址")
	private String refundNotifyUrl;

	/**
	 * 对账单下载地址模板
	 */
	@Schema(description = "对账单下载地址模板")
	private String billDownloadUrl;

	/**
	 * 扩展配置（签约模式/商户证书路径等）
	 */
	@Schema(description = "扩展配置（签约模式/商户证书路径等）")
	private String configJson;

	/**
	 * 状态：1启用 0禁用
	 */
	@Schema(description = "状态：1启用 0禁用")
	private Integer status;

	/**
	 * 乐观锁
	 */
	@Schema(description = "乐观锁")
	private Integer version;

}

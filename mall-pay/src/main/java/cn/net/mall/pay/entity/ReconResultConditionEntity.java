package cn.net.mall.pay.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.net.mall.entity.RequestConditionEntity;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 对账逐笔结果查询条件实体
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Schema(description = "查询条件实体")
@EqualsAndHashCode(callSuper = true)
@Data
public class ReconResultConditionEntity extends RequestConditionEntity {


	/**
	 *  主键，雪花ID
     */
	@Schema(description = "主键，雪花ID")
	private Long id;

	/**
	 *  对账批次号
     */
	@Schema(description = "对账批次号")
	private String batchNo;

	/**
	 *  渠道编码
     */
	@Schema(description = "渠道编码")
	private String channelCode;

	/**
	 *  渠道商户订单号
     */
	@Schema(description = "渠道商户订单号")
	private String tradeNo;

	/**
	 *  渠道交易号
     */
	@Schema(description = "渠道交易号")
	private String channelTradeNo;

	/**
	 *  本地支付订单ID
     */
	@Schema(description = "本地支付订单ID")
	private Long payOrderId;

	/**
	 *  本地支付订单号
     */
	@Schema(description = "本地支付订单号")
	private String payOrderNo;

	/**
	 *  交易时间
     */
	@Schema(description = "交易时间")
	private Date tradeTime;

	/**
	 *  平台侧金额（分，退款记负）
     */
	@Schema(description = "平台侧金额（分，退款记负）")
	private Long platformAmount;

	/**
	 *  渠道侧金额（分，退款记负）
     */
	@Schema(description = "渠道侧金额（分，退款记负）")
	private Long channelAmount;

	/**
	 *  差异类型：LONG_PAYMENT/SHORT_PAYMENT/AMOUNT_MISMATCH/ONLY_PLATFORM/ONLY_CHANNEL/FEE_MISMATCH/STATUS_MISMATCH
     */
	@Schema(description = "差异类型：LONG_PAYMENT/SHORT_PAYMENT/AMOUNT_MISMATCH/ONLY_PLATFORM/ONLY_CHANNEL/FEE_MISMATCH/STATUS_MISMATCH")
	private String diffType;

	/**
	 *  差异金额（分，带方向）
     */
	@Schema(description = "差异金额（分，带方向）")
	private Long diffAmount;

	/**
	 *  处理状态：0待处理 1处理中 2已处理 3无需处理
     */
	@Schema(description = "处理状态：0待处理 1处理中 2已处理 3无需处理")
	private Integer handleStatus;

	/**
	 *  处理方式：1自动冲正 2自动退款 3自动补单 4人工处理
     */
	@Schema(description = "处理方式：1自动冲正 2自动退款 3自动补单 4人工处理")
	private Integer handleType;

	/**
	 *  处理结果说明
     */
	@Schema(description = "处理结果说明")
	private String handleResult;

	/**
	 *  处理人ID
     */
	@Schema(description = "处理人ID")
	private Long handleUserId;

	/**
	 *  处理时间
     */
	@Schema(description = "处理时间")
	private Date handleTime;

	/**
	 *  乐观锁
     */
	@Schema(description = "乐观锁")
	private Integer version;

	/**
	 *  创建时间
     */
	@Schema(description = "创建时间")
	private Date createTime;

	/**
	 *  更新时间
     */
	@Schema(description = "更新时间")
	private Date updateTime;
}

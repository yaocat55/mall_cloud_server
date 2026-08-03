package cn.net.mall.pay.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.net.mall.entity.RequestConditionEntity;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 对账临时（按批次批量插入，对账完成保留N天后清理）查询条件实体
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Schema(description = "查询条件实体")
@EqualsAndHashCode(callSuper = true)
@Data
public class ReconTempConditionEntity extends RequestConditionEntity {


	/**
	 *  自增主键（临时表专用，非雪花）
     */
	@Schema(description = "自增主键（临时表专用，非雪花）")
	private Long id;

	/**
	 *  对账批次号
     */
	@Schema(description = "对账批次号")
	private String batchNo;

	/**
	 *  文件行号
     */
	@Schema(description = "文件行号")
	private Integer lineNo;

	/**
	 *  渠道商户订单号（= merchant_order_no）
     */
	@Schema(description = "渠道商户订单号（= merchant_order_no）")
	private String tradeNo;

	/**
	 *  渠道交易流水号
     */
	@Schema(description = "渠道交易流水号")
	private String channelTradeNo;

	/**
	 *  渠道退款单号（退款记录行才有）
     */
	@Schema(description = "渠道退款单号（退款记录行才有）")
	private String refundNo;

	/**
	 *  交易时间
     */
	@Schema(description = "交易时间")
	private Date tradeTime;

	/**
	 *  交易类型：PAY 支付 / REFUND 退款
     */
	@Schema(description = "交易类型：PAY 支付 / REFUND 退款")
	private String tradeType;

	/**
	 *  交易金额（分，正数）
     */
	@Schema(description = "交易金额（分，正数）")
	private Long amount;

	/**
	 *  手续费（分）
     */
	@Schema(description = "手续费（分）")
	private Long fee;

	/**
	 *  净入账金额（分，amount - fee，可正可负）
     */
	@Schema(description = "净入账金额（分，amount - fee，可正可负）")
	private Long income;

	/**
	 *  渠道交易状态：SUCCESS/REFUND/CLOSED
     */
	@Schema(description = "渠道交易状态：SUCCESS/REFUND/CLOSED")
	private String tradeStatus;

	/**
	 *  付款方账号
     */
	@Schema(description = "付款方账号")
	private String payerAccount;

	/**
	 *  渠道特有字段（原样保留）
     */
	@Schema(description = "渠道特有字段（原样保留）")
	private String extJson;

	/**
	 *  入库时间
     */
	@Schema(description = "入库时间")
	private Date createTime;
}

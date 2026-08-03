package cn.net.mall.pay.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.net.mall.entity.RequestConditionEntity;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 支付渠道回调日志查询条件实体
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Schema(description = "查询条件实体")
@EqualsAndHashCode(callSuper = true)
@Data
public class PayNotifyLogConditionEntity extends RequestConditionEntity {


	/**
	 *  主键，雪花ID
     */
	@Schema(description = "主键，雪花ID")
	private Long id;

	/**
	 *  支付订单ID
     */
	@Schema(description = "支付订单ID")
	private Long payOrderId;

	/**
	 *  支付订单号
     */
	@Schema(description = "支付订单号")
	private String payOrderNo;

	/**
	 *  渠道编码
     */
	@Schema(description = "渠道编码")
	private String channelCode;

	/**
	 *  渠道交易号
     */
	@Schema(description = "渠道交易号")
	private String channelTradeNo;

	/**
	 *  通知类型：PAY 支付 / REFUND 退款
     */
	@Schema(description = "通知类型：PAY 支付 / REFUND 退款")
	private String notifyType;

	/**
	 *  渠道原始报文
     */
	@Schema(description = "渠道原始报文")
	private String rawData;

	/**
	 *  解密后报文（微信需要解密）
     */
	@Schema(description = "解密后报文（微信需要解密）")
	private String decryptData;

	/**
	 *  验签结果：0未验证 1成功 2失败
     */
	@Schema(description = "验签结果：0未验证 1成功 2失败")
	private Integer verifyStatus;

	/**
	 *  处理状态：0待处理 1处理成功 2处理失败 3重复通知忽略
     */
	@Schema(description = "处理状态：0待处理 1处理成功 2处理失败 3重复通知忽略")
	private Integer processStatus;

	/**
	 *  处理结果信息
     */
	@Schema(description = "处理结果信息")
	private String processMsg;

	/**
	 *  创建时间
     */
	@Schema(description = "创建时间")
	private Date createTime;
}

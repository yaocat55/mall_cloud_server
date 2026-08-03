package cn.net.mall.pay.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import cn.net.mall.entity.BaseEntity;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 支付退款单实体
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Schema(description = "支付退款单实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class PayRefundEntity extends BaseEntity {


	/**
	 * 退款单号（雪花ID字符串）
	 */
	@Schema(description = "退款单号（雪花ID字符串）")
	private String refundNo;

	/**
	 * 支付订单ID
	 */
	@Schema(description = "支付订单ID")
	private Long payOrderId;

	/**
	 * 支付订单号
	 */
	@Schema(description = "支付订单号")
	private String payOrderNo;

	/**
	 * 业务方订单号
	 */
	@Schema(description = "业务方订单号")
	private String bizOrderNo;

	/**
	 * 业务类型：MALL_ORDER 商城订单 / OTHER 其他（冗余自 pay_order）
	 */
	@Schema(description = "业务类型：MALL_ORDER 商城订单 / OTHER 其他（冗余自 pay_order）")
	private String bizType;

	/**
	 * 用户ID（冗余自 pay_order，供按用户追溯退款）
	 */
	@Schema(description = "用户ID（冗余自 pay_order，供按用户追溯退款）")
	private Long userId;

	/**
	 * 渠道编码
	 */
	@Schema(description = "渠道编码")
	private String channelCode;

	/**
	 * 渠道退款单号
	 */
	@Schema(description = "渠道退款单号")
	private String channelRefundNo;

	/**
	 * 退款金额（分）
	 */
	@Schema(description = "退款金额（分）")
	private Long refundAmount;

	/**
	 * 退款手续费（分，微信等比例退还手续费，对账 FEE 对齐用）
	 */
	@Schema(description = "退款手续费（分，微信等比例退还手续费，对账 FEE 对齐用）")
	private Long refundFee;

	/**
	 * 退款原因
	 */
	@Schema(description = "退款原因")
	private String refundReason;

	/**
	 * 退款类型：1用户申请 2系统自动 3人工介入
	 */
	@Schema(description = "退款类型：1用户申请 2系统自动 3人工介入")
	private Integer refundType;

	/**
	 * 审核状态：0无需审核 1待审核 2审核通过 3审核拒绝
	 */
	@Schema(description = "审核状态：0无需审核 1待审核 2审核通过 3审核拒绝")
	private Integer auditStatus;

	/**
	 * 退款状态：0待处理 1处理中 2退款成功 3退款失败
	 */
	@Schema(description = "退款状态：0待处理 1处理中 2退款成功 3退款失败")
	private Integer refundStatus;

	/**
	 * 审核人ID
	 */
	@Schema(description = "审核人ID")
	private Long auditUserId;

	/**
	 * 审核人姓名
	 */
	@Schema(description = "审核人姓名")
	private String auditUserName;

	/**
	 * 审核时间
	 */
	@Schema(description = "审核时间")
	private Date auditTime;

	/**
	 * 退款成功时间
	 */
	@Schema(description = "退款成功时间")
	private Date successTime;

	/**
	 * 退款失败原因
	 */
	@Schema(description = "退款失败原因")
	private String failReason;

	/**
	 * 乐观锁
	 */
	@Schema(description = "乐观锁")
	private Integer version;

	/**
	 * 备注
	 */
	@Schema(description = "备注")
	private String remark;

}

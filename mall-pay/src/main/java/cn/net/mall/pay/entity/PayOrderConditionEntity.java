package cn.net.mall.pay.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.net.mall.entity.RequestConditionEntity;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 支付订单查询条件实体
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Schema(description = "查询条件实体")
@EqualsAndHashCode(callSuper = true)
@Data
public class PayOrderConditionEntity extends RequestConditionEntity {


	/**
	 *  主键，雪花ID
     */
	@Schema(description = "主键，雪花ID")
	private Long id;

	/**
	 *  支付订单号（对外交易号，雪花ID字符串）
     */
	@Schema(description = "支付订单号（对外交易号，雪花ID字符串）")
	private String payOrderNo;

	/**
	 *  业务方订单号（如商城 tradeCode）
     */
	@Schema(description = "业务方订单号（如商城 tradeCode）")
	private String bizOrderNo;

	/**
	 *  业务类型：MALL_ORDER 商城订单 / OTHER 其他
     */
	@Schema(description = "业务类型：MALL_ORDER 商城订单 / OTHER 其他")
	private String bizType;

	/**
	 *  支付渠道：ALIPAY / WECHAT_PAY / WECHAT_MINI / MOCK（模拟渠道，仅 dev/test 启用）
     */
	@Schema(description = "支付渠道：ALIPAY / WECHAT_PAY / WECHAT_MINI / MOCK（模拟渠道，仅 dev/test 启用）")
	private String channelCode;

	/**
	 *  渠道商户订单号（本地唯一，渠道侧可回溯）
     */
	@Schema(description = "渠道商户订单号（本地唯一，渠道侧可回溯）")
	private String merchantOrderNo;

	/**
	 *  渠道交易号（回调成功后回填）
     */
	@Schema(description = "渠道交易号（回调成功后回填）")
	private String channelTradeNo;

	/**
	 *  用户ID
     */
	@Schema(description = "用户ID")
	private Long userId;

	/**
	 *  微信 openId（小程序支付必填）
     */
	@Schema(description = "微信 openId（小程序支付必填）")
	private String openId;

	/**
	 *  商品描述（收银台展示）
     */
	@Schema(description = "商品描述（收银台展示）")
	private String subject;

	/**
	 *  商品详情
     */
	@Schema(description = "商品详情")
	private String body;

	/**
	 *  订单总金额（单位：分）
     */
	@Schema(description = "订单总金额（单位：分）")
	private Long totalAmount;

	/**
	 *  实际支付金额（单位：分，默认=total_amount）
     */
	@Schema(description = "实际支付金额（单位：分，默认=total_amount）")
	private Long payAmount;

	/**
	 *  已退款金额（单位：分）
     */
	@Schema(description = "已退款金额（单位：分）")
	private Long refundAmount;

	/**
	 *  币种
     */
	@Schema(description = "币种")
	private String currency;

	/**
	 *  支付状态：10待支付 20已支付 30已关闭 40支付失败
     */
	@Schema(description = "支付状态：10待支付 20已支付 30已关闭 40支付失败")
	private Integer payStatus;

	/**
	 *  退款状态：0无 1退款中 2部分退款 3全额退款 4退款失败
     */
	@Schema(description = "退款状态：0无 1退款中 2部分退款 3全额退款 4退款失败")
	private Integer refundStatus;

	/**
	 *  渠道回调次数
     */
	@Schema(description = "渠道回调次数")
	private Integer notifyCount;

	/**
	 *  最近一次回调时间
     */
	@Schema(description = "最近一次回调时间")
	private Date notifyTime;

	/**
	 *  业务方通知状态：0待通知 1通知成功 2通知失败
     */
	@Schema(description = "业务方通知状态：0待通知 1通知成功 2通知失败")
	private Integer bizNotifyStatus;

	/**
	 *  客户端IP
     */
	@Schema(description = "客户端IP")
	private String clientIp;

	/**
	 *  设备信息
     */
	@Schema(description = "设备信息")
	private String deviceInfo;

	/**
	 *  支付成功时间
     */
	@Schema(description = "支付成功时间")
	private Date successTime;

	/**
	 *  支付过期时间（超时自动关闭）
     */
	@Schema(description = "支付过期时间（超时自动关闭）")
	private Date expireTime;

	/**
	 *  关闭时间
     */
	@Schema(description = "关闭时间")
	private Date closedTime;

	/**
	 *  乐观锁
     */
	@Schema(description = "乐观锁")
	private Integer version;

	/**
	 *  创建人ID
     */
	@Schema(description = "创建人ID")
	private Long createUserId;

	/**
	 *  创建人姓名
     */
	@Schema(description = "创建人姓名")
	private String createUserName;

	/**
	 *  创建时间
     */
	@Schema(description = "创建时间")
	private Date createTime;

	/**
	 *  更新人ID
     */
	@Schema(description = "更新人ID")
	private Long updateUserId;

	/**
	 *  更新人姓名
     */
	@Schema(description = "更新人姓名")
	private String updateUserName;

	/**
	 *  更新时间
     */
	@Schema(description = "更新时间")
	private Date updateTime;

	/**
	 *  备注
     */
	@Schema(description = "备注")
	private String remark;
}

package cn.net.mall.pay.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.net.mall.entity.RequestConditionEntity;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 业务渠道接入查询条件实体
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Schema(description = "查询条件实体")
@EqualsAndHashCode(callSuper = true)
@Data
public class PayBizConfigConditionEntity extends RequestConditionEntity {


	/**
	 *  主键，雪花ID
     */
	@Schema(description = "主键，雪花ID")
	private Long id;

	/**
	 *  业务类型编码（唯一，如 MALL_ORDER）
     */
	@Schema(description = "业务类型编码（唯一，如 MALL_ORDER）")
	private String bizType;

	/**
	 *  业务方名称（如 商城订单）
     */
	@Schema(description = "业务方名称（如 商城订单）")
	private String bizName;

	/**
	 *  业务方接收支付结果通知的 MQ Topic
     */
	@Schema(description = "业务方接收支付结果通知的 MQ Topic")
	private String notifyMqTopic;

	/**
	 *  MQ Tag（可选，用于过滤）
     */
	@Schema(description = "MQ Tag（可选，用于过滤）")
	private String notifyMqTag;

	/**
	 *  业务方回调验签密钥（AES 加密存储，预留）
     */
	@Schema(description = "业务方回调验签密钥（AES 加密存储，预留）")
	private String signKey;

	/**
	 *  状态：1启用 0禁用
     */
	@Schema(description = "状态：1启用 0禁用")
	private Integer status;

	/**
	 *  备注
     */
	@Schema(description = "备注")
	private String remark;

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
}

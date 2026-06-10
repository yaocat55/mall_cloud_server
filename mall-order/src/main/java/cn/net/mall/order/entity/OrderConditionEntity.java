package cn.net.mall.order.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单查询条件实体
 */
@Schema(name = "订单查询条件实体")
@Data
public class OrderConditionEntity extends RequestConditionEntity {

	/**
	 *  ID
     */
	@Schema(name = "ID")
	private Long id;

	/**
	 *  订单编码
     */
	@Schema(name = "订单编码")
	private String code;

	/**
	 *  用户ID
     */
	@Schema(name = "用户ID")
	private Long userId;

	/**
	 *  用户名称
     */
	@Schema(name = "用户名称")
	private String userName;

	/**
	 *  下单时间
     */
	@Schema(name = "下单时间")
	private Date orderTime;

	/**
	 *  订单状态 1:已下单 2:已支付 3：已发货 4：已完成 5：已取消 6：已退货 7：已评价
     */
	@Schema(name = "订单状态 1:已下单 2:已支付 3：已发货 4：已完成 5：已取消 6：已退货 7：已评价")
	private Integer orderStatus;

	/**
	 *  支付状态 1:待支付 2:已支付 3：退款
     */
	@Schema(name = "支付状态 1:待支付 2:已支付 3：退款")
	private Integer payStatus;

	/**
	 *  总金额
     */
	@Schema(name = "总金额")
	private BigDecimal totalAmount;

	/**
	 *  付款金额
     */
	@Schema(name = "付款金额")
	private BigDecimal paymentAmount;

	/**
	 *  备注
     */
	@Schema(name = "备注")
	private String remark;

	/**
	 *  创建人ID
     */
	@Schema(name = "创建人ID")
	private Long createUserId;

	/**
	 *  创建人名称
     */
	@Schema(name = "创建人名称")
	private String createUserName;

	/**
	 *  创建日期
     */
	@Schema(name = "创建日期")
	private Date createTime;
}

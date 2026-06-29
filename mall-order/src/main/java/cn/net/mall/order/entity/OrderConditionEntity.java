package cn.net.mall.order.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单查询条件实体
 */
@Schema(description = "订单查询条件实体")
@Data
public class OrderConditionEntity extends RequestConditionEntity {

	/**
	 *  ID
     */
	@Schema(description = "ID", example = "1")
	private Long id;

	/**
	 *  订单编码
     */
	@Schema(description = "订单编码", example = "CODE_001")
	private String code;

	/**
	 *  用户ID
     */
	@Schema(description = "用户ID", example = "1")
	private Long userId;

	/**
	 *  用户名称
     */
	@Schema(description = "用户名称", example = "admin")
	private String userName;

	/**
	 *  下单时间
     */
	@Schema(description = "下单时间", example = "2024-01-01")
	private Date orderTime;

	/**
	 *  订单状态 1:已下单 2:已支付 3：已发货 4：已完成 5：已取消 6：已退货 7：已评价
     */
	@Schema(description = "订单状态 1:已下单 2:已支付 3：已发货 4：已完成 5：已取消 6：已退货 7：已评价", example = "1")
	private Integer orderStatus;

	/**
	 *  支付状态 1:待支付 2:已支付 3：退款
     */
	@Schema(description = "支付状态 1:待支付 2:已支付 3：退款", example = "1")
	private Integer payStatus;

	/**
	 *  总金额
     */
	@Schema(description = "总金额", example = "99.99")
	private BigDecimal totalAmount;

	/**
	 *  付款金额
     */
	@Schema(description = "付款金额", example = "99.99")
	private BigDecimal paymentAmount;

	/**
	 *  备注
     */
	@Schema(description = "备注", example = "备注")
	private String remark;

	/**
	 *  创建人ID
     */
	@Schema(description = "创建人ID", example = "1")
	private Long createUserId;

	/**
	 *  创建人名称
     */
	@Schema(description = "创建人名称", example = "admin")
	private String createUserName;

	/**
	 *  创建日期
     */
	@Schema(description = "创建日期", example = "2024-01-01 00:00:00")
	private Date createTime;
}

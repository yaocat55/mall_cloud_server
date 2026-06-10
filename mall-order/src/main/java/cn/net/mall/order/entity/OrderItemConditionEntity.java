package cn.net.mall.order.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单明细查询条件实体
 */
@Schema(name = "订单明细查询条件实体")
@Data
public class OrderItemConditionEntity extends RequestConditionEntity {

	/**
	 *  ID
     */
	@Schema(name = "ID")
	private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	/**
	 *  订单ID
     */
	@Schema(name = "订单ID")
	private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

	/**
	 *  订单编码
	 */
	@Schema(name = "订单编码")
	private String orderCode;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

	/**
	 *  订单ID集合
	 */
	@Schema(name = "订单ID集合")
	private List<Long> orderIdList;

    public List<Long> getOrderIdList() {
        return orderIdList;
    }

    public void setOrderIdList(List<Long> orderIdList) {
        this.orderIdList = orderIdList;
    }

	/**
	 *  商品ID
     */
	@Schema(name = "商品ID")
	private Long productId;

	/**
	 *  商品名称
     */
	@Schema(name = "商品名称")
	private String productName;

	/**
	 *  商品规格
     */
	@Schema(name = "商品规格")
	private String model;

	/**
	 *  单价
     */
	@Schema(name = "单价")
	private BigDecimal price;

	/**
	 *  数量
     */
	@Schema(name = "数量")
	private Integer quantity;

	/**
	 *  金额
     */
	@Schema(name = "金额")
	private BigDecimal amount;

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

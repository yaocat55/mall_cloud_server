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
@Schema(description = "订单明细查询条件实体")
@Data
public class OrderItemConditionEntity extends RequestConditionEntity {

	/**
	 *  ID
     */
	@Schema(description = "ID", example = "1")
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
	@Schema(description = "订单ID", example = "1")
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
	@Schema(description = "订单编码", example = "-")
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
	@Schema(description = "订单ID集合", example = "0")
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
	@Schema(description = "商品ID", example = "1")
	private Long productId;

	/**
	 *  商品名称
     */
	@Schema(description = "商品名称", example = "-")
	private String productName;

	/**
	 *  商品规格
     */
	@Schema(description = "商品规格", example = "型号")
	private String model;

	/**
	 *  单价
     */
	@Schema(description = "单价", example = "99.99")
	private BigDecimal price;

	/**
	 *  数量
     */
	@Schema(description = "数量", example = "10")
	private Integer quantity;

	/**
	 *  金额
     */
	@Schema(description = "金额", example = "99.99")
	private BigDecimal amount;

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

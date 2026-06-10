package cn.net.mall.order.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单收货地址实体
 */
@Schema(name = "订单收货地址实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDeliveryAddressEntity extends BaseEntity {

	/**
	 * 订单ID
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
	 * 订单编码
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
	 * 用户ID
	 */
    @Schema(name = "用户ID")
	private Long userId;

	/**
	 * 用户名称
	 */
    @Schema(name = "用户名称")
	private String userName;

	/**
	 * 收货人姓名
	 */
    @Schema(name = "收货人姓名")
	private String receiverName;

	/**
	 * 收货人手机号
	 */
    @Schema(name = "收货人手机号")
	private String receiverPhone;

	/**
	 * 省份
	 */
    @Schema(name = "省份")
	private String province;

	/**
	 * 城市
	 */
    @Schema(name = "城市")
	private String city;

	/**
	 * 区县
	 */
    @Schema(name = "区县")
	private String district;

	/**
	 * 详细地址
	 */
    @Schema(name = "详细地址")
	private String detailAddress;

	/**
	 * 邮编
	 */
    @Schema(name = "邮编")
	private String postCode;
}

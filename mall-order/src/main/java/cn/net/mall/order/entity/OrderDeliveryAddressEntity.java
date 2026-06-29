package cn.net.mall.order.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单收货地址实体
 */
@Schema(description = "订单收货地址实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDeliveryAddressEntity extends BaseEntity {

	/**
	 * 订单ID
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
	 * 订单编码
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
	 * 用户ID
	 */
    @Schema(description = "用户ID", example = "1")
	private Long userId;

	/**
	 * 用户名称
	 */
    @Schema(description = "用户名称", example = "admin")
	private String userName;

	/**
	 * 收货人姓名
	 */
    @Schema(description = "收货人姓名", example = "-")
	private String receiverName;

	/**
	 * 收货人手机号
	 */
    @Schema(description = "收货人手机号", example = "-")
	private String receiverPhone;

	/**
	 * 省份
	 */
    @Schema(description = "省份", example = "北京市")
	private String province;

	/**
	 * 城市
	 */
    @Schema(description = "城市", example = "北京市")
	private String city;

	/**
	 * 区县
	 */
    @Schema(description = "区县", example = "朝阳区")
	private String district;

	/**
	 * 详细地址
	 */
    @Schema(description = "详细地址", example = "北京市朝阳区xxx街道")
	private String detailAddress;

	/**
	 * 邮编
	 */
    @Schema(description = "邮编", example = "-")
	private String postCode;
}

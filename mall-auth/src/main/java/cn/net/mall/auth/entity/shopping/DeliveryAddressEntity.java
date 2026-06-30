package cn.net.mall.auth.entity.shopping;

import cn.net.mall.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 收货地址实体
 *
 * @date 2024-09-01 10:02:01
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "收货地址")

public class DeliveryAddressEntity extends BaseEntity {


	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * 收货人姓名
	 */
	private String receiverName;

	/**
	 * 收货人手机号
	 */
	private String receiverPhone;

	/**
	 * 省份ID
	 */
	private Long provinceId;

	/**
	 * 城市ID
	 */
	private Long cityId;

	/**
	 * 区县ID
	 */
	private Long districtId;

	/**
	 * 省份
	 */
	private String province;

	/**
	 * 城市
	 */
	private String city;

	/**
	 * 区县
	 */
	private String district;

	/**
	 * 邮编
	 */
	private String postCode;

	/**
	 * 详细地址
	 */
	private String detailAddress;

	/**
	 * 是否默认地址 1：是 0：否
	 */
	private Boolean isDefault;
}

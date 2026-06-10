package cn.net.mall.auth.dto;

import cn.net.mall.annotation.ValidPhone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 收货地址DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliveryAddressDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 系统ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收货人姓名
     */
    @NotEmpty(message = "收货人姓名不能为空")
    private String receiverName;

    /**
     * 收货人手机号
     */
    @NotEmpty(message = "收货人手机号不能为空")
    @ValidPhone
    private String receiverPhone;

    /**
     * 省份ID
     */
    @NotNull(message = "省份ID不能为空")
    private Long provinceId;

    /**
     * 省份
     */
    @NotEmpty(message = "省份不能为空")
    private String province;

    /**
     * 城市ID
     */
    @NotNull(message = "城市ID不能为空")
    private Long cityId;

    /**
     * 城市
     */
    @NotEmpty(message = "城市不能为空")
    private String city;

    /**
     * 区县ID
     */
    @NotNull(message = "区县ID不能为空")
    private Long districtId;

    /**
     * 区县
     */
    @NotEmpty(message = "区县不能为空")
    private String district;

    /**
     * 邮编
     */
    @NotEmpty(message = "邮编不能为空")
    private String postCode;

    /**
     * 详细地址
     */
    @NotEmpty(message = "详细地址不能为空")
    private String detailAddress;

    /**
     * 是否默认地址 1：是 0：否
     */
    private Boolean isDefault;
}

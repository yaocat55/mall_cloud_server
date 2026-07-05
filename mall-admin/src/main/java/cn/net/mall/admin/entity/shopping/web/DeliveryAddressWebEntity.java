package cn.net.mall.admin.entity.shopping.web;

import cn.net.mall.annotation.ValidPhone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 收货地址实体
 *
 * @date 2024-09-10 10:02:01
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "收货地址")

public class DeliveryAddressWebEntity {

    /**
     * 系统ID
     */
    @Schema(description = "系统ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 收货人姓名
     */
    @NotEmpty(message = "收货人姓名不能为空")
    @Schema(description = "收货人姓名")
    private String receiverName;

    /**
     * 收货人手机号
     */
    @NotEmpty(message = "收货人手机号不能为空")
    @ValidPhone
    @Schema(description = "收货人手机号")
    private String receiverPhone;

    /**
     * 省份ID
     */
    @NotNull(message = "省份ID不能为空")
    @Schema(description = "province Id")
    private Long provinceId;

    /**
     * 省份
     */
    @NotEmpty(message = "省份不能为空")
    @Schema(description = "省份")
    private String province;

    /**
     * 城市ID
     */
    @NotNull(message = "城市ID不能为空")
    @Schema(description = "city Id")
    private Long cityId;

    /**
     * 城市
     */
    @NotEmpty(message = "城市不能为空")
    @Schema(description = "城市")
    private String city;

    /**
     * 区县ID
     */
    @NotNull(message = "区县ID不能为空")
    @Schema(description = "district Id")
    private Long districtId;

    /**
     * 区县
     */
    @NotEmpty(message = "区县不能为空")
    @Schema(description = "区县")
    private String district;

    /**
     * 邮编
     */
    @NotEmpty(message = "邮编不能为空")
    @Schema(description = "邮编")
    private String postCode;

    /**
     * 详细地址
     */
    @NotEmpty(message = "详细地址不能为空")
    @Schema(description = "详细地址")
    private String detailAddress;

    /**
     * 是否默认地址 1：是 0：否
     */
    @Schema(description = "是否默认")
    private Boolean isDefault;
}

package cn.net.mall.admin.dto;

import cn.net.mall.annotation.ValidPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 收货地址 DTO
 */
@Schema(description = "收货地址 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliveryAddressDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 系统ID
     */
    @Schema(description = "系统ID", example = "1")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "13")
    private Long userId;

    /**
     * 收货人姓名
     */
    @NotEmpty(message = "收货人姓名不能为空")
    @Schema(description = "收货人姓名", example = "张三")
    private String receiverName;

    /**
     * 收货人手机号
     */
    @NotEmpty(message = "收货人手机号不能为空")
    @ValidPhone
    @Schema(description = "收货人手机号", example = "13800138000")
    private String receiverPhone;

    /**
     * 省份ID
     */
    @NotNull(message = "省份ID不能为空")
    @Schema(description = "省份ID", example = "440000")
    private Long provinceId;

    /**
     * 省份
     */
    @NotEmpty(message = "省份不能为空")
    @Schema(description = "省份", example = "广东省")
    private String province;

    /**
     * 城市ID
     */
    @NotNull(message = "城市ID不能为空")
    @Schema(description = "城市ID", example = "440100")
    private Long cityId;

    /**
     * 城市
     */
    @NotEmpty(message = "城市不能为空")
    @Schema(description = "城市", example = "广州市")
    private String city;

    /**
     * 区县ID
     */
    @NotNull(message = "区县ID不能为空")
    @Schema(description = "区县ID", example = "440103")
    private Long districtId;

    /**
     * 区县
     */
    @NotEmpty(message = "区县不能为空")
    @Schema(description = "区县", example = "荔湾区")
    private String district;

    /**
     * 邮编
     */
    @NotEmpty(message = "邮编不能为空")
    @Schema(description = "邮编", example = "510000")
    private String postCode;

    /**
     * 详细地址
     */
    @NotEmpty(message = "详细地址不能为空")
    @Schema(description = "详细地址", example = "中山三路33号")
    private String detailAddress;

    /**
     * 是否默认地址 1：是 0：否
     */
    @Schema(description = "是否默认 1:是 0:否", example = "0")
    private Boolean isDefault;
}

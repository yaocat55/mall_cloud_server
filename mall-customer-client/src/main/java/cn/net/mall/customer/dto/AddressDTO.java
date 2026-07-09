package cn.net.mall.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 收货地址 DTO
 */
@Schema(description = "收货地址 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "系统ID", example = "1")
    private Long id;

    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @NotEmpty(message = "收货人姓名不能为空")
    @Schema(description = "收货人姓名", example = "张三")
    private String receiverName;

    @NotEmpty(message = "收货人手机号不能为空")
    @Schema(description = "收货人手机号", example = "13800138000")
    private String receiverPhone;

    @NotNull(message = "省份ID不能为空")
    @Schema(description = "省份ID", example = "440000")
    private Long provinceId;

    @NotEmpty(message = "省份不能为空")
    @Schema(description = "省份", example = "广东省")
    private String province;

    @NotNull(message = "城市ID不能为空")
    @Schema(description = "城市ID", example = "440100")
    private Long cityId;

    @NotEmpty(message = "城市不能为空")
    @Schema(description = "城市", example = "广州市")
    private String city;

    @NotNull(message = "区县ID不能为空")
    @Schema(description = "区县ID", example = "440103")
    private Long districtId;

    @NotEmpty(message = "区县不能为空")
    @Schema(description = "区县", example = "荔湾区")
    private String district;

    @Schema(description = "邮编", example = "510000")
    private String postCode;

    @NotEmpty(message = "详细地址不能为空")
    @Schema(description = "详细地址", example = "中山三路33号")
    private String detailAddress;

    @Schema(description = "是否默认 1:是 0:否", example = "0")
    private Boolean isDefault;

    @Schema(description = "标签（家/公司/学校）", example = "家")
    private String label;
}

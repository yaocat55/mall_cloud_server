package cn.net.mall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 绑定手机号实体
 *
 * @date 2025/5/28 18:39
 */
@Schema(description = "绑定手机号请求参数")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BindPhoneDTO {

    @NotBlank(message = "当前手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "当前手机号格式不正确")
    @Schema(description = "新手机号", example = "13800138000")
    private String newPhone;

    @NotBlank(message = "短信验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "短信验证码不能为空")
    @Schema(description = "短信验证码", example = "123456")
    private String smsCode;
}
package cn.net.mall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 重置密码 DTO
 *
 * @date 2025/4/22 16:07
 */
@Schema(description = "重置密码 DTO")
@Data
public class ResetPasswordDTO {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @NotBlank(message = "短信验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "短信验证码不能为空")
    @Schema(description = "短信验证码", example = "123456")
    private String smsCode;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "图形验证码", example = "0000")
    private String verifyCode;

    /**
     * 图形验证码uuid
     */
    @NotBlank(message = "图形验证码uuid不能为空")
    @Schema(description = "图形验证码UUID", example = "C0ff54a1581c547f8bb13fd8b60e26d2e")
    private String uuid;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$", message = "密码必须包含字母和数字，长度为6-20位")
    @Schema(description = "新密码", example = "newPass123")
    private String password;
}
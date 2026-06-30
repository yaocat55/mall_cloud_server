package cn.net.mall.auth.dto;

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
    private String phone;

    @NotBlank(message = "短信验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "短信验证码不能为空")
    private String smsCode;

    @NotBlank(message = "验证码不能为空")
    private String verifyCode;

    /**
     * 图形验证码uuid
     */
    @NotBlank(message = "图形验证码uuid不能为空")
    private String uuid;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$", message = "密码必须包含字母和数字，长度为6-20位")
    private String password;
}
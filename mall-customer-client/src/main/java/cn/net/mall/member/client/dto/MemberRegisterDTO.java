package cn.net.mall.customer.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "会员注册请求参数")
@Data
public class MemberRegisterDTO {

    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 20, message = "昵称长度必须在1-20个字符之间")
    @Schema(description = "昵称", example = "张三")
    private String nickName;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Schema(description = "密码", example = "123456")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    @Size(min = 6, max = 20, message = "确认密码长度必须在6-20个字符之间")
    @Schema(description = "确认密码", example = "123456")
    private String confirmPassword;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @NotBlank(message = "短信验证码不能为空")
    @Size(min = 6, max = 6, message = "短信验证码必须是6位数字")
    @Schema(description = "短信验证码", example = "123456")
    private String smsCode;

    @NotBlank(message = "图形验证码uuid不能为空")
    @Schema(description = "图形验证码Key", example = "abc123def456")
    private String uuid;

    @NotBlank(message = "图形验证码不能为空")
    @Schema(description = "图形验证码", example = "CODE_001")
    private String code;
}

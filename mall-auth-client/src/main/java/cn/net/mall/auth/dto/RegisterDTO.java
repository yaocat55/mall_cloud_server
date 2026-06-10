package cn.net.mall.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求DTO
 */
@Data
public class RegisterDTO {

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 3, max = 20, message = "昵称长度必须在3-20个字符之间")
    private String nickName;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String userName;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    @Size(min = 6, max = 20, message = "密确认密码长度必须在6-20个字符之间")
    private String confirmPassword;


    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 短信验证码
     */
    @NotBlank(message = "短信验证码不能为空")
    @Size(min = 6, max = 6, message = "短信验证码必须是6位数字")
    private String smsCode;

    /**
     * 图形验证码uuid
     */
    @NotBlank(message = "图形验证码uuid不能为空")
    private String uuid;

    /**
     * 图形验证码
     */
    @NotBlank(message = "图形验证码不能为空")
    private String code;
}
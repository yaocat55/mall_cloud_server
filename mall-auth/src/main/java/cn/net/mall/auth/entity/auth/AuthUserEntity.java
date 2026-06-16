package cn.net.mall.auth.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(name = "权限用户实体")
@Data
public class AuthUserEntity {

    @NotBlank(message = "唯一标识不能为空")
    @Schema(name = "唯一标识")
    private String uuid;

    @NotBlank(message = "用户名称不能为空")
    @Schema(name = "用户名称")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(name = "密码")
    @NotBlank
    private String password;

    @NotBlank(message = "验证码不能为空")
    @Schema(name = "验证码")
    private String code;

    @Schema(name = "手机号")
    private String phone;
}

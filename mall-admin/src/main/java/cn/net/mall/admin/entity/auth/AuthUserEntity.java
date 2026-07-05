package cn.net.mall.admin.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "权限用户实体")
@Data
public class AuthUserEntity {

    @NotBlank(message = "唯一标识不能为空")
    @Schema(description = "唯一标识", example = "abc123def456")
    private String uuid;

    @NotBlank(message = "用户名称不能为空")
    @Schema(description = "用户名称", example = "admin")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456")
    @NotBlank
    private String password;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码", example = "CODE_001")
    private String code;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;
}

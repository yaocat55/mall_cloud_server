package cn.net.mall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录实体
 */
@Schema(description = "用户登录请求参数")
@Data
public class UserLoginDTO {

    /**
     * 唯一标识
     */
    @NotBlank(message = "唯一标识不能为空")
    @Schema(description = "图形验证码Key", example = "abc123def456")
    private String uuid;

    /**
     * 用户名称
     */
    @NotBlank(message = "用户名称不能为空")
    @Schema(description = "用户名", example = "admin")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456")
    @NotBlank
    private String password;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Schema(description = "图形验证码", example = "CODE_001")
    private String code;
}

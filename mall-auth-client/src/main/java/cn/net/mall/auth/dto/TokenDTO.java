package cn.net.mall.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * token实体
 *
 * @date 2024/1/12 下午12:54
 */
@Schema(description = "登录Token响应")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenDTO {

    /**
     * 用户名称
     */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /**
     * token
     */
    @Schema(description = "Token", example = "eyJhbGciOiJIUzI1NiJ9.xxx")
    private String token;

    /**
     * 角色信息
     */
    @Schema(description = "角色列表", example = "[\"ROLE_ADMIN\"]")
    private List<String> roles;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间（秒）", example = "3600")
    private int expiresIn;
}

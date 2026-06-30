package cn.net.mall.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户信息 DTO
 *
 * @date 2025/5/22 16:16
 */
@Schema(description = "用户信息 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInfoDTO {

    /**
     * 用户ID
     */
    @Schema(description = "系统ID")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 角色信息
     */
    @Schema(description = "roles")
    private List<String> roles;
}
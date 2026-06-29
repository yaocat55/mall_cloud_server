package cn.net.mall.auth.entity.auth.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用户web实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-09-29 11:18:18
 */
@Schema(description = "用户web实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserWebEntity {

    /**
     * 系统ID
     */
    private Long id;

    /**
     * 头像url
     */
    @Schema(description = "头像url", example = "-")
    private String avatarUrl;

    /**
     * 邮箱
     */
    @NotEmpty(message = "邮箱不能为空")
    @Schema(description = "邮箱", example = "admin@mall.com")
    private String email;


    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    private String userName;


    /**
     * 别名
     */
    @Schema(description = "别名", example = "-")
    private String nickName;

    /**
     * 性别 1：男 2：女
     */
    @Schema(description = "性别 1：男 2：女", example = "1")
    private Integer sex;
}

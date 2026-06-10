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
@Schema(name = "用户web实体")
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
    @Schema(name = "头像url")
    private String avatarUrl;

    /**
     * 邮箱
     */
    @NotEmpty(message = "邮箱不能为空")
    @Schema(name = "邮箱")
    private String email;


    /**
     * 用户名
     */
    @Schema(name = "用户名")
    private String userName;


    /**
     * 别名
     */
    @Schema(name = "别名")
    private String nickName;

    /**
     * 性别 1：男 2：女
     */
    @Schema(name = "性别 1：男 2：女")
    private Integer sex;
}

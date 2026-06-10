package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户角色关联实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-01-08 17:18:19
 */
@Schema(name = "用户角色关联实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRoleEntity extends BaseEntity {


    /**
     * 用户ID
     */
    @Schema(name = "用户ID")
    private Long userId;

    /**
     * 角色ID
     */
    @Schema(name = "角色ID")
    private Long roleId;
}

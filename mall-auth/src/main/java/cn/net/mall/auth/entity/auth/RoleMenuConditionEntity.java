package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 角色菜单关联查询条件实体
 *
 * @date 2024-01-08 17:18:18
 */
@Schema(description = "角色菜单关联查询条件实体")
@Data
public class RoleMenuConditionEntity extends RequestPageEntity {


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID", example = "1")
    private Long menuId;

    /**
     * 角色ID集合
     */
    private List<Long> roleIdList;
}

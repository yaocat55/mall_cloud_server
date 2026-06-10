package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 角色实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-01-08 17:18:18
 */
@Schema(name = "角色实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleEntity extends BaseEntity {


    /**
     * 名称
     */
    @Schema(name = "名称")
    private String name;

    /**
     * 备注
     */
    @Schema(name = "备注")
    private String remark;

    /**
     * 数据权限
     */
    @Schema(name = "数据权限")
    private String dataScope;

    /**
     * 角色级别
     */
    @Schema(name = "角色级别")
    private Integer level;

    /**
     * 功能权限
     */
    @Schema(name = "功能权限")
    private String permission;

    /**
     * 菜单
     */
    @Schema(name = "菜单")
    private List<MenuEntity> menus;
}

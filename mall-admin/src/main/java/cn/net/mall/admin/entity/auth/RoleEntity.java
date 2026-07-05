package cn.net.mall.admin.entity.auth;

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
@Schema(description = "角色实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleEntity extends BaseEntity {


    /**
     * 名称
     */
    @Schema(description = "名称", example = "测试数据")
    private String name;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "备注")
    private String remark;

    /**
     * 数据权限
     */
    @Schema(description = "数据权限", example = "-")
    private String dataScope;

    /**
     * 角色级别
     */
    @Schema(description = "角色级别", example = "1")
    private Integer level;

    /**
     * 功能权限
     */
    @Schema(description = "功能权限", example = "-")
    private String permission;

    /**
     * 菜单
     */
    @Schema(description = "菜单")
    private List<MenuEntity> menus;
}

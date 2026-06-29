package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色菜单关联实体 该项目是知识星球：java突击队 的内部项目
 * 
 * @date 2024-01-08 17:18:18
 */
@Schema(description = "角色菜单关联实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleMenuEntity extends BaseEntity {
	

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
}

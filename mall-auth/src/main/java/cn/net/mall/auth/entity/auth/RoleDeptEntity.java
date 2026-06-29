package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色部门关联实体 该项目是知识星球：java突击队 的内部项目
 * 
 * @date 2024-01-08 17:18:18
 */
@Schema(description = "角色部门关联实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleDeptEntity extends BaseEntity {
	

	/**
	 * 
	 */
	@Schema(description = "", example = "1")
	private Long roleId;

	/**
	 * 
	 */
	@Schema(description = "", example = "1")
	private Long deptId;
}

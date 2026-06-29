package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色部门关联查询条件实体
 * 
 * @date 2024-01-08 17:18:18
 */
@Schema(description = "角色部门关联查询条件实体")
@Data
public class RoleDeptConditionEntity extends RequestPageEntity {
	

	/**
	 *  ID
     */
	@Schema(description = "ID", example = "1")
	private Long id;

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

package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色部门关联查询条件实体
 * 
 * @date 2024-01-08 17:18:18
 */
@Schema(name = "角色部门关联查询条件实体")
@Data
public class RoleDeptConditionEntity extends RequestPageEntity {
	

	/**
	 *  ID
     */
	@Schema(name = "ID")
	private Long id;

	/**
	 *  
     */
	@Schema(name = "")
	private Long roleId;

	/**
	 *  
     */
	@Schema(name = "")
	private Long deptId;
}

package cn.net.mall.admin.controller.auth;

import cn.net.mall.admin.entity.auth.RoleDeptConditionEntity;
import cn.net.mall.admin.entity.auth.RoleDeptEntity;
import cn.net.mall.admin.service.auth.RoleDeptService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

/**
 * 角色部门关联 接口层
 * 
 * @date 2024-01-08 17:18:18
 */
@Tag(name = "角色部门管理", description = "管理后台：角色部门关联")
@RestController
@RequestMapping("/v1/roleDept")
public class RoleDeptController {
	
	private final RoleDeptService roleDeptService;

	public RoleDeptController(RoleDeptService roleDeptService) {
		this.roleDeptService = roleDeptService;
	}

	/**
	 * 通过id查询角色部门关联信息
	 *
	 * @param id 系统ID
	 * @return 角色部门关联信息
	 */
	@Operation(summary = "通过id查询角色部门关联信息", description = "通过id查询角色部门关联信息")
	@GetMapping("/findById")
	public RoleDeptEntity findById(Long id) {
		return roleDeptService.findById(id);
	}

	/**
    * 根据条件查询角色部门关联列表
    *
    * @param roleDeptConditionEntity 条件
    * @return 角色部门关联列表
    */
	@Operation(summary = "根据条件查询角色部门关联列表", description = "根据条件查询角色部门关联列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<RoleDeptEntity> searchByPage(@RequestBody RoleDeptConditionEntity roleDeptConditionEntity) {
		return roleDeptService.searchByPage(roleDeptConditionEntity);
	}


	/**
     * 添加角色部门关联
     *
     * @param roleDeptEntity 角色部门关联实体
     * @return 影响行数
     */
	@Operation(summary = "添加角色部门关联", description = "添加角色部门关联")
	@PostMapping("/insert")
	public int insert(@RequestBody RoleDeptEntity roleDeptEntity) {
		return roleDeptService.insert(roleDeptEntity);
	}

	/**
     * 修改角色部门关联
     *
     * @param roleDeptEntity 角色部门关联实体
     * @return 影响行数
     */
	@Operation(summary = "修改角色部门关联", description = "修改角色部门关联")
	@PostMapping("/update")
	public int update(@RequestBody RoleDeptEntity roleDeptEntity) {
		return roleDeptService.update(roleDeptEntity);
	}

	/**
     * 删除角色部门关联
     *
     * @param id 角色部门关联ID
     * @return 影响行数
     */
	@Operation(summary = "删除角色部门关联", description = "删除角色部门关联")
	@PostMapping("/deleteById")
	public int deleteById(@RequestBody @NotNull Long id) {
		return roleDeptService.deleteById(id);
	}
}

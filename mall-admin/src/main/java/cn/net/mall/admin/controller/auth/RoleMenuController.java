package cn.net.mall.admin.controller.auth;

import cn.net.mall.admin.entity.auth.RoleMenuConditionEntity;
import cn.net.mall.admin.entity.auth.RoleMenuEntity;
import cn.net.mall.admin.service.auth.RoleMenuService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 角色菜单关联 接口层
 * 
 * @date 2024-01-08 17:18:18
 */
@Tag(name = "角色菜单管理", description = "管理后台：角色菜单权限")
@RestController
@RequestMapping("/v1/auth/role-menu")
public class RoleMenuController {
	
	private RoleMenuService roleMenuService;

	public RoleMenuController(RoleMenuService roleMenuService) {
		this.roleMenuService = roleMenuService;
	}

	/**
	 * 通过id查询角色菜单关联信息
	 *
	 * @param id 系统ID
	 * @return 角色菜单关联信息
	 */
	@Operation(summary = "通过id查询角色菜单关联信息", description = "通过id查询角色菜单关联信息")
	@GetMapping("/findById")
	public RoleMenuEntity findById(Long id) {
		return roleMenuService.findById(id);
	}

	/**
    * 根据条件查询角色菜单关联列表
    *
    * @param roleMenuConditionEntity 条件
    * @return 角色菜单关联列表
    */
	@Operation(summary = "根据条件查询角色菜单关联列表", description = "根据条件查询角色菜单关联列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<RoleMenuEntity> searchByPage(@RequestBody RoleMenuConditionEntity roleMenuConditionEntity) {
		return roleMenuService.searchByPage(roleMenuConditionEntity);
	}


	/**
     * 添加角色菜单关联
     *
     * @param roleMenuEntity 角色菜单关联实体
     * @return 影响行数
     */
	@Operation(summary = "添加角色菜单关联", description = "添加角色菜单关联")
	@PreAuthorize("hasRole('admin')")
	@PostMapping("/insert")
	public int insert(@RequestBody RoleMenuEntity roleMenuEntity) {
		return roleMenuService.insert(roleMenuEntity);
	}

	/**
     * 修改角色菜单关联
     *
     * @param roleMenuEntity 角色菜单关联实体
     * @return 影响行数
     */
	@Operation(summary = "修改角色菜单关联", description = "修改角色菜单关联")
	@PreAuthorize("hasRole('admin')")
	@PostMapping("/update")
	public int update(@RequestBody RoleMenuEntity roleMenuEntity) {
		return roleMenuService.update(roleMenuEntity);
	}

	/**
     * 删除角色菜单关联
     *
     * @param id 角色菜单关联ID
     * @return 影响行数
     */
	@Operation(summary = "删除角色菜单关联", description = "删除角色菜单关联")
	@PreAuthorize("hasRole('admin')")
	@PostMapping("/deleteById")
	public int deleteById(@RequestBody @NotNull Long id) {
		return roleMenuService.deleteById(id);
	}
}

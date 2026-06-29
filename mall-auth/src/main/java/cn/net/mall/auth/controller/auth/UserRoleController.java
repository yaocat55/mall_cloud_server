package cn.net.mall.auth.controller.auth;

import cn.net.mall.auth.entity.auth.UserRoleConditionEntity;
import cn.net.mall.auth.entity.auth.UserRoleEntity;
import cn.net.mall.auth.service.auth.UserRoleService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;


/**
 * 用户角色关联 接口层
 * 
 * @date 2024-01-08 17:18:19
 */
@Tag(name = "用户角色管理", description = "管理后台：用户角色关联")
@RestController
@RequestMapping("/v1/userRole")
public class UserRoleController {
	
	private final UserRoleService userRoleService;

	public UserRoleController(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	/**
	 * 通过id查询用户角色关联信息
	 *
	 * @param id 系统ID
	 * @return 用户角色关联信息
	 */
	@Operation(summary = "通过id查询用户角色关联信息", description = "通过id查询用户角色关联信息")
	@GetMapping("/findById")
	public UserRoleEntity findById(Long id) {
		return userRoleService.findById(id);
	}

	/**
    * 根据条件查询用户角色关联列表
    *
    * @param userRoleConditionEntity 条件
    * @return 用户角色关联列表
    */
	@Operation(summary = "根据条件查询用户角色关联列表", description = "根据条件查询用户角色关联列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<UserRoleEntity> searchByPage(@RequestBody UserRoleConditionEntity userRoleConditionEntity) {
		return userRoleService.searchByPage(userRoleConditionEntity);
	}


	/**
     * 添加用户角色关联
     *
     * @param userRoleEntity 用户角色关联实体
     * @return 影响行数
     */
	@Operation(summary = "添加用户角色关联", description = "添加用户角色关联")
	@PostMapping("/insert")
	public int insert(@RequestBody UserRoleEntity userRoleEntity) {
		return userRoleService.insert(userRoleEntity);
	}

	/**
     * 修改用户角色关联
     *
     * @param userRoleEntity 用户角色关联实体
     * @return 影响行数
     */
	@Operation(summary = "修改用户角色关联", description = "修改用户角色关联")
	@PostMapping("/update")
	public int update(@RequestBody UserRoleEntity userRoleEntity) {
		return userRoleService.update(userRoleEntity);
	}

	/**
     * 删除用户角色关联
     *
     * @param id 用户角色关联ID
     * @return 影响行数
     */
	@Operation(summary = "删除用户角色关联", description = "删除用户角色关联")
	@PostMapping("/deleteById")
	public int deleteById(@RequestBody @NotNull Long id) {
		return userRoleService.deleteById(id);
	}
}

package cn.net.mall.admin.controller.auth;

import cn.net.mall.admin.entity.auth.UserAvatarConditionEntity;
import cn.net.mall.admin.entity.auth.UserAvatarEntity;
import cn.net.mall.admin.service.auth.UserAvatarService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;


/**
 * 用户头像 接口层
 * 
 * @date 2024-01-08 17:18:18
 */
@Tag(name = "用户头像管理", description = "管理后台：用户头像上传/更新")
@RestController
@RequestMapping("/v1/userAvatar")
public class UserAvatarController {
	
	private UserAvatarService userAvatarService;

	public UserAvatarController(UserAvatarService userAvatarService) {
		this.userAvatarService = userAvatarService;
	}

	/**
	 * 通过id查询用户头像信息
	 *
	 * @param id 系统ID
	 * @return 用户头像信息
	 */
	@Operation(summary = "通过id查询用户头像信息", description = "通过id查询用户头像信息")
	@GetMapping("/findById")
	public UserAvatarEntity findById(Long id) {
		return userAvatarService.findById(id);
	}

	/**
    * 根据条件查询用户头像列表
    *
    * @param userAvatarConditionEntity 条件
    * @return 用户头像列表
    */
	@Operation(summary = "根据条件查询用户头像列表", description = "根据条件查询用户头像列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<UserAvatarEntity> searchByPage(@RequestBody UserAvatarConditionEntity userAvatarConditionEntity) {
		return userAvatarService.searchByPage(userAvatarConditionEntity);
	}


	/**
     * 添加用户头像
     *
     * @param userAvatarEntity 用户头像实体
     * @return 影响行数
     */
	@Operation(summary = "添加用户头像", description = "添加用户头像")
	@PostMapping("/insert")
	public int insert(@RequestBody UserAvatarEntity userAvatarEntity) {
		return userAvatarService.insert(userAvatarEntity);
	}

	/**
     * 修改用户头像
     *
     * @param userAvatarEntity 用户头像实体
     * @return 影响行数
     */
	@Operation(summary = "修改用户头像", description = "修改用户头像")
	@PostMapping("/update")
	public int update(@RequestBody UserAvatarEntity userAvatarEntity) {
		return userAvatarService.update(userAvatarEntity);
	}

	/**
     * 删除用户头像
     *
     * @param id 用户头像ID
     * @return 影响行数
     */
	@Operation(summary = "删除用户头像", description = "删除用户头像")
	@PostMapping("/deleteById")
	public int deleteById(@RequestBody @NotNull Long id) {
		return userAvatarService.deleteById(id);
	}
}

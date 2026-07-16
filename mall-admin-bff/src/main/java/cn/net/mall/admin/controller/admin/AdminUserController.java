package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.dto.IdsDTO;
import cn.net.mall.admin.client.UserFeignClient;
import cn.net.mall.admin.dto.*;
import cn.net.mall.admin.client.*;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 管理后台用户管理 BFF 控制器
 *
* 提供用户管理相关接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/user")
@RequiredArgsConstructor
@Tag(name = "系统管理", description = "用户管理、角色、部门、岗位查询")
public class AdminUserController {

    private final UserFeignClient userFeignClient;

    // ==================== 用户列表（聚合） ====================

    @Operation(summary = "分页查询用户列表",
               description = "分页查询用户列表，前端通过 /system/dept/tree 获取部门树、/system/role/all 获取角色列表",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/page")
    public ApiResult<ResponsePageEntity<?>> userPage(@RequestBody Map<String, Object> condition) {
        return ApiResultUtil.success(userFeignClient.searchByPage(condition));
    }

    // ==================== 用户增删改（直通） ====================

    @Operation(summary = "新增用户", description = "创建新用户，含账号密码等基本信息", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/insert")
    public ApiResult<Integer> insert(@RequestBody UserDTO entity) {
        return ApiResultUtil.success(userFeignClient.insert(entity).getRows());
    }

    @Operation(summary = "修改用户", description = "更新用户信息", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/update")
    public ApiResult<Integer> update(@RequestBody UserDTO entity) {
        return ApiResultUtil.success(userFeignClient.update(entity).getRows());
    }

    @Operation(summary = "删除用户", description = "批量删除用户", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/delete")
    public ApiResult<Integer> delete(@RequestBody IdsDTO dto) {
        return ApiResultUtil.success(userFeignClient.deleteByIds(dto.getIds()).getRows());
    }

    // ==================== 用户信息 ====================

    @Operation(summary = "通过ID查询用户信息", description = "批量通过ID查询用户信息")
    @GetMapping("/findByIds")
    public ApiResult<List<UserDTO>> findByIds(@RequestBody List<Long> ids) {
        return ApiResultUtil.success(userFeignClient.findByIds(ids));
    }

    @Operation(summary = "通过手机号查询用户信息")
    @GetMapping("/findByPhone")
    public ApiResult<UserDTO> findByPhone(@RequestParam String phone) {
        return ApiResultUtil.success(userFeignClient.findByPhone(phone));
    }

    @Operation(summary = "更新用户头像")
    @PostMapping("/updateAvatar")
    public ApiResult<Void> updateAvatar(@Valid @RequestBody UserAvatarDTO userAvatarDTO) {
        userFeignClient.updateAvatar(userAvatarDTO);
        return ApiResultUtil.success();
    }

    @Operation(summary = "更新用户信息")
    @PostMapping("/updateUser")
    public ApiResult<Void> updateUser(@Valid @RequestBody UpdateUserDTO updateUserDTO) {
        userFeignClient.updateUser(updateUserDTO);
        return ApiResultUtil.success();
    }

    // ==================== 用户编辑数据 ====================

    @Operation(summary = "通过ID查询用户信息",
               description = "根据用户ID查询用户基本信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/{id}/edit-data")
    public ApiResult<UserDTO> getUserEditData(@PathVariable Long id) {
        List<UserDTO> users = userFeignClient.findByIds(Collections.singletonList(id));
        return ApiResultUtil.success(users != null && !users.isEmpty() ? users.get(0) : null);
    }
}

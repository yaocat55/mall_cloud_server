package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.client.DeptFeignClient;
import cn.net.mall.admin.client.JobFeignClient;
import cn.net.mall.admin.client.RoleFeignClient;
import cn.net.mall.admin.client.UserFeignClient;
import cn.net.mall.admin.dto.UserEditDataDTO;
import cn.net.mall.admin.dto.*;
import cn.net.mall.admin.client.*;
import cn.net.mall.admin.dto.auth.DeptTreeDTO;
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
* 聚合用户信息、角色、部门、岗位等数据，提供管理后台所需的用户管理接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/user")
@RequiredArgsConstructor
@Tag(name = "管理后台-用户管理", description = "用户信息、角色、部门、岗位等管理接口")
public class AdminUserController {

    private final UserFeignClient userFeignClient;
    private final RoleFeignClient roleFeignClient;
    private final DeptFeignClient deptFeignClient;
    private final JobFeignClient jobFeignClient;

    // ==================== 用户列表（聚合） ====================

    @Operation(summary = "分页查询用户列表（聚合）",
               description = "聚合用户列表 + 部门树 + 角色列表，前端一次调用即可渲染用户管理页面",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/page")
    public ApiResult<Map<String, Object>> userPage(@RequestBody Map<String, Object> condition) {
        Map<String, Object> result = new HashMap<>();

        // 1. 用户列表
        try {
            result.put("users", userFeignClient.searchByPage(condition));
        } catch (Exception e) {
            log.warn("获取用户列表失败", e);
            result.put("users", null);
        }

        // 2. 部门树（用于搜索筛选）
        try {
            result.put("deptTree", deptFeignClient.searchByTree(Collections.emptyMap()));
        } catch (Exception e) {
            log.warn("获取部门树失败", e);
            result.put("deptTree", Collections.emptyList());
        }

        // 3. 角色列表（用于搜索筛选）
        try {
            result.put("roles", roleFeignClient.all());
        } catch (Exception e) {
            log.warn("获取角色列表失败", e);
            result.put("roles", Collections.emptyList());
        }

        return ApiResultUtil.success(result);
    }

    // ==================== 用户增删改（直通） ====================

    @Operation(summary = "新增用户", description = "创建新用户，含账号密码等基本信息", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/insert")
    public ApiResult<Integer> insert(@RequestBody Object entity) {
        return ApiResultUtil.success(userFeignClient.insert(entity).getRows());
    }

    @Operation(summary = "修改用户", description = "更新用户信息", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/update")
    public ApiResult<Integer> update(@RequestBody Object entity) {
        return ApiResultUtil.success(userFeignClient.update(entity).getRows());
    }

    @Operation(summary = "删除用户", description = "批量删除用户", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/delete")
    public ApiResult<Integer> delete(@RequestBody List<Long> ids) {
        return ApiResultUtil.success(userFeignClient.deleteByIds(ids).getRows());
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

    // ==================== 聚合接口 ====================

    @Operation(summary = "获取用户编辑页数据",
               description = "聚合用户基本信息 + 角色列表 + 部门树 + 岗位列表，支持管理后台用户编辑页面\n\n"
                           + "**注意事项：**\n"
                           + "- 需携带 Bearer Token（Authorization 请求头）",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/{id}/edit-data")
    public ApiResult<UserEditDataDTO> getUserEditData(@PathVariable Long id) {
        UserEditDataDTO result = new UserEditDataDTO();

        // 1. 用户基本信息
        try {
            List<UserDTO> users = userFeignClient.findByIds(Collections.singletonList(id));
            result.setUser(users != null && !users.isEmpty() ? users.get(0) : null);
        } catch (Exception e) {
            log.warn("获取用户信息失败, id={}", id, e);
            result.setUser(null);
        }

        // 2. 所有角色（用于角色分配下拉框）
        try {
            result.setRoles(roleFeignClient.all());
        } catch (Exception e) {
            log.warn("获取角色列表失败", e);
            result.setRoles(Collections.emptyList());
        }

        // 3. 部门树（用于部门选择）
        try {
            result.setDeptTree(deptFeignClient.searchByTree(Collections.emptyMap()));
        } catch (Exception e) {
            log.warn("获取部门树失败", e);
            result.setDeptTree(Collections.emptyList());
        }

        // 4. 所有岗位（用于岗位分配下拉框）
        try {
            result.setJobs(jobFeignClient.all());
        } catch (Exception e) {
            log.warn("获取岗位列表失败", e);
            result.setJobs(Collections.emptyList());
        }

        return ApiResultUtil.success(result);
    }
}

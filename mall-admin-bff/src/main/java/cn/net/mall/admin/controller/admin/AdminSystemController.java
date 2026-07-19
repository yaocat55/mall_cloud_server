package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.client.*;
import cn.net.mall.admin.dto.MenuConditionDTO;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/v1/system")
@RequiredArgsConstructor
@Tag(name = "系统管理", description = "用户管理、角色、部门、岗位管理")
public class AdminSystemController {
    private final RoleFeignClient roleFeignClient;
    private final MenuFeignClient menuFeignClient;
    private final DeptFeignClient deptFeignClient;
    private final JobFeignClient jobFeignClient;

    // ========== 角色管理 ==========

    @Operation(summary = "分页查询角色", description = "按条件分页查询角色列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/role/page")
    public ApiResult<ResponsePageEntity<?>> rolePage(@RequestBody Map c) { return ApiResultUtil.success(roleFeignClient.searchByPage(c)); }

    @Operation(summary = "查询所有角色", description = "获取所有角色列表", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/role/all")
    public ApiResult<List> allRoles() { return ApiResultUtil.success(roleFeignClient.all()); }

    // ========== 菜单管理 ==========

    @Operation(summary = "获取菜单树", description = "获取完整的菜单树结构", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/menu/tree")
    public ApiResult<List> getMenuTree() { return ApiResultUtil.success(menuFeignClient.getMenuTree()); }

    @Operation(summary = "查询菜单列表", description = "按条件查询菜单列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/menu/list")
    public ApiResult<List> getMenuList(@RequestBody MenuConditionDTO c) { return ApiResultUtil.success(menuFeignClient.getMenu(c)); }

    // ========== 部门管理 ==========

    @Operation(summary = "分页查询部门", description = "按条件分页查询部门列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dept/page")
    public ApiResult<ResponsePageEntity<?>> deptPage(@RequestBody Map c) { return ApiResultUtil.success(deptFeignClient.searchByPage(c)); }

    @Operation(summary = "查询部门树", description = "获取完整的部门树结构", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/dept/tree")
    public ApiResult<List> getDeptTree() { return ApiResultUtil.success(deptFeignClient.searchByTree(Collections.emptyMap())); }

    // ========== 岗位管理 ==========

    @Operation(summary = "分页查询岗位", description = "按条件分页查询岗位列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/page")
    public ApiResult<ResponsePageEntity<?>> jobPage(@RequestBody Map c) { return ApiResultUtil.success(jobFeignClient.searchByPage(c)); }

    @Operation(summary = "查询所有岗位", description = "获取所有岗位列表", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/job/all")
    public ApiResult<List> allJobs() { return ApiResultUtil.success(jobFeignClient.all()); }

}

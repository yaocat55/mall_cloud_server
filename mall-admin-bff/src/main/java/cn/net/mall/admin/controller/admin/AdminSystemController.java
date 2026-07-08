package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.client.*;
import cn.net.mall.admin.dto.*;
import cn.net.mall.admin.client.*;
import cn.net.mall.admin.dto.auth.DeptTreeDTO;
import cn.net.mall.admin.dto.auth.MenuTreeDTO;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/v1/system")
@RequiredArgsConstructor
@Tag(name = "管理后台-系统管理", description = "角色、菜单、部门、岗位管理接口，需携带 Bearer Token")
public class AdminSystemController {
    private final RoleFeignClient roleFeignClient;
    private final MenuFeignClient menuFeignClient;
    private final DeptFeignClient deptFeignClient;
    private final JobFeignClient jobFeignClient;

    @Operation(summary = "分页查询角色", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/role/page")
    public ApiResult<ResponsePageEntity<?>> searchRolePage(@RequestBody Map c) { return ApiResultUtil.success(roleFeignClient.searchByPage(c)); }
    @Operation(summary = "查询所有角色", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/role/all")
    public ApiResult<List> allRoles() { return ApiResultUtil.success(roleFeignClient.all()); }
    @Operation(summary = "新增角色", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/role/insert")
    public ApiResult<Integer> insertRole(@RequestBody Object e) { return ApiResultUtil.success(roleFeignClient.insert(e)); }
    @Operation(summary = "修改角色", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/role/update")
    public ApiResult<Integer> updateRole(@RequestBody Object e) { return ApiResultUtil.success(roleFeignClient.update(e)); }
    @Operation(summary = "删除角色", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/role/delete")
    public ApiResult<Integer> deleteRole(@RequestBody @NotNull List ids) { return ApiResultUtil.success(roleFeignClient.deleteByIds(ids)); }

    @Operation(summary = "分页查询菜单", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/menu/page")
    public ApiResult<ResponsePageEntity<?>> searchMenuPage(@RequestBody Map c) { return ApiResultUtil.success(menuFeignClient.searchByPage(c)); }
    @Operation(summary = "获取菜单树", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/menu/tree")
    public ApiResult<List> getMenuTree() { return ApiResultUtil.success(menuFeignClient.getMenuTree()); }
    @Operation(summary = "查询菜单列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/menu/list")
    public ApiResult<List> getMenuList(@RequestBody Map c) { return ApiResultUtil.success(menuFeignClient.getMenu(c)); }
    @Operation(summary = "新增菜单", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/menu/insert")
    public ApiResult<Integer> insertMenu(@RequestBody Object e) { return ApiResultUtil.success(menuFeignClient.insert(e)); }
    @Operation(summary = "修改菜单", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/menu/update")
    public ApiResult<Integer> updateMenu(@RequestBody Object e) { return ApiResultUtil.success(menuFeignClient.update(e)); }
    @Operation(summary = "删除菜单", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/menu/delete")
    public ApiResult<Integer> deleteMenu(@RequestBody @NotNull List ids) { return ApiResultUtil.success(menuFeignClient.deleteByIds(ids)); }

    @Operation(summary = "分页查询部门", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dept/page")
    public ApiResult<ResponsePageEntity<?>> searchDeptPage(@RequestBody Map c) { return ApiResultUtil.success(deptFeignClient.searchByPage(c)); }
    @Operation(summary = "查询部门树", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dept/tree")
    public ApiResult<List> getDeptTree(@RequestBody Map p) { return ApiResultUtil.success(deptFeignClient.searchByTree(p)); }
    @Operation(summary = "新增部门", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dept/insert")
    public ApiResult<Integer> insertDept(@RequestBody Object e) { return ApiResultUtil.success(deptFeignClient.insert(e)); }
    @Operation(summary = "修改部门", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dept/update")
    public ApiResult<Integer> updateDept(@RequestBody Object e) { return ApiResultUtil.success(deptFeignClient.update(e)); }
    @Operation(summary = "删除部门", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dept/delete")
    public ApiResult<Integer> deleteDept(@RequestBody @NotNull List ids) { return ApiResultUtil.success(deptFeignClient.deleteByIds(ids)); }

    @Operation(summary = "分页查询岗位", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/page")
    public ApiResult<ResponsePageEntity<?>> searchJobPage(@RequestBody Map c) { return ApiResultUtil.success(jobFeignClient.searchByPage(c)); }
    @Operation(summary = "查询所有岗位", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/job/all")
    public ApiResult<List> allJobs() { return ApiResultUtil.success(jobFeignClient.all()); }
    @Operation(summary = "新增岗位", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/insert")
    public ApiResult<Integer> insertJob(@RequestBody Object e) { return ApiResultUtil.success(jobFeignClient.insert(e)); }
    @Operation(summary = "修改岗位", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/update")
    public ApiResult<Integer> updateJob(@RequestBody Object e) { return ApiResultUtil.success(jobFeignClient.update(e)); }
    @Operation(summary = "删除岗位", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/delete")
    public ApiResult<Integer> deleteJob(@RequestBody @NotNull List ids) { return ApiResultUtil.success(jobFeignClient.deleteByIds(ids)); }
}

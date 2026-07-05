package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.client.*;
import cn.net.mall.admin.dto.*;
import cn.net.mall.admin.client.*;
import cn.net.mall.admin.dto.auth.DeptTreeDTO;
import cn.net.mall.admin.dto.auth.MenuTreeDTO;
import cn.net.mall.entity.ResponsePageEntity;
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
    public ResponsePageEntity<?> searchRolePage(@RequestBody Map c) { return roleFeignClient.searchByPage(c); }
    @Operation(summary = "查询所有角色", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/role/all")
    public List allRoles() { return roleFeignClient.all(); }
    @Operation(summary = "新增角色", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/role/insert")
    public int insertRole(@RequestBody Object e) { return roleFeignClient.insert(e); }
    @Operation(summary = "修改角色", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/role/update")
    public int updateRole(@RequestBody Object e) { return roleFeignClient.update(e); }
    @Operation(summary = "删除角色", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/role/delete")
    public int deleteRole(@RequestBody @NotNull List ids) { return roleFeignClient.deleteByIds(ids); }

    @Operation(summary = "分页查询菜单", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/menu/page")
    public ResponsePageEntity<?> searchMenuPage(@RequestBody Map c) { return menuFeignClient.searchByPage(c); }
    @Operation(summary = "获取菜单树", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/menu/tree")
    public List getMenuTree() { return menuFeignClient.getMenuTree(); }
    @Operation(summary = "查询菜单列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/menu/list")
    public List getMenuList(@RequestBody Map c) { return menuFeignClient.getMenu(c); }
    @Operation(summary = "新增菜单", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/menu/insert")
    public int insertMenu(@RequestBody Object e) { return menuFeignClient.insert(e); }
    @Operation(summary = "修改菜单", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/menu/update")
    public int updateMenu(@RequestBody Object e) { return menuFeignClient.update(e); }
    @Operation(summary = "删除菜单", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/menu/delete")
    public int deleteMenu(@RequestBody @NotNull List ids) { return menuFeignClient.deleteByIds(ids); }

    @Operation(summary = "分页查询部门", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dept/page")
    public ResponsePageEntity<?> searchDeptPage(@RequestBody Map c) { return deptFeignClient.searchByPage(c); }
    @Operation(summary = "查询部门树", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dept/tree")
    public List getDeptTree(@RequestBody Map p) { return deptFeignClient.searchByTree(p); }
    @Operation(summary = "新增部门", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dept/insert")
    public int insertDept(@RequestBody Object e) { return deptFeignClient.insert(e); }
    @Operation(summary = "修改部门", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dept/update")
    public int updateDept(@RequestBody Object e) { return deptFeignClient.update(e); }
    @Operation(summary = "删除部门", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dept/delete")
    public int deleteDept(@RequestBody @NotNull List ids) { return deptFeignClient.deleteByIds(ids); }

    @Operation(summary = "分页查询岗位", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/page")
    public ResponsePageEntity<?> searchJobPage(@RequestBody Map c) { return jobFeignClient.searchByPage(c); }
    @Operation(summary = "查询所有岗位", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/job/all")
    public List allJobs() { return jobFeignClient.all(); }
    @Operation(summary = "新增岗位", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/insert")
    public int insertJob(@RequestBody Object e) { return jobFeignClient.insert(e); }
    @Operation(summary = "修改岗位", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/update")
    public int updateJob(@RequestBody Object e) { return jobFeignClient.update(e); }
    @Operation(summary = "删除岗位", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/job/delete")
    public int deleteJob(@RequestBody @NotNull List ids) { return jobFeignClient.deleteByIds(ids); }
}
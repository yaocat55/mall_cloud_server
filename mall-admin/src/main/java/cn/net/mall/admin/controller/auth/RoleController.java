package cn.net.mall.admin.controller.auth;

import cn.net.mall.admin.entity.auth.RoleConditionEntity;
import cn.net.mall.admin.dto.RowsDTO;
import cn.net.mall.admin.entity.auth.RoleEntity;
import cn.net.mall.admin.service.auth.RoleService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色 接口层
 *
 * @date 2024-01-08 17:18:18
 */
@Tag(name = "角色管理", description = "管理后台：角色 CRUD。除 /all 外均需 Bearer Token")
@RestController
@RequestMapping("/v1/auth/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 通过id查询角色信息
     *
     * @param id 系统ID
     * @return 角色信息
     */
    @Operation(summary = "通过id查询角色信息", description = "需 Bearer Token | 查询参数：id（角色ID）")
    @GetMapping("/findById")
    public RoleEntity findById(Long id) {
        return roleService.findById(id);
    }

    /**
     * 根据条件查询角色列表
     *
     * @param roleConditionEntity 条件
     * @return 角色列表
     */
    @Operation(summary = "分页查询角色列表", description = "需 Bearer Token | 请求体：RoleConditionEntity（分页条件，含 page/pageSize）")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<RoleEntity> searchByPage(@RequestBody RoleConditionEntity roleConditionEntity) {
        return roleService.searchByPage(roleConditionEntity);
    }


    /**
     * 根据查询所有角色
     *
     * @return 角色列表
     */
    @Operation(summary = "查询所有角色", description = "无需认证（公开接口）| 无参，返回全部角色列表")
    @GetMapping("/all")
    public List<RoleEntity> all() {
        return roleService.all();
    }

    /**
     * 添加角色
     *
     * @param roleEntity 角色实体
     * @return 影响行数
     */
    @Operation(summary = "添加角色", description = "需 Bearer Token + admin 角色 | 请求体：RoleEntity（角色信息，含 name/code/sort/status 等）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/insert")
    public RowsDTO insert(@RequestBody RoleEntity roleEntity) {
        return new RowsDTO(roleService.insert(roleEntity));
    }

    /**
     * 修改角色
     *
     * @param roleEntity 角色实体
     * @return 影响行数
     */
    @Operation(summary = "修改角色", description = "需 Bearer Token + admin 角色 | 请求体：RoleEntity（待修改的完整角色信息，含 id）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/update")
    public RowsDTO update(@RequestBody RoleEntity roleEntity) {
        return new RowsDTO(roleService.update(roleEntity));
    }

    /**
     * 批量删除角色
     *
     * @param ids 角色ID
     * @return 影响行数
     */
    @Operation(summary = "批量删除角色", description = "需 Bearer Token + admin 角色 | 请求体：ids（角色ID列表）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return new RowsDTO(roleService.deleteByIds(ids));
    }

}

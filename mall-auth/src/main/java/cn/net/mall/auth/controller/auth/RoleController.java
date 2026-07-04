package cn.net.mall.auth.controller.auth;

import cn.net.mall.auth.entity.auth.RoleConditionEntity;
import cn.net.mall.auth.entity.auth.RoleEntity;
import cn.net.mall.auth.service.auth.RoleService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色 接口层
 *
 * @date 2024-01-08 17:18:18
 */
@Tag(name = "角色管理", description = "管理后台：角色 CRUD")
@RestController
@RequestMapping("/v1/role")
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
    @Operation(summary = "通过id查询角色信息", description = "通过id查询角色信息")
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
    @Operation(summary = "根据条件查询角色列表", description = "根据条件查询角色列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<RoleEntity> searchByPage(@RequestBody RoleConditionEntity roleConditionEntity) {
        return roleService.searchByPage(roleConditionEntity);
    }


    /**
     * 根据查询所有角色
     *
     * @return 角色列表
     */
    @Operation(summary = "根据查询所有角色", description = "根据查询所有角色")
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
    @Operation(summary = "添加角色", description = "添加角色")
    @PostMapping("/insert")
    public int insert(@RequestBody RoleEntity roleEntity) {
        return roleService.insert(roleEntity);
    }

    /**
     * 修改角色
     *
     * @param roleEntity 角色实体
     * @return 影响行数
     */
    @Operation(summary = "修改角色", description = "修改角色")
    @PostMapping("/update")
    public int update(@RequestBody RoleEntity roleEntity) {
        return roleService.update(roleEntity);
    }

    /**
     * 批量删除角色
     *
     * @param ids 角色ID
     * @return 影响行数
     */
    @Operation(summary = "批量删除角色", description = "批量删除角色")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return roleService.deleteByIds(ids);
    }

}

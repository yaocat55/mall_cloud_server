package cn.net.mall.admin.controller.internal;

import cn.net.mall.admin.dto.RowsDTO;
import cn.net.mall.admin.entity.auth.RoleConditionEntity;
import cn.net.mall.admin.entity.auth.RoleEntity;
import cn.net.mall.admin.service.auth.RoleService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色 内部接口层（供 Feign 调用）
 *
 * @date 2024-01-08 17:18:18
 */
@Tag(name = "内部服务-角色")
@RestController
@RequestMapping("/v1/internal/auth/role")
public class RoleInternalController {

    private final RoleService roleService;

    public RoleInternalController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "通过id查询角色信息", description = "内部服务：根据角色ID查询角色信息")
    @GetMapping("/findById")
    public RoleEntity findById(Long id) {
        return roleService.findById(id);
    }

    @Operation(summary = "分页查询角色列表", description = "内部服务：按条件分页查询角色列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<RoleEntity> searchByPage(@RequestBody RoleConditionEntity roleConditionEntity) {
        return roleService.searchByPage(roleConditionEntity);
    }

    @Operation(summary = "查询所有角色", description = "内部服务：获取全部角色列表")
    @GetMapping("/all")
    public List<RoleEntity> all() {
        return roleService.all();
    }

    @Operation(summary = "添加角色", description = "内部服务：新增角色")
    @PostMapping("/insert")
    public RowsDTO insert(@RequestBody RoleEntity roleEntity) {
        return new RowsDTO(roleService.insert(roleEntity));
    }

    @Operation(summary = "修改角色", description = "内部服务：修改角色信息")
    @PostMapping("/update")
    public RowsDTO update(@RequestBody RoleEntity roleEntity) {
        return new RowsDTO(roleService.update(roleEntity));
    }

    @Operation(summary = "批量删除角色", description = "内部服务：批量删除角色")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return new RowsDTO(roleService.deleteByIds(ids));
    }
}

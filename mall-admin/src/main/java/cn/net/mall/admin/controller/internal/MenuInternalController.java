package cn.net.mall.admin.controller.internal;

import cn.net.mall.admin.dto.RowsDTO;
import cn.net.mall.admin.dto.auth.MenuTreeDTO;
import cn.net.mall.admin.entity.auth.MenuConditionEntity;
import cn.net.mall.admin.entity.auth.MenuEntity;
import cn.net.mall.admin.service.auth.MenuService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单 内部接口层（供 Feign 调用）
 *
 * @date 2024-01-08 17:18:18
 */
@Tag(name = "内部服务-菜单")
@RestController
@RequestMapping("/v1/internal/auth/menu")
public class MenuInternalController {

    private final MenuService menuService;

    public MenuInternalController(MenuService menuService) {
        this.menuService = menuService;
    }

    @Operation(summary = "通过id查询菜单信息", description = "内部服务：根据菜单ID查询菜单信息")
    @GetMapping("/findById")
    public MenuEntity findById(Long id) {
        return menuService.findById(id);
    }

    @Operation(summary = "分页查询菜单列表", description = "内部服务：按条件分页查询菜单列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<MenuEntity> searchByPage(@RequestBody MenuConditionEntity menuConditionEntity) {
        return menuService.searchByPage(menuConditionEntity);
    }

    @Operation(summary = "获取菜单树", description = "内部服务：获取完整递归菜单树")
    @GetMapping("/getMenuTree")
    public List<MenuTreeDTO> getMenuTree() {
        return menuService.getMenuTree();
    }

    @Operation(summary = "获取下级菜单", description = "内部服务：获取指定菜单下的所有子菜单ID")
    @GetMapping("/getChild")
    public List<Long> getChild(@Parameter(description = "菜单ID") @RequestParam("id") Long id) {
        return menuService.getChild(id);
    }

    @Operation(summary = "获取逐级加载的菜单", description = "内部服务：按条件查询菜单，返回当前层级及子节点标记")
    @PostMapping("/getMenu")
    public List<MenuTreeDTO> getMenu(@RequestBody MenuConditionEntity menuConditionEntity) {
        return menuService.getMenu(menuConditionEntity);
    }

    @Operation(summary = "添加菜单", description = "内部服务：新增菜单")
    @PostMapping("/insert")
    public RowsDTO insert(@RequestBody MenuEntity menuEntity) {
        return new RowsDTO(menuService.insert(menuEntity));
    }

    @Operation(summary = "修改菜单", description = "内部服务：修改菜单信息")
    @PostMapping("/update")
    public RowsDTO update(@RequestBody MenuEntity menuEntity) {
        return new RowsDTO(menuService.update(menuEntity));
    }

    @Operation(summary = "批量删除菜单", description = "内部服务：批量删除菜单")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return new RowsDTO(menuService.deleteByIds(ids));
    }
}

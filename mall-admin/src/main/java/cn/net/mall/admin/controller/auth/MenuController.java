package cn.net.mall.admin.controller.auth;

import cn.net.mall.admin.dto.auth.MenuTreeDTO;
import cn.net.mall.admin.entity.auth.MenuConditionEntity;
import cn.net.mall.admin.entity.auth.MenuEntity;
import cn.net.mall.admin.service.auth.MenuService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单 接口层
 *
 * @date 2024-01-08 17:18:18
 */
@Tag(name = "菜单管理", description = "管理后台：菜单权限配置。查 + 增删改均在白名单中公开（无需 Bearer Token，但 insert/update/deleteByIds 需 admin 角色）")
@RestController
@RequestMapping("/v1/auth/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * 通过id查询菜单信息
     *
     * @param id 系统ID
     * @return 菜单信息
     */
    @Operation(summary = "通过id查询菜单信息", description = "需 Bearer Token | 查询参数：id（菜单ID）")
    @GetMapping("/findById")
    public MenuEntity findById(Long id) {
        return menuService.findById(id);
    }

    /**
     * 根据条件查询菜单列表
     *
     * @param menuConditionEntity 条件
     * @return 菜单列表
     */
    @Operation(summary = "分页查询菜单列表", description = "无需认证 | 请求体：MenuConditionEntity（分页条件，含 page/pageSize/pid）")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<MenuEntity> searchByPage(@RequestBody MenuConditionEntity menuConditionEntity) {
        return menuService.searchByPage(menuConditionEntity);
    }

    /**
     * 获取菜单树
     *
     * @return 菜单列表
     */
    @Operation(summary = "获取菜单树", description = "需 Bearer Token | 无参，返回完整递归菜单树（含 children）")
    @GetMapping("/getMenuTree")
    public List<MenuTreeDTO> getMenuTree() {
        return menuService.getMenuTree();
    }


    /**
     * 获取下级菜单
     *
     * @return 菜单列表
     */
    @Operation(summary = "获取下级菜单", description = "需 Bearer Token | 查询参数：id（菜单ID），返回该菜单下所有子菜单ID")
    @GetMapping("/getChild")
    public List<Long> getChild(@Parameter(description = "菜单ID")
    @RequestParam("id") Long id) {
        return menuService.getChild(id);
    }

    /**
     * 获取逐级加载的菜单
     *
     * @return 菜单列表
     */
    @Operation(summary = "获取逐级加载的菜单", description = "需 Bearer Token | 请求体：MenuConditionEntity（分页条件，含 pid/sortField 等），返回当前层级 + 子节点标记")
    @PostMapping("/getMenu")
    public List<MenuTreeDTO> getMenu(@RequestBody MenuConditionEntity menuConditionEntity) {
        return menuService.getMenu(menuConditionEntity);
    }

    /**
     * 添加菜单
     *
     * @param menuEntity 菜单实体
     * @return 影响行数
     */
    @Operation(summary = "添加菜单", description = "需 admin 角色 | 请求体：MenuEntity（菜单信息，含 pid/name/type/sort/path 等）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/insert")
    public int insert(@RequestBody MenuEntity menuEntity) {
        return menuService.insert(menuEntity);
    }

    /**
     * 修改菜单
     *
     * @param menuEntity 菜单实体
     * @return 影响行数
     */
    @Operation(summary = "修改菜单", description = "需 admin 角色 | 请求体：MenuEntity（待修改的完整菜单信息，含 id）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/update")
    public int update(@RequestBody MenuEntity menuEntity) {
        return menuService.update(menuEntity);
    }

    /**
     * 批量删除菜单
     *
     * @param ids 菜单ID
     * @return 影响行数
     */
    @Operation(summary = "批量删除菜单", description = "需 admin 角色 | 请求体：ids（菜单ID列表）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return menuService.deleteByIds(ids);
    }
}

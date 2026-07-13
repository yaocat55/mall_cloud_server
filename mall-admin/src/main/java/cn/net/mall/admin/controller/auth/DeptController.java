package cn.net.mall.admin.controller.auth;

import cn.net.mall.admin.dto.auth.DeptTreeDTO;
import cn.net.mall.admin.dto.RowsDTO;
import cn.net.mall.admin.entity.auth.DeptConditionEntity;
import cn.net.mall.admin.entity.auth.DeptEntity;
import cn.net.mall.admin.service.auth.DeptService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门 接口层
 *
 * @date 2024-01-08 17:18:17
 */
@Tag(name = "部门管理", description = "管理后台：部门树形管理。findById / searchByPage / searchByTree 无需认证；insert / update / deleteByIds 需 Bearer Token + admin 角色")
@RestController
@RequestMapping("/v1/auth/dept")
public class DeptController {

    private final DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    /**
     * 通过id查询部门信息
     *
     * @param id 系统ID
     * @return 部门信息
     */
    @Operation(summary = "通过id查询部门信息", description = "无需认证 | 查询参数：id（部门ID）")
    @GetMapping("/findById")
    public DeptEntity findById(Long id) {
        return deptService.findById(id);
    }

    /**
     * 根据条件查询部门列表
     *
     * @param deptConditionEntity 条件
     * @return 部门列表
     */
    @Operation(summary = "分页查询部门列表", description = "无需认证 | 请求体：DeptConditionEntity（分页条件，含 page/pageSize/pid）")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<DeptTreeDTO> searchByPage(@RequestBody DeptConditionEntity deptConditionEntity) {
        return deptService.searchByPage(deptConditionEntity);
    }

    /**
     * 查询部门树
     *
     * @param deptConditionEntity 条件
     * @return 部门树
     */
    @Operation(summary = "查询部门树", description = "无需认证 | 请求体：DeptConditionEntity（查询条件，支持 pid 筛选）")
    @PostMapping("/searchByTree")
    public List<DeptTreeDTO> searchByTree(@RequestBody DeptConditionEntity deptConditionEntity) {
        return deptService.searchByTree(deptConditionEntity);
    }


    /**
     * 添加部门
     *
     * @param deptEntity 部门实体
     * @return 影响行数
     */
    @Operation(summary = "添加部门", description = "需 Bearer Token + admin 角色 | 请求体：DeptEntity（部门信息，含 pid/name/sort/status 等）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/insert")
    public RowsDTO insert(@RequestBody DeptEntity deptEntity) {
        return new RowsDTO(deptService.insert(deptEntity));
    }

    /**
     * 修改部门
     *
     * @param deptEntity 部门实体
     * @return 影响行数
     */
    @Operation(summary = "修改部门", description = "需 Bearer Token + admin 角色 | 请求体：DeptEntity（待修改的完整部门信息，含 id）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/update")
    public RowsDTO update(@RequestBody DeptEntity deptEntity) {
        return new RowsDTO(deptService.update(deptEntity));
    }

    /**
     * 删除部门
     *
     * @param ids 部门ID
     * @return 影响行数
     */
    @Operation(summary = "删除部门", description = "需 Bearer Token + admin 角色 | 请求体：ids（部门ID列表）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return new RowsDTO(deptService.deleteByIds(ids));
    }
}

package cn.net.mall.admin.controller.internal;

import cn.net.mall.admin.dto.RowsDTO;
import cn.net.mall.admin.dto.auth.DeptTreeDTO;
import cn.net.mall.admin.entity.auth.DeptConditionEntity;
import cn.net.mall.admin.entity.auth.DeptEntity;
import cn.net.mall.admin.service.auth.DeptService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门 内部接口层（供 Feign 调用）
 *
 * @date 2024-01-08 17:18:17
 */
@Tag(name = "内部服务-部门")
@RestController
@RequestMapping("/v1/internal/auth/dept")
public class DeptInternalController {

    private final DeptService deptService;

    public DeptInternalController(DeptService deptService) {
        this.deptService = deptService;
    }

    @Operation(summary = "通过id查询部门信息", description = "内部服务：根据部门ID查询部门信息")
    @GetMapping("/findById")
    public DeptEntity findById(Long id) {
        return deptService.findById(id);
    }

    @Operation(summary = "分页查询部门列表", description = "内部服务：按条件分页查询部门列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<DeptTreeDTO> searchByPage(@RequestBody DeptConditionEntity deptConditionEntity) {
        return deptService.searchByPage(deptConditionEntity);
    }

    @Operation(summary = "查询部门树", description = "内部服务：按条件查询部门树")
    @PostMapping("/searchByTree")
    public List<DeptTreeDTO> searchByTree(@RequestBody DeptConditionEntity deptConditionEntity) {
        return deptService.searchByTree(deptConditionEntity);
    }

    @Operation(summary = "添加部门", description = "内部服务：新增部门")
    @PostMapping("/insert")
    public RowsDTO insert(@RequestBody DeptEntity deptEntity) {
        return new RowsDTO(deptService.insert(deptEntity));
    }

    @Operation(summary = "修改部门", description = "内部服务：修改部门信息")
    @PostMapping("/update")
    public RowsDTO update(@RequestBody DeptEntity deptEntity) {
        return new RowsDTO(deptService.update(deptEntity));
    }

    @Operation(summary = "删除部门", description = "内部服务：批量删除部门")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return new RowsDTO(deptService.deleteByIds(ids));
    }
}

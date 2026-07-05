package cn.net.mall.admin.controller.auth;

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
 * 部门 接口层
 *
 * @date 2024-01-08 17:18:17
 */
@Tag(name = "部门管理", description = "管理后台：部门树形管理")
@RestController
@RequestMapping("/v1/dept")
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
    @Operation(summary = "通过id查询部门信息", description = "通过id查询部门信息")
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
    @Operation(summary = "根据条件查询部门列表", description = "根据条件查询部门列表")
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
    @Operation(summary = "查询部门树", description = "查询部门树")
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
    @Operation(summary = "添加部门", description = "添加部门")
    @PostMapping("/insert")
    public int insert(@RequestBody DeptEntity deptEntity) {
        return deptService.insert(deptEntity);
    }

    /**
     * 修改部门
     *
     * @param deptEntity 部门实体
     * @return 影响行数
     */
    @Operation(summary = "修改部门", description = "修改部门")
    @PostMapping("/update")
    public int update(@RequestBody DeptEntity deptEntity) {
        return deptService.update(deptEntity);
    }

    /**
     * 删除部门
     *
     * @param ids 部门ID
     * @return 影响行数
     */
    @Operation(summary = "删除部门", description = "删除部门")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return deptService.deleteByIds(ids);
    }
}

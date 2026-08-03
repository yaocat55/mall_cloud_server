package cn.net.mall.pay.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.pay.dto.RowsDTO;
import cn.net.mall.pay.entity.ReconResultEntity;
import cn.net.mall.pay.entity.ReconResultConditionEntity;
import cn.net.mall.pay.service.ReconResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 对账逐笔结果 接口层（管理端）
 */
@Tag(name = "对账逐笔结果", description = "对账逐笔结果：分页查询、新增、修改、删除")
@RestController
@RequestMapping("/v1/admin/reconResult")
public class ReconResultController {

    private final ReconResultService reconResultservice;

    public ReconResultController(ReconResultService reconResultservice) {
        this.reconResultservice = reconResultservice;
    }

    @Operation(summary = "通过id查询对账逐笔结果")
    @GetMapping("/{id}")
    public ReconResultEntity findById(@PathVariable Long id) {
        return reconResultservice.findById(id);
    }

    @Operation(summary = "分页查询对账逐笔结果")
    @PostMapping("/page")
    public ResponsePageEntity<ReconResultEntity> searchByPage(@RequestBody ReconResultConditionEntity condition) {
        return reconResultservice.searchByPage(condition);
    }

    @Operation(summary = "新增对账逐笔结果")
    @PostMapping("/insert")
    public RowsDTO insert(@RequestBody ReconResultEntity entity) {
        return new RowsDTO(reconResultservice.insert(entity));
    }

    @Operation(summary = "修改对账逐笔结果")
    @PostMapping("/update")
    public RowsDTO update(@RequestBody ReconResultEntity entity) {
        return new RowsDTO(reconResultservice.update(entity));
    }

    @Operation(summary = "批量删除对账逐笔结果")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return new RowsDTO(reconResultservice.deleteByIds(ids));
    }
}

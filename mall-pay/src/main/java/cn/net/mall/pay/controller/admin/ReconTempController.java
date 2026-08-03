package cn.net.mall.pay.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.pay.dto.RowsDTO;
import cn.net.mall.pay.entity.ReconTempEntity;
import cn.net.mall.pay.entity.ReconTempConditionEntity;
import cn.net.mall.pay.service.ReconTempService;
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
 * 对账临时数据 接口层（管理端）
 */
@Tag(name = "对账临时数据", description = "对账临时数据：分页查询、新增、修改、删除")
@RestController
@RequestMapping("/v1/admin/reconTemp")
public class ReconTempController {

    private final ReconTempService reconTempservice;

    public ReconTempController(ReconTempService reconTempservice) {
        this.reconTempservice = reconTempservice;
    }

    @Operation(summary = "通过id查询对账临时数据")
    @GetMapping("/{id}")
    public ReconTempEntity findById(@PathVariable Long id) {
        return reconTempservice.findById(id);
    }

    @Operation(summary = "分页查询对账临时数据")
    @PostMapping("/page")
    public ResponsePageEntity<ReconTempEntity> searchByPage(@RequestBody ReconTempConditionEntity condition) {
        return reconTempservice.searchByPage(condition);
    }

    @Operation(summary = "新增对账临时数据")
    @PostMapping("/insert")
    public RowsDTO insert(@RequestBody ReconTempEntity entity) {
        return new RowsDTO(reconTempservice.insert(entity));
    }

    @Operation(summary = "修改对账临时数据")
    @PostMapping("/update")
    public RowsDTO update(@RequestBody ReconTempEntity entity) {
        return new RowsDTO(reconTempservice.update(entity));
    }

    @Operation(summary = "批量删除对账临时数据")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return new RowsDTO(reconTempservice.deleteByIds(ids));
    }
}

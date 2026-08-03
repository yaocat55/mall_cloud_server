package cn.net.mall.pay.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.pay.dto.RowsDTO;
import cn.net.mall.pay.entity.ReconBatchEntity;
import cn.net.mall.pay.entity.ReconBatchConditionEntity;
import cn.net.mall.pay.service.ReconBatchService;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
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
 * 对账批次 接口层（管理端）
 */
@Tag(name = "对账批次", description = "对账批次：分页查询、新增、修改、删除")
@RestController
@RequestMapping("/v1/admin/reconBatch")
public class ReconBatchController {

    private final ReconBatchService reconBatchservice;

    public ReconBatchController(ReconBatchService reconBatchservice) {
        this.reconBatchservice = reconBatchservice;
    }

    @Operation(summary = "通过id查询对账批次")
    @GetMapping("/{id}")
    public ApiResult<ReconBatchEntity> findById(@PathVariable Long id) {
        return ApiResultUtil.success(reconBatchservice.findById(id));
    }

    @Operation(summary = "分页查询对账批次")
    @PostMapping("/page")
    public ApiResult<ResponsePageEntity<ReconBatchEntity>> searchByPage(@RequestBody ReconBatchConditionEntity condition) {
        return ApiResultUtil.success(reconBatchservice.searchByPage(condition));
    }

    @Operation(summary = "新增对账批次")
    @PostMapping("/insert")
    public ApiResult<RowsDTO> insert(@RequestBody ReconBatchEntity entity) {
        return ApiResultUtil.success(new RowsDTO(reconBatchservice.insert(entity)));
    }

    @Operation(summary = "修改对账批次")
    @PostMapping("/update")
    public ApiResult<RowsDTO> update(@RequestBody ReconBatchEntity entity) {
        return ApiResultUtil.success(new RowsDTO(reconBatchservice.update(entity)));
    }

    @Operation(summary = "批量删除对账批次")
    @PostMapping("/deleteByIds")
    public ApiResult<RowsDTO> deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return ApiResultUtil.success(new RowsDTO(reconBatchservice.deleteByIds(ids)));
    }
}

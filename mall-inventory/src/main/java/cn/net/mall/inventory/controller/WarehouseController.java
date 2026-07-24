package cn.net.mall.inventory.controller;

import cn.net.mall.inventory.dto.RowsDTO;
import cn.net.mall.inventory.entity.WarehouseConditionEntity;
import cn.net.mall.inventory.entity.WarehouseEntity;
import cn.net.mall.inventory.service.WarehouseService;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仓库管理 接口层
 */
@Tag(name = "仓库管理", description = "仓库 CRUD：分页查询、新增、修改、删除")
@RestController
@RequestMapping("/v1/inventory/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Operation(summary = "通过id查询仓库")
    @GetMapping("/{id}")
    public ApiResult<WarehouseEntity> findById(@PathVariable Long id) {
        return ApiResultUtil.success(warehouseService.findById(id));
    }

    @Operation(summary = "分页查询仓库列表")
    @PostMapping("/page")
    public ApiResult<Object> searchByPage(@RequestBody WarehouseConditionEntity condition) {
        return ApiResultUtil.success(warehouseService.searchByPage(condition));
    }

    @Operation(summary = "新增仓库")
    @PostMapping("/insert")
    public ApiResult<RowsDTO> insert(@RequestBody WarehouseEntity entity) {
        return ApiResultUtil.success(new RowsDTO(warehouseService.insert(entity)));
    }

    @Operation(summary = "修改仓库")
    @PostMapping("/update")
    public ApiResult<RowsDTO> update(@RequestBody WarehouseEntity entity) {
        return ApiResultUtil.success(new RowsDTO(warehouseService.update(entity)));
    }

    @Operation(summary = "批量删除仓库")
    @PostMapping("/deleteByIds")
    public ApiResult<RowsDTO> deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return ApiResultUtil.success(new RowsDTO(warehouseService.deleteByIds(ids)));
    }
}

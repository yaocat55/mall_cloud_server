package cn.net.mall.inventory.controller;

import cn.net.mall.inventory.dto.*;
import cn.net.mall.inventory.entity.InventoryConditionEntity;
import cn.net.mall.inventory.entity.InventoryBatchConditionEntity;
import cn.net.mall.inventory.entity.InventoryEntity;
import cn.net.mall.inventory.entity.InventoryLogConditionEntity;
import cn.net.mall.inventory.service.InventoryBatchService;
import cn.net.mall.inventory.service.InventoryLogService;
import cn.net.mall.inventory.service.InventoryService;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存管理接口.
 */
@Tag(name = "库存管理", description = "库存 CRUD + 批次查询 + 流水查询 + 出入库操作")
@RestController
@RequestMapping("/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryBatchService inventoryBatchService;
    private final InventoryLogService inventoryLogService;

    // ==================== 库存 CRUD ====================

    @Operation(summary = "分页查询库存列表")
    @PostMapping("/page")
    public ApiResult<Object> searchByPage(@RequestBody InventoryConditionEntity condition) {
        return ApiResultUtil.success(inventoryService.searchByPage(condition));
    }

    @Operation(summary = "查询商品库存")
    @GetMapping("/{productId}")
    public ApiResult<InventoryDTO> getByProductId(@PathVariable Long productId) {
        return ApiResultUtil.success(inventoryService.getByProductId(productId));
    }

    @Operation(summary = "批量查询库存")
    @PostMapping("/batch")
    public ApiResult<List<InventoryDTO>> getByProductIds(@RequestBody List<Long> productIds) {
        return ApiResultUtil.success(inventoryService.getByProductIds(productIds));
    }

    @Operation(summary = "初始化库存（手动创建）")
    @PostMapping("/init")
    public ApiResult<RowsDTO> initInventory(@RequestBody InventoryEntity entity) {
        return ApiResultUtil.success(new RowsDTO(inventoryService.initInventory(entity)));
    }

    @Operation(summary = "修改库存（手动调整）")
    @PostMapping("/update")
    public ApiResult<RowsDTO> updateInventory(@RequestBody InventoryEntity entity) {
        return ApiResultUtil.success(new RowsDTO(inventoryService.updateInventory(entity)));
    }

    @Operation(summary = "删除库存")
    @PostMapping("/deleteByIds")
    public ApiResult<RowsDTO> deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return ApiResultUtil.success(new RowsDTO(inventoryService.deleteByIds(ids)));
    }

    // ==================== 批次查询 ====================

    @Operation(summary = "分页查询批次列表")
    @PostMapping("/batch/page")
    public ApiResult<Object> batchPage(@RequestBody InventoryBatchConditionEntity condition) {
        return ApiResultUtil.success(inventoryBatchService.searchByPage(condition));
    }

    @Operation(summary = "批次详情")
    @GetMapping("/batch/{id}")
    public ApiResult<Object> batchDetail(@PathVariable Long id) {
        return ApiResultUtil.success(inventoryBatchService.findById(id));
    }

    // ==================== 流水查询 ====================

    @Operation(summary = "分页查询库存流水")
    @PostMapping("/log/page")
    public ApiResult<Object> logPage(@RequestBody InventoryLogConditionEntity condition) {
        return ApiResultUtil.success(inventoryLogService.searchByPage(condition));
    }

    @Operation(summary = "流水详情")
    @GetMapping("/log/{id}")
    public ApiResult<Object> logDetail(@PathVariable Long id) {
        return ApiResultUtil.success(inventoryLogService.findById(id));
    }

    // ==================== 出入库操作 ====================

    @PostMapping("/freeze")
    public ApiResult<RowsDTO> freeze(@Valid @RequestBody InventoryFreezeDTO dto) {
        inventoryService.freeze(dto);
        return ApiResultUtil.success(new RowsDTO(1));
    }

    @PostMapping("/confirm")
    public ApiResult<RowsDTO> confirm(@Valid @RequestBody InventoryConfirmDTO dto) {
        inventoryService.confirm(dto);
        return ApiResultUtil.success(new RowsDTO(1));
    }

    @PostMapping("/unfreeze")
    public ApiResult<RowsDTO> unfreeze(@Valid @RequestBody InventoryUnfreezeDTO dto) {
        inventoryService.unfreeze(dto);
        return ApiResultUtil.success(new RowsDTO(1));
    }

    @PostMapping("/return")
    public ApiResult<RowsDTO> returnStock(@Valid @RequestBody InventoryReturnDTO dto) {
        inventoryService.returnStock(dto);
        return ApiResultUtil.success(new RowsDTO(1));
    }

    @PostMapping("/inbound")
    public ApiResult<RowsDTO> inbound(@Valid @RequestBody InventoryInboundDTO dto) {
        inventoryService.inbound(dto);
        return ApiResultUtil.success(new RowsDTO(1));
    }
}

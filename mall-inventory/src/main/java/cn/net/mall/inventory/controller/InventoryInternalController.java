package cn.net.mall.inventory.controller;

import cn.net.mall.inventory.dto.*;
import cn.net.mall.inventory.entity.*;
import cn.net.mall.inventory.service.InventoryService;
import cn.net.mall.inventory.service.InventoryBatchService;
import cn.net.mall.inventory.service.InventoryLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存内部接口.
 *
 * <p>调用方：order-service、admin-bff 通过 Feign 调用</p>
 * <p>返回裸 DTO（不包 ApiResult），由 {@link cn.net.mall.handler.GlobalApiResultHandler} 自动包装。</p>
 */
@Tag(name = "内部服务-库存", description = "内部微服务：order-service、admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/inventory")
@RequiredArgsConstructor
public class InventoryInternalController {

    private final InventoryService inventoryService;
    private final InventoryBatchService inventoryBatchService;
    private final InventoryLogService inventoryLogService;

    @Operation(summary = "冻结库存", description = "下单时调用，Redis Lua 原子扣减，降级到 MySQL 条件扣减")
    @PostMapping("/freeze")
    public RowsDTO freeze(@Valid @RequestBody InventoryFreezeDTO dto) {
        inventoryService.freeze(dto);
        return new RowsDTO(1);
    }

    @Operation(summary = "确认扣减", description = "支付回调时调用，从冻结库存扣减并走 FIFO 出库")
    @PostMapping("/confirm")
    public RowsDTO confirm(@Valid @RequestBody InventoryConfirmDTO dto) {
        inventoryService.confirm(dto);
        return new RowsDTO(1);
    }

    @Operation(summary = "释放冻结", description = "取消订单/超时时调用，回退冻结库存")
    @PostMapping("/unfreeze")
    public RowsDTO unfreeze(@Valid @RequestBody InventoryUnfreezeDTO dto) {
        inventoryService.unfreeze(dto);
        return new RowsDTO(1);
    }

    @Operation(summary = "回库", description = "售后退货时调用，增加可用库存")
    @PostMapping("/return")
    public RowsDTO returnStock(@Valid @RequestBody InventoryReturnDTO dto) {
        inventoryService.returnStock(dto);
        return new RowsDTO(1);
    }

    @Operation(summary = "入库", description = "采购/调拨入库，创建批次记录并增加库存")
    @PostMapping("/inbound")
    public RowsDTO inbound(@Valid @RequestBody InventoryInboundDTO dto) {
        inventoryService.inbound(dto);
        return new RowsDTO(1);
    }

    @Operation(summary = "查询商品库存", description = "根据商品 ID 查询实时库存")
    @GetMapping("/{productId}")
    public InventoryDTO getByProductId(@PathVariable Long productId) {
        return inventoryService.getByProductId(productId);
    }

    @Operation(summary = "批量查询库存", description = "根据商品 ID 列表批量查询库存")
    @PostMapping("/batch")
    public List<InventoryDTO> getByProductIds(@RequestBody List<Long> productIds) {
        return inventoryService.getByProductIds(productIds);
    }

    // ==================== 库存 CRUD（Feign 内部使用） ====================

    @Operation(summary = "分页查询库存列表")
    @PostMapping("/page")
    public Object searchByPage(@RequestBody InventoryConditionEntity condition) {
        return inventoryService.searchByPage(condition);
    }

    @Operation(summary = "初始化库存")
    @PostMapping("/init")
    public RowsDTO initInventory(@RequestBody InventoryEntity entity) {
        return new RowsDTO(inventoryService.initInventory(entity));
    }

    @Operation(summary = "修改库存")
    @PostMapping("/update")
    public RowsDTO updateInventory(@RequestBody InventoryEntity entity) {
        return new RowsDTO(inventoryService.updateInventory(entity));
    }

    @Operation(summary = "删除库存")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteByIds(@RequestBody List<Long> ids) {
        return new RowsDTO(inventoryService.deleteByIds(ids));
    }

    // ==================== 批次 / 流水查询 ====================

    @Operation(summary = "分页查询批次列表")
    @PostMapping("/batch/page")
    public Object batchPage(@RequestBody InventoryBatchConditionEntity condition) {
        return inventoryBatchService.searchByPage(condition);
    }

    @Operation(summary = "批次详情")
    @GetMapping("/batch/{id}")
    public InventoryBatchEntity batchDetail(@PathVariable Long id) {
        return inventoryBatchService.findById(id);
    }

    @Operation(summary = "分页查询库存流水")
    @PostMapping("/log/page")
    public Object logPage(@RequestBody InventoryLogConditionEntity condition) {
        return inventoryLogService.searchByPage(condition);
    }

    @Operation(summary = "流水详情")
    @GetMapping("/log/{id}")
    public InventoryLogEntity logDetail(@PathVariable Long id) {
        return inventoryLogService.findById(id);
    }
}

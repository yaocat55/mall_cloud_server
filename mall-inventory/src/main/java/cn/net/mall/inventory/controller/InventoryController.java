package cn.net.mall.inventory.controller;

import cn.net.mall.inventory.dto.*;
import cn.net.mall.inventory.service.InventoryService;
import cn.net.mall.inventory.dto.RowsDTO;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存管理接口.
 */
@RestController
@RequestMapping("/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

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

    @GetMapping("/{productId}")
    public ApiResult<InventoryDTO> getByProductId(@PathVariable Long productId) {
        return ApiResultUtil.success(inventoryService.getByProductId(productId));
    }

    @PostMapping("/batch")
    public ApiResult<List<InventoryDTO>> getByProductIds(@RequestBody List<Long> productIds) {
        return ApiResultUtil.success(inventoryService.getByProductIds(productIds));
    }
}

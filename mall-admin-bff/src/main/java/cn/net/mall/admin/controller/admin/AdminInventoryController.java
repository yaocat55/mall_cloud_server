package cn.net.mall.admin.controller.admin;

import cn.net.mall.inventory.client.InventoryFeignClient;
import cn.net.mall.inventory.dto.InventoryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台库存管理 BFF 控制器.
 *
 * <p>读聚合 + CRUD 透传</p>
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "库存管理", description = "库存查询、CRUD、批次流水查询")
public class AdminInventoryController {

    private final InventoryFeignClient inventoryFeignClient;

    @Operation(summary = "查询商品库存",
               description = "根据商品ID查询实时库存（available/frozen/saleCount）",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/{productId}")
    public InventoryDTO getByProductId(@PathVariable Long productId) {
        return inventoryFeignClient.getByProductId(productId);
    }

    @Operation(summary = "批量查询库存",
               description = "根据商品ID列表批量查询库存",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/batch")
    public List<InventoryDTO> getByProductIds(@RequestBody List<Long> productIds) {
        return inventoryFeignClient.getByProductIds(productIds);
    }

    // ==================== 库存 CRUD ====================

    @Operation(summary = "分页查询库存列表",
               description = "分页查询所有商品库存",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/page")
    public Object searchByPage(@RequestBody Object condition) {
        return inventoryFeignClient.searchByPage(condition);
    }

    @Operation(summary = "初始化库存", description = "手动创建库存记录",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/init")
    public Object initInventory(@RequestBody Object entity) {
        return inventoryFeignClient.initInventory(entity);
    }

    @Operation(summary = "修改库存", description = "手动调整库存数量",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/update")
    public Object updateInventory(@RequestBody Object entity) {
        return inventoryFeignClient.updateInventory(entity);
    }

    @Operation(summary = "删除库存", description = "批量删除库存记录",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/deleteByIds")
    public Object deleteByIds(@RequestBody List<Long> ids) {
        return inventoryFeignClient.deleteByIds(ids);
    }

    // ==================== 批次 / 流水查询 ====================

    @Operation(summary = "分页查询批次列表",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/batch/page")
    public Object batchPage(@RequestBody Object condition) {
        return inventoryFeignClient.batchPage(condition);
    }

    @Operation(summary = "批次详情",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/batch/{id}")
    public Object batchDetail(@PathVariable Long id) {
        return inventoryFeignClient.batchDetail(id);
    }

    @Operation(summary = "分页查询库存流水",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/log/page")
    public Object logPage(@RequestBody Object condition) {
        return inventoryFeignClient.logPage(condition);
    }

    @Operation(summary = "流水详情",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/log/{id}")
    public Object logDetail(@PathVariable Long id) {
        return inventoryFeignClient.logDetail(id);
    }
}

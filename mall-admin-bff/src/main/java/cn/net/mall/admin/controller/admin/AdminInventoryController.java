package cn.net.mall.admin.controller.admin;

import cn.net.mall.inventory.client.InventoryFeignClient;
import cn.net.mall.inventory.dto.InventoryDTO;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
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
 * <p>读聚合：查询库存</p>
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "库存管理", description = "库存查询")
public class AdminInventoryController {

    private final InventoryFeignClient inventoryFeignClient;

    @Operation(summary = "查询商品库存",
               description = "根据商品ID查询实时库存（available/frozen/saleCount）",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/{productId}")
    public ApiResult<InventoryDTO> getByProductId(@PathVariable Long productId) {
        return ApiResultUtil.success(inventoryFeignClient.getByProductId(productId));
    }

    @Operation(summary = "批量查询库存",
               description = "根据商品ID列表批量查询库存",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/batch")
    public ApiResult<List<InventoryDTO>> getByProductIds(@RequestBody List<Long> productIds) {
        return ApiResultUtil.success(inventoryFeignClient.getByProductIds(productIds));
    }
}

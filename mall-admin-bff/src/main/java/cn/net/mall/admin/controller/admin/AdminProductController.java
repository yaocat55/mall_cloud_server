package cn.net.mall.admin.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.inventory.client.InventoryFeignClient;
import cn.net.mall.inventory.dto.InventoryDTO;
import cn.net.mall.product.dto.ProductConditionDTO;
import cn.net.mall.product.dto.ProductDTO;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 管理后台商品管理 BFF 控制器.
 *
 * <p>读聚合：商品基本信息 + 库存信息</p>
 * <p>写透传：商品编辑走 product，库存操作走 inventory</p>
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/product")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品列表、分类、品牌、单位、属性、首页管理")
public class AdminProductController {

    private final ProductFeignClient productFeignClient;
    private final InventoryFeignClient inventoryFeignClient;

    @Operation(summary = "查询商品编辑数据",
               description = "聚合商品基本信息 + 当前库存数据",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/{id}/edit-data")
    public ApiResult<Map<String, Object>> getProductEditData(@PathVariable Long id) {
        ProductDTO product = null;
        try {
            List<ProductDTO> products = productFeignClient.findByIds(Collections.singletonList(id));
            if (products != null && !products.isEmpty()) {
                product = products.get(0);
            }
        } catch (Exception e) {
            log.warn("获取商品基本信息失败, id={}", id, e);
        }

        InventoryDTO inventory = null;
        try {
            inventory = inventoryFeignClient.getByProductId(id);
        } catch (Exception e) {
            log.warn("获取库存信息失败, productId={}", id, e);
        }

        return ApiResultUtil.success(Map.of(
                "product", product,
                "inventory", inventory
        ));
    }

    @Operation(summary = "分页查询商品列表",
               description = "多条件分页查询商品列表",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/page")
    public ApiResult<ResponsePageEntity<?>> searchByPage(@RequestBody ProductConditionDTO condition) {
        return ApiResultUtil.success(productFeignClient.searchByPage(condition));
    }

    @Operation(summary = "查询商品详情",
               description = "根据 ID 查询单个商品的完整信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/detail")
    public ApiResult<Object> findById(@RequestParam("id") Long id) {
        return ApiResultUtil.success(productFeignClient.findById(id));
    }
}

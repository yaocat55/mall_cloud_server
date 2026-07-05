package cn.net.mall.admin.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.admin.dto.ProductEditDataDTO;
import cn.net.mall.product.dto.ProductDTO;
import cn.net.mall.product.client.CategoryFeignClient;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.CategoryDTO;
import cn.net.mall.product.dto.ProductDTO;
import cn.net.mall.product.dto.ProductDetailDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 管理后台商品管理 BFF 控制器
 * 
* 聚合商品详情、分类、品牌、单位等数据，提供管理后台所需的商品管理接口
 * 
* **认证要求：**需携带 Bearer Token（登录后获取）
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/product")
@RequiredArgsConstructor
@Tag(name = "管理后台-商品管理", description = "商品详情、分类、品牌、单位等管理接口")
public class AdminProductController {

    private final ProductFeignClient productFeignClient;
    private final CategoryFeignClient categoryFeignClient;

    @Operation(summary = "获取商品编辑页数据",
               description = "聚合商品详情、分类树、品牌、单位，一次返回商品编辑页所需全部数据\n\n"
                           + "**注意事项：**\n"
                           + "- 需携带 Bearer Token（Authorization 请求头）\n"
                           + "- 品牌和单位列表当前返回空数组（TODO）",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/{id}/edit-data")
    public ProductEditDataDTO getProductEditData(@PathVariable Long id) {
        ProductEditDataDTO result = new ProductEditDataDTO();

        // 1. 商品基本信息
        try {
            List<ProductDTO> products = productFeignClient.findByIds(Collections.singletonList(id));
            result.setProduct(products != null && !products.isEmpty() ? products.get(0) : null);
        } catch (Exception e) {
            log.warn("获取商品基本信息失败, id={}", id, e);
            result.setProduct(null);
        }

        // 2. 商品详情
        try {
            result.setDetail(productFeignClient.findDetailById(id));
        } catch (Exception e) {
            log.warn("获取商品详情失败, id={}", id, e);
            result.setDetail(null);
        }

        // 3. 分类树
        try {
            result.setCategoryTree(buildCategoryTree());
        } catch (Exception e) {
            log.warn("获取分类树失败", e);
            result.setCategoryTree(Collections.emptyList());
        }

        // 4. 品牌列表（TODO: 需在 product-client 中新增 BrandFeignClient）
        result.setBrands(Collections.emptyList());

        // 5. 单位列表（TODO: 需在 product-client 中新增 UnitFeignClient）
        result.setUnits(Collections.emptyList());

        return result;
    }

    // ========== 管理端 CRUD ==========

    @Operation(summary = "分页查询商品列表",
               description = "多条件分页查询商品列表，支持按名称、分类、状态等条件筛选",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/page")
    public ResponsePageEntity<?> searchByPage(@RequestBody Map condition) {
        return productFeignClient.searchByPage(condition);
    }

    @Operation(summary = "查询商品详情",
               description = "根据 ID 查询单个商品的完整信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/detail")
    public Object findById(@RequestParam("id") Long id) {
        return productFeignClient.findById(id);
    }

    @Operation(summary = "新增商品",
               description = "新增一条商品记录，包含基本信息、价格、库存等字段",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/insert")
    public void insert(@RequestBody Object entity) {
        productFeignClient.insert(entity);
    }

    @Operation(summary = "修改商品",
               description = "修改已有商品的信息，支持部分字段更新",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/update")
    public void update(@RequestBody Object entity) {
        productFeignClient.update(entity);
    }

    @Operation(summary = "批量删除商品",
               description = "根据 ID 列表批量删除商品，物理删除",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/delete")
    public int delete(@RequestBody @NotNull List ids) {
        return productFeignClient.deleteByIds(ids);
    }

    /**
     * 通过逐级查询构建简易分类树（仅两级深度）
     */
    private List buildCategoryTree() {
        try {
            return categoryFeignClient.getCategoryByParentId(0L);
        } catch (Exception e) {
            log.warn("构建分类树失败", e);
            return Collections.emptyList();
        }
    }
}

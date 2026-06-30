package cn.net.mall.admin.controller.admin;

import cn.net.mall.product.client.CategoryFeignClient;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.CategoryDTO;
import cn.net.mall.product.dto.ProductDetailDTO;
import cn.net.mall.product.dto.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 管理后台商品管理 BFF 控制器
 * <p>
 * 聚合商品详情、分类、品牌、单位等数据，提供管理后台所需的商品管理接口
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
               description = "聚合商品详情 + 分类树 + 品牌列表 + 单位列表，支持管理后台商品编辑页面")
    @GetMapping("/{id}/edit-data")
    public Map<String, Object> getProductEditData(@PathVariable Long id) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 1. 商品基本信息
        try {
            List<ProductDTO> products = productFeignClient.findByIds(Collections.singletonList(id));
            result.put("product", products != null && !products.isEmpty() ? products.get(0) : null);
        } catch (Exception e) {
            log.warn("获取商品基本信息失败, id={}", id, e);
            result.put("product", null);
        }

        // 2. 商品详情
        try {
            ProductDetailDTO detail = productFeignClient.findDetailById(id);
            result.put("detail", detail);
        } catch (Exception e) {
            log.warn("获取商品详情失败, id={}", id, e);
            result.put("detail", null);
        }

        // 3. 分类树（通过逐级查询父分类构建；TODO: 后续可增加分类树专用 Feign 接口）
        try {
            List<CategoryDTO> categories = buildCategoryTree();
            result.put("categoryTree", categories);
        } catch (Exception e) {
            log.warn("获取分类树失败", e);
            result.put("categoryTree", Collections.emptyList());
        }

        // 4. 品牌列表（TODO: 需在 product-client 中新增 BrandFeignClient）
        result.put("brands", Collections.emptyList());

        // 5. 单位列表（TODO: 需在 product-client 中新增 UnitFeignClient）
        result.put("units", Collections.emptyList());

        return result;
    }

    /**
     * 通过逐级查询构建简易分类树（仅两级深度）
     * <p>
     * TODO: 后续可替换为分类树专用 Feign 接口
     */
    private List<CategoryDTO> buildCategoryTree() {
        try {
            // 查询顶级分类
            return categoryFeignClient.getCategoryByParentId(0L);
        } catch (Exception e) {
            log.warn("构建分类树失败", e);
            return Collections.emptyList();
        }
    }
}

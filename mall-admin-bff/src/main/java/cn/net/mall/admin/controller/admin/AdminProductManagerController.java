package cn.net.mall.admin.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.client.*;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/v1/product-mgr")
@RequiredArgsConstructor
@Tag(name = "管理后台-商品基础数据", description = "分类、品牌、单位等商品基础数据管理接口，需携带 Bearer Token")
public class AdminProductManagerController {
    private final CategoryFeignClient categoryFeignClient;
    private final BrandFeignClient brandFeignClient;
    private final UnitFeignClient unitFeignClient;

    @Operation(summary = "分页查询分类", description = "多条件分页查询商品分类列表，支持按名称、状态等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/category/page")
    public ApiResult<ResponsePageEntity<?>> searchCategoryPage(@RequestBody Map c) { return ApiResultUtil.success(categoryFeignClient.searchByPage(c)); }
    @Operation(summary = "新增分类", description = "新增一条商品分类记录，包含分类名称、上级分类、排序等", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/category/insert")
    public ApiResult<Integer> insertCategory(@RequestBody Object e) { return ApiResultUtil.success(categoryFeignClient.insert(e)); }
    @Operation(summary = "修改分类", description = "修改已有商品分类的信息", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/category/update")
    public ApiResult<Integer> updateCategory(@RequestBody Object e) { return ApiResultUtil.success(categoryFeignClient.update(e)); }
    @Operation(summary = "删除分类", description = "根据 ID 列表批量删除商品分类", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/category/delete")
    public ApiResult<Integer> deleteCategory(@RequestBody @NotNull List ids) { return ApiResultUtil.success(categoryFeignClient.deleteByIds(ids)); }

    @Operation(summary = "查询分类树", description = "按层级查询分类树结构，用于分类选择器", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/category/tree")
    public ApiResult<List<?>> getCategoryTree(@RequestBody Map c) { return ApiResultUtil.success(categoryFeignClient.searchByTree(c)); }

    @Operation(summary = "分页查询品牌", description = "多条件分页查询商品品牌列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/brand/page")
    public ApiResult<ResponsePageEntity<?>> searchBrandPage(@RequestBody Map c) { return ApiResultUtil.success(brandFeignClient.searchByPage(c)); }
    @Operation(summary = "新增品牌", description = "新增一条商品品牌记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/brand/insert")
    public ApiResult<Integer> insertBrand(@RequestBody Object e) { return ApiResultUtil.success(brandFeignClient.insert(e)); }
    @Operation(summary = "修改品牌", description = "修改已有商品品牌的信息", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/brand/update")
    public ApiResult<Integer> updateBrand(@RequestBody Object e) { return ApiResultUtil.success(brandFeignClient.update(e)); }
    @Operation(summary = "删除品牌", description = "根据 ID 列表批量删除商品品牌", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/brand/delete")
    public ApiResult<Integer> deleteBrand(@RequestBody @NotNull List ids) { return ApiResultUtil.success(brandFeignClient.deleteByIds(ids)); }

    @Operation(summary = "分页查询单位", description = "多条件分页查询商品单位列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/unit/page")
    public ApiResult<ResponsePageEntity<?>> searchUnitPage(@RequestBody Map c) { return ApiResultUtil.success(unitFeignClient.searchByPage(c)); }
    @Operation(summary = "新增单位", description = "新增一条商品单位记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/unit/insert")
    public ApiResult<Integer> insertUnit(@RequestBody Object e) { return ApiResultUtil.success(unitFeignClient.insert(e)); }
    @Operation(summary = "修改单位", description = "修改已有商品单位的信息", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/unit/update")
    public ApiResult<Integer> updateUnit(@RequestBody Object e) { return ApiResultUtil.success(unitFeignClient.update(e)); }
    @Operation(summary = "删除单位", description = "根据 ID 列表批量删除商品单位", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/unit/delete")
    public ApiResult<Integer> deleteUnit(@RequestBody @NotNull List ids) { return ApiResultUtil.success(unitFeignClient.deleteByIds(ids)); }
}

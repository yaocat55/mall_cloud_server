package cn.net.mall.admin.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.client.*;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import cn.net.mall.product.dto.CategoryConditionDTO;
import cn.net.mall.product.dto.BrandConditionDTO;
import cn.net.mall.product.dto.UnitConditionDTO;

@Slf4j
@RestController
@RequestMapping("/admin/v1/product-mgr")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品列表、分类、品牌、单位、属性、首页管理")
public class AdminProductManagerController {
    private final CategoryFeignClient categoryFeignClient;
    private final BrandFeignClient brandFeignClient;
    private final UnitFeignClient unitFeignClient;

    @Operation(summary = "分页查询分类", description = "多条件分页查询商品分类列表，支持按名称、状态等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/category/page")
    public ApiResult<ResponsePageEntity<?>> searchCategoryPage(@RequestBody CategoryConditionDTO c) { return ApiResultUtil.success(categoryFeignClient.searchByPage(c)); }

    @Operation(summary = "查询分类树", description = "按层级查询分类树结构，用于分类选择器", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/category/tree")
    public ApiResult<List<?>> getCategoryTree(@RequestBody Map c) { return ApiResultUtil.success(categoryFeignClient.searchByTree(c)); }

    @Operation(summary = "分页查询品牌", description = "多条件分页查询商品品牌列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/brand/page")
    public ApiResult<ResponsePageEntity<?>> searchBrandPage(@RequestBody BrandConditionDTO c) { return ApiResultUtil.success(brandFeignClient.searchByPage(c)); }

    @Operation(summary = "分页查询单位", description = "多条件分页查询商品单位列表", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/unit/page")
    public ApiResult<ResponsePageEntity<?>> searchUnitPage(@RequestBody UnitConditionDTO c) { return ApiResultUtil.success(unitFeignClient.searchByPage(c)); }
}

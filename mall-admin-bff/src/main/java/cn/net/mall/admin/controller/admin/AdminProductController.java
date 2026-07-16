package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.dto.IdsDTO;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.ProductConditionDTO;
import cn.net.mall.product.dto.ProductDTO;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
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
@Tag(name = "商品管理", description = "商品列表、分类、品牌、单位、属性、首页管理")
public class AdminProductController {

    private final ProductFeignClient productFeignClient;

    @Operation(summary = "查询商品编辑数据",
               description = "根据商品ID查询商品基本信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/{id}/edit-data")
    public ApiResult<ProductDTO> getProductEditData(@PathVariable Long id) {
        List<ProductDTO> products = productFeignClient.findByIds(Collections.singletonList(id));
        return ApiResultUtil.success(products != null && !products.isEmpty() ? products.get(0) : null);
    }

    // ========== 管理端 CRUD ==========

    @Operation(summary = "分页查询商品列表",
               description = "多条件分页查询商品列表，支持按名称、分类、状态等条件筛选",
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

    @Operation(summary = "新增商品",
               description = "新增一条商品记录，包含基本信息、价格、库存等字段",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/insert")
    public ApiResult<Void> insert(@RequestBody ProductDTO entity) {
        productFeignClient.insert(entity);
        return ApiResultUtil.success();
    }

    @Operation(summary = "修改商品",
               description = "修改已有商品的信息，支持部分字段更新",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/update")
    public ApiResult<Void> update(@RequestBody ProductDTO entity) {
        productFeignClient.update(entity);
        return ApiResultUtil.success();
    }

    @Operation(summary = "批量删除商品",
               description = "根据 ID 列表批量删除商品，物理删除",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/delete")
    public ApiResult<Integer> delete(@RequestBody IdsDTO dto) {
        return ApiResultUtil.success(productFeignClient.deleteByIds(dto.getIds()));
    }
}

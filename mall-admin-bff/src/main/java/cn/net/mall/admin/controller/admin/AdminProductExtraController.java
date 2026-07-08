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

/**
 * 管理后台商品扩展数据管理 BFF 控制器
 *
* 聚合属性、属性值、商品组、首页公告、首页商品、首页轮播图等扩展实体的 CRUD 操作，
 * 提供管理后台所需的统一管理接口。
 *
* **认证要求：**需携带 Bearer Token（登录后获取）
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/product-extra")
@RequiredArgsConstructor
@Tag(name = "管理后台-商品扩展数据", description = "属性、属性值、商品组、首页公告、首页商品、首页轮播图等扩展数据管理接口，需携带 Bearer Token")
public class AdminProductExtraController {

    private final AttributeFeignClient attributeFeignClient;
    private final AttributeValueFeignClient attributeValueFeignClient;
    private final ProductGroupFeignClient productGroupFeignClient;
    private final IndexNoticeFeignClient indexNoticeFeignClient;
    private final IndexProductFeignClient indexProductFeignClient;
    private final IndexCarouselImageFeignClient indexCarouselImageFeignClient;

    // ========== 属性 (Attribute) ==========

    @Operation(summary = "分页查询属性", description = "按条件分页查询属性列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attribute/page")
    public ApiResult<ResponsePageEntity<?>> searchAttributePage(@RequestBody Map c) {
        return ApiResultUtil.success(attributeFeignClient.searchByPage(c));
    }

    @Operation(summary = "新增属性", description = "新增一条属性记录，请求体包含属性名称、属性类型、排序等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attribute/insert")
    public ApiResult<Integer> insertAttribute(@RequestBody Object e) {
        return ApiResultUtil.success(attributeFeignClient.insert(e));
    }

    @Operation(summary = "修改属性", description = "修改指定属性记录，根据 ID 更新属性名称、属性类型、排序等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attribute/update")
    public ApiResult<Integer> updateAttribute(@RequestBody Object e) {
        return ApiResultUtil.success(attributeFeignClient.update(e));
    }

    @Operation(summary = "删除属性", description = "根据 ID 列表批量删除属性记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attribute/delete")
    public ApiResult<Integer> deleteAttribute(@RequestBody @NotNull List ids) {
        return ApiResultUtil.success(attributeFeignClient.deleteByIds(ids));
    }

    // ========== 属性值 (AttributeValue) ==========

    @Operation(summary = "分页查询属性值", description = "按条件分页查询属性值列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attributeValue/page")
    public ApiResult<ResponsePageEntity<?>> searchAttributeValuePage(@RequestBody Map c) {
        return ApiResultUtil.success(attributeValueFeignClient.searchByPage(c));
    }

    @Operation(summary = "新增属性值", description = "新增一条属性值记录，请求体包含属性ID、属性值名称、排序等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attributeValue/insert")
    public ApiResult<Integer> insertAttributeValue(@RequestBody Object e) {
        return ApiResultUtil.success(attributeValueFeignClient.insert(e));
    }

    @Operation(summary = "修改属性值", description = "修改指定属性值记录，根据 ID 更新属性值名称、排序等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attributeValue/update")
    public ApiResult<Integer> updateAttributeValue(@RequestBody Object e) {
        return ApiResultUtil.success(attributeValueFeignClient.update(e));
    }

    @Operation(summary = "删除属性值", description = "根据 ID 列表批量删除属性值记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attributeValue/delete")
    public ApiResult<Integer> deleteAttributeValue(@RequestBody @NotNull List ids) {
        return ApiResultUtil.success(attributeValueFeignClient.deleteByIds(ids));
    }

    // ========== 商品组 (ProductGroup) ==========

    @Operation(summary = "分页查询商品组", description = "按条件分页查询商品组列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productGroup/page")
    public ApiResult<ResponsePageEntity<?>> searchProductGroupPage(@RequestBody Map c) {
        return ApiResultUtil.success(productGroupFeignClient.searchByPage(c));
    }

    @Operation(summary = "新增商品组", description = "新增一条商品组记录，请求体包含商品组名称、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productGroup/insert")
    public ApiResult<Integer> insertProductGroup(@RequestBody Object e) {
        return ApiResultUtil.success(productGroupFeignClient.insert(e));
    }

    @Operation(summary = "修改商品组", description = "修改指定商品组记录，根据 ID 更新商品组名称、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productGroup/update")
    public ApiResult<Integer> updateProductGroup(@RequestBody Object e) {
        return ApiResultUtil.success(productGroupFeignClient.update(e));
    }

    @Operation(summary = "删除商品组", description = "根据 ID 列表批量删除商品组记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productGroup/delete")
    public ApiResult<Integer> deleteProductGroup(@RequestBody @NotNull List ids) {
        return ApiResultUtil.success(productGroupFeignClient.deleteByIds(ids));
    }

    // ========== 首页公告 (IndexNotice) ==========

    @Operation(summary = "分页查询首页公告", description = "按条件分页查询首页公告列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexNotice/page")
    public ApiResult<ResponsePageEntity<?>> searchIndexNoticePage(@RequestBody Map c) {
        return ApiResultUtil.success(indexNoticeFeignClient.searchByPage(c));
    }

    @Operation(summary = "新增首页公告", description = "新增一条首页公告记录，请求体包含公告标题、内容、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexNotice/insert")
    public ApiResult<Integer> insertIndexNotice(@RequestBody Object e) {
        return ApiResultUtil.success(indexNoticeFeignClient.insert(e));
    }

    @Operation(summary = "修改首页公告", description = "修改指定首页公告记录，根据 ID 更新公告标题、内容、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexNotice/update")
    public ApiResult<Integer> updateIndexNotice(@RequestBody Object e) {
        return ApiResultUtil.success(indexNoticeFeignClient.update(e));
    }

    @Operation(summary = "删除首页公告", description = "根据 ID 列表批量删除首页公告记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexNotice/delete")
    public ApiResult<Integer> deleteIndexNotice(@RequestBody @NotNull List ids) {
        return ApiResultUtil.success(indexNoticeFeignClient.deleteByIds(ids));
    }

    @Operation(summary = "查询首页公告详情", description = "根据ID查询首页公告详细内容", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/indexNotice/detail")
    public ApiResult<Object> getIndexNoticeDetail(@RequestParam("id") Long id) {
        return ApiResultUtil.success(indexNoticeFeignClient.findById(id));
    }

    // ========== 首页商品 (IndexProduct) ==========

    @Operation(summary = "分页查询首页商品", description = "按条件分页查询首页商品列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexProduct/page")
    public ApiResult<ResponsePageEntity<?>> searchIndexProductPage(@RequestBody Map c) {
        return ApiResultUtil.success(indexProductFeignClient.searchByPage(c));
    }

    @Operation(summary = "新增首页商品", description = "新增一条首页商品记录，请求体包含商品ID、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexProduct/insert")
    public ApiResult<Integer> insertIndexProduct(@RequestBody Object e) {
        return ApiResultUtil.success(indexProductFeignClient.insert(e));
    }

    @Operation(summary = "修改首页商品", description = "修改指定首页商品记录，根据 ID 更新商品ID、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexProduct/update")
    public ApiResult<Integer> updateIndexProduct(@RequestBody Object e) {
        return ApiResultUtil.success(indexProductFeignClient.update(e));
    }

    @Operation(summary = "删除首页商品", description = "根据 ID 列表批量删除首页商品记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexProduct/delete")
    public ApiResult<Integer> deleteIndexProduct(@RequestBody @NotNull List ids) {
        return ApiResultUtil.success(indexProductFeignClient.deleteByIds(ids));
    }

    // ========== 首页轮播图 (IndexCarouselImage) ==========

    @Operation(summary = "分页查询首页轮播图", description = "按条件分页查询首页轮播图列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexCarouselImage/page")
    public ApiResult<ResponsePageEntity<?>> searchIndexCarouselImagePage(@RequestBody Map c) {
        return ApiResultUtil.success(indexCarouselImageFeignClient.searchByPage(c));
    }

    @Operation(summary = "新增首页轮播图", description = "新增一条首页轮播图记录，请求体包含图片地址、标题、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexCarouselImage/insert")
    public ApiResult<Integer> insertIndexCarouselImage(@RequestBody Object e) {
        return ApiResultUtil.success(indexCarouselImageFeignClient.insert(e));
    }

    @Operation(summary = "修改首页轮播图", description = "修改指定首页轮播图记录，根据 ID 更新图片地址、标题、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexCarouselImage/update")
    public ApiResult<Integer> updateIndexCarouselImage(@RequestBody Object e) {
        return ApiResultUtil.success(indexCarouselImageFeignClient.update(e));
    }

    @Operation(summary = "删除首页轮播图", description = "根据 ID 列表批量删除首页轮播图记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexCarouselImage/delete")
    public ApiResult<Integer> deleteIndexCarouselImage(@RequestBody @NotNull List ids) {
        return ApiResultUtil.success(indexCarouselImageFeignClient.deleteByIds(ids));
    }
}

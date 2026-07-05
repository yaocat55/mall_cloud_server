package cn.net.mall.admin.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.client.*;
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
    public ResponsePageEntity<?> searchAttributePage(@RequestBody Map c) {
        return attributeFeignClient.searchByPage(c);
    }

    @Operation(summary = "新增属性", description = "新增一条属性记录，请求体包含属性名称、属性类型、排序等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attribute/insert")
    public int insertAttribute(@RequestBody Object e) {
        return attributeFeignClient.insert(e);
    }

    @Operation(summary = "修改属性", description = "修改指定属性记录，根据 ID 更新属性名称、属性类型、排序等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attribute/update")
    public int updateAttribute(@RequestBody Object e) {
        return attributeFeignClient.update(e);
    }

    @Operation(summary = "删除属性", description = "根据 ID 列表批量删除属性记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attribute/delete")
    public int deleteAttribute(@RequestBody @NotNull List ids) {
        return attributeFeignClient.deleteByIds(ids);
    }

    // ========== 属性值 (AttributeValue) ==========

    @Operation(summary = "分页查询属性值", description = "按条件分页查询属性值列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attributeValue/page")
    public ResponsePageEntity<?> searchAttributeValuePage(@RequestBody Map c) {
        return attributeValueFeignClient.searchByPage(c);
    }

    @Operation(summary = "新增属性值", description = "新增一条属性值记录，请求体包含属性ID、属性值名称、排序等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attributeValue/insert")
    public int insertAttributeValue(@RequestBody Object e) {
        return attributeValueFeignClient.insert(e);
    }

    @Operation(summary = "修改属性值", description = "修改指定属性值记录，根据 ID 更新属性值名称、排序等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attributeValue/update")
    public int updateAttributeValue(@RequestBody Object e) {
        return attributeValueFeignClient.update(e);
    }

    @Operation(summary = "删除属性值", description = "根据 ID 列表批量删除属性值记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attributeValue/delete")
    public int deleteAttributeValue(@RequestBody @NotNull List ids) {
        return attributeValueFeignClient.deleteByIds(ids);
    }

    // ========== 商品组 (ProductGroup) ==========

    @Operation(summary = "分页查询商品组", description = "按条件分页查询商品组列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productGroup/page")
    public ResponsePageEntity<?> searchProductGroupPage(@RequestBody Map c) {
        return productGroupFeignClient.searchByPage(c);
    }

    @Operation(summary = "新增商品组", description = "新增一条商品组记录，请求体包含商品组名称、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productGroup/insert")
    public int insertProductGroup(@RequestBody Object e) {
        return productGroupFeignClient.insert(e);
    }

    @Operation(summary = "修改商品组", description = "修改指定商品组记录，根据 ID 更新商品组名称、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productGroup/update")
    public int updateProductGroup(@RequestBody Object e) {
        return productGroupFeignClient.update(e);
    }

    @Operation(summary = "删除商品组", description = "根据 ID 列表批量删除商品组记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productGroup/delete")
    public int deleteProductGroup(@RequestBody @NotNull List ids) {
        return productGroupFeignClient.deleteByIds(ids);
    }

    // ========== 首页公告 (IndexNotice) ==========

    @Operation(summary = "分页查询首页公告", description = "按条件分页查询首页公告列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexNotice/page")
    public ResponsePageEntity<?> searchIndexNoticePage(@RequestBody Map c) {
        return indexNoticeFeignClient.searchByPage(c);
    }

    @Operation(summary = "新增首页公告", description = "新增一条首页公告记录，请求体包含公告标题、内容、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexNotice/insert")
    public int insertIndexNotice(@RequestBody Object e) {
        return indexNoticeFeignClient.insert(e);
    }

    @Operation(summary = "修改首页公告", description = "修改指定首页公告记录，根据 ID 更新公告标题、内容、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexNotice/update")
    public int updateIndexNotice(@RequestBody Object e) {
        return indexNoticeFeignClient.update(e);
    }

    @Operation(summary = "删除首页公告", description = "根据 ID 列表批量删除首页公告记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexNotice/delete")
    public int deleteIndexNotice(@RequestBody @NotNull List ids) {
        return indexNoticeFeignClient.deleteByIds(ids);
    }

    @Operation(summary = "查询首页公告详情", description = "根据ID查询首页公告详细内容", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/indexNotice/detail")
    public Object getIndexNoticeDetail(@RequestParam("id") Long id) {
        return indexNoticeFeignClient.findById(id);
    }

    // ========== 首页商品 (IndexProduct) ==========

    @Operation(summary = "分页查询首页商品", description = "按条件分页查询首页商品列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexProduct/page")
    public ResponsePageEntity<?> searchIndexProductPage(@RequestBody Map c) {
        return indexProductFeignClient.searchByPage(c);
    }

    @Operation(summary = "新增首页商品", description = "新增一条首页商品记录，请求体包含商品ID、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexProduct/insert")
    public int insertIndexProduct(@RequestBody Object e) {
        return indexProductFeignClient.insert(e);
    }

    @Operation(summary = "修改首页商品", description = "修改指定首页商品记录，根据 ID 更新商品ID、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexProduct/update")
    public int updateIndexProduct(@RequestBody Object e) {
        return indexProductFeignClient.update(e);
    }

    @Operation(summary = "删除首页商品", description = "根据 ID 列表批量删除首页商品记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexProduct/delete")
    public int deleteIndexProduct(@RequestBody @NotNull List ids) {
        return indexProductFeignClient.deleteByIds(ids);
    }

    // ========== 首页轮播图 (IndexCarouselImage) ==========

    @Operation(summary = "分页查询首页轮播图", description = "按条件分页查询首页轮播图列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexCarouselImage/page")
    public ResponsePageEntity<?> searchIndexCarouselImagePage(@RequestBody Map c) {
        return indexCarouselImageFeignClient.searchByPage(c);
    }

    @Operation(summary = "新增首页轮播图", description = "新增一条首页轮播图记录，请求体包含图片地址、标题、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexCarouselImage/insert")
    public int insertIndexCarouselImage(@RequestBody Object e) {
        return indexCarouselImageFeignClient.insert(e);
    }

    @Operation(summary = "修改首页轮播图", description = "修改指定首页轮播图记录，根据 ID 更新图片地址、标题、排序、状态等字段", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexCarouselImage/update")
    public int updateIndexCarouselImage(@RequestBody Object e) {
        return indexCarouselImageFeignClient.update(e);
    }

    @Operation(summary = "删除首页轮播图", description = "根据 ID 列表批量删除首页轮播图记录", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexCarouselImage/delete")
    public int deleteIndexCarouselImage(@RequestBody @NotNull List ids) {
        return indexCarouselImageFeignClient.deleteByIds(ids);
    }
}

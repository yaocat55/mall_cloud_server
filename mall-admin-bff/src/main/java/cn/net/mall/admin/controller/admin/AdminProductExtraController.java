package cn.net.mall.admin.controller.admin;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.client.*;
import cn.net.mall.product.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台商品扩展数据管理 BFF 控制器
 *
 * 聚合属性、属性值、商品组、首页公告、首页商品、首页轮播图等扩展实体的查询操作，
 * 提供管理后台所需的统一管理接口。
 *
 * **认证要求：**需携带 Bearer Token（登录后获取）
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/product-extra")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品列表、分类、品牌、单位、属性、首页管理")
public class AdminProductExtraController {

    private final AttributeFeignClient attributeFeignClient;
    private final AttributeValueFeignClient attributeValueFeignClient;
    private final ProductGroupFeignClient productGroupFeignClient;
    private final IndexNoticeFeignClient indexNoticeFeignClient;
    private final IndexProductFeignClient indexProductFeignClient;
    private final IndexCarouselImageFeignClient indexCarouselImageFeignClient;
    private final ProductPhotoFeignClient productPhotoFeignClient;

    // ========== 属性 (Attribute) ==========

    @Operation(summary = "分页查询属性", description = "按条件分页查询属性列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attribute/page")
    public ResponsePageEntity<?> searchAttributePage(@RequestBody AttributeConditionDTO c) {
        return attributeFeignClient.searchByPage(c);
    }

    // ========== 属性值 (AttributeValue) ==========

    @Operation(summary = "分页查询属性值", description = "按条件分页查询属性值列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/attributeValue/page")
    public ResponsePageEntity<?> searchAttributeValuePage(@RequestBody AttributeValueConditionDTO c) {
        return attributeValueFeignClient.searchByPage(c);
    }

    // ========== 商品组 (ProductGroup) ==========

    @Operation(summary = "分页查询商品组", description = "按条件分页查询商品组列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productGroup/page")
    public ResponsePageEntity<?> searchProductGroupPage(@RequestBody ProductGroupConditionDTO c) {
        return productGroupFeignClient.searchByPage(c);
    }

    // ========== 首页公告 (IndexNotice) ==========

    @Operation(summary = "分页查询首页公告", description = "按条件分页查询首页公告列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexNotice/page")
    public ResponsePageEntity<?> searchIndexNoticePage(@RequestBody IndexNoticeConditionDTO c) {
        return indexNoticeFeignClient.searchByPage(c);
    }

    @Operation(summary = "查询首页公告详情", description = "根据ID查询首页公告详细内容", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/indexNotice/detail")
    public Object getIndexNoticeDetail(@RequestParam("id") Long id) {
        return indexNoticeFeignClient.findById(id);
    }

    // ========== 首页商品 (IndexProduct) ==========

    @Operation(summary = "分页查询首页商品", description = "按条件分页查询首页商品列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexProduct/page")
    public ResponsePageEntity<?> searchIndexProductPage(@RequestBody IndexProductConditionDTO c) {
        return indexProductFeignClient.searchByPage(c);
    }

    // ========== 首页轮播图 (IndexCarouselImage) ==========

    @Operation(summary = "分页查询首页轮播图", description = "按条件分页查询首页轮播图列表，支持分页参数", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/indexCarouselImage/page")
    public ResponsePageEntity<?> searchIndexCarouselImagePage(@RequestBody IndexCarouselImageConditionDTO c) {
        return indexCarouselImageFeignClient.searchByPage(c);
    }

    // ========== 商品图片 (ProductPhoto) ==========

    @Operation(summary = "分页查询商品图片", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productPhoto/page")
    public ResponsePageEntity<?> searchPhotoPage(@RequestBody ProductPhotoConditionDTO c) {
        return productPhotoFeignClient.searchByPage(c);
    }
}

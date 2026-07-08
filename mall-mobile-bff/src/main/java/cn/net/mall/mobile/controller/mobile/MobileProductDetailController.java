package cn.net.mall.mobile.controller.mobile;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.client.CategoryFeignClient;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.*;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 移动端商品 BFF 控制器
 * 聚合商品信息 + 评论，提供搜索、分类、收藏等接口
 */
@Slf4j
@RestController
@RequestMapping("/mobile/v1/product")
@RequiredArgsConstructor
@Tag(name = "移动端-商品", description = "商品详情聚合、搜索、评论、分类、收藏")
public class MobileProductDetailController {

    private final ProductFeignClient productFeignClient;
    private final CategoryFeignClient categoryFeignClient;

    @Operation(summary = "获取商品完整详情", description = "一次接口获取商品信息、评论列表、收藏状态，前端无需多次调用")
    @GetMapping("/{productId}/detail")
    public ApiResult<Map> getDetail(@PathVariable("productId") Long productId) {
        Map result = new LinkedHashMap<>();

        // 商品基本信息
        try {
            result.put("product", productFeignClient.getDetail(productId));
        } catch (Exception e) {
            log.warn("获取商品详情失败 productId={}", productId, e);
            result.put("product", null);
        }

        // 商品评论（只返回第一页）
        try {
            ProductCommentConditionDTO commentCond = new ProductCommentConditionDTO();
            commentCond.setProductId(productId);
            commentCond.setPageNo(1);
            commentCond.setPageSize(10);
            result.put("comments", productFeignClient.searchProductComment(commentCond));
        } catch (Exception e) {
            log.warn("获取商品评论失败 productId={}", productId, e);
            result.put("comments", ResponsePageEntity.buildEmpty(new cn.net.mall.entity.RequestPageEntity()));
        }

        return ApiResultUtil.success(result);
    }

    @Operation(summary = "搜索商品", description = "从ES搜索商品列表")
    @PostMapping("/search")
    public ApiResult<ResponsePageEntity> search(@RequestBody ProductSearchConditionDTO condition) {
        return ApiResultUtil.success(productFeignClient.searchFromES(condition));
    }

    @Operation(summary = "查询商品评论", description = "分页查询商品评论（加载更多时调用）")
    @PostMapping("/comment/page")
    public ApiResult<ResponsePageEntity> searchComment(@RequestBody ProductCommentConditionDTO condition) {
        return ApiResultUtil.success(productFeignClient.searchProductComment(condition));
    }

    @Operation(summary = "收藏/取消收藏商品")
    @PostMapping("/favorites/toggle")
    public ApiResult<Boolean> toggleFavorites(@RequestBody ProductFavoritesDTO dto) {
        return ApiResultUtil.success(productFeignClient.addOrCancelFavorites(dto));
    }

    @Operation(summary = "获取商品详情（简单）", description = "仅获取商品基本信息，不含评论和收藏状态")
    @GetMapping("/detail")
    public ApiResult<ProductDetailInfoDTO> getSimpleDetail(@RequestParam("productId") Long productId) {
        return ApiResultUtil.success(productFeignClient.getDetail(productId));
    }

    @Operation(summary = "获取分类列表", description = "根据父分类ID查询子分类")
    @GetMapping("/category")
    public ApiResult<List> getCategory(@RequestParam("parentId") Long parentId) {
        return ApiResultUtil.success(categoryFeignClient.getCategoryByParentId(parentId));
    }

    @Operation(summary = "保存商品评论", description = "批量保存商品评价")
    @PostMapping("/comment/save")
    public ApiResult<Boolean> saveComment(@RequestBody OrderTradeProductCommentDTO dto) {
        return ApiResultUtil.success(productFeignClient.saveProductComment(dto));
    }
}

package cn.net.mall.product.controller.mobile;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.annotation.ValidSensitiveWord;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.*;
import cn.net.mall.product.entity.ProductCommentConditionEntity;
import cn.net.mall.product.entity.ProductFavoritesEntity;
import cn.net.mall.product.entity.web.ProductCommentWebEntity;
import cn.net.mall.product.service.ProductCommentService;
import cn.net.mall.product.service.ProductFavoritesService;
import cn.net.mall.product.service.ProductService;
import cn.net.mall.product.service.shopping.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.util.List;
import cn.net.mall.product.cache.MobileCacheService;
/**
 * 移动端商品相关接口
 *
 * @date 2025/6/16 14:50
 */
@Tag(name = "移动端-商品详情", description = "移动端：商品详情、评价、收藏")
@AllArgsConstructor
@RestController
@RequestMapping("/v1/mobile/product")
public class MobileProductController {

    private final ProductService productService;
    private final ProductFavoritesService productFavoritesService;
    private final ProductCommentService productCommentService;
    private final ShoppingCartService shoppingCartService;
    private final MobileCacheService mobileCacheService;


    /**
     * 根据条件搜索商品列表
     *
     * @param productSearchConditionDTO 条件
     * @return 商品列表
     */
    @Operation(summary = "根据条件搜索商品列表", description = "根据条件搜索商品列表")
    @PostMapping("/searchProduct")
    public ResponsePageEntity<ProductSearchResultDTO> searchProduct(@RequestBody ProductSearchConditionDTO productSearchConditionDTO) {
        if (isDefaultCondition(productSearchConditionDTO)
                && Integer.valueOf(1).equals(productSearchConditionDTO.getPageNo())
                && Integer.valueOf(10).equals(productSearchConditionDTO.getPageSize())) {

            ResponsePageEntity<ProductSearchResultDTO> result = mobileCacheService.getProductSearchResult();
            if (result != null) {
                return result;
            }
        }
        return productService.searchFromES(productSearchConditionDTO);
    }

    private boolean isDefaultCondition(ProductSearchConditionDTO condition) {
        return condition.getCategoryId() == null &&
                condition.getBrandId() == null &&
                condition.getUnitId() == null &&
                condition.getType() == null &&
                !StringUtils.hasText(condition.getKeyword());
    }

    /**
     * 获取商品详情
     *
     * @param productId 商品ID
     * @return 商品详情
     */
    @Operation(summary = "获取商品详情", description = "获取商品详情")
    @GetMapping("/getDetail")
    public ProductDetailInfoDTO getDetail(@Parameter(description = "商品ID")
    @RequestParam("productId") Long productId) {
        return BeanUtil.toBean(productService.getDetail(productId), ProductDetailInfoDTO.class);
    }

    /**
     * 获取商品详情（内部 Feign 调用）
     *
     * @param productId 商品ID
     * @return 商品详情
     */
    @Operation(summary = "获取商品详情（内部）", description = "由微服务内部 Feign 调用，获取商品详情")
    @GetMapping("/v1/internal/product/getDetail")
    public ProductDetailInfoDTO getDetailInternal(@Parameter(description = "商品ID")
    @RequestParam("productId") Long productId) {
        return BeanUtil.toBean(productService.getDetail(productId), ProductDetailInfoDTO.class);
    }


    /**
     * 收藏或取消收藏商品
     *
     * @param productFavoritesDTO 商品收藏实体
     */
    @Operation(summary = "收藏或取消收藏商品", description = "收藏或取消收藏商品")
    @PostMapping("/addOrCancelFavorites")
    public Boolean addOrCancelFavorites(@RequestBody @Valid ProductFavoritesDTO productFavoritesDTO) {
        return productFavoritesService.addOrCancelFavorites(BeanUtil.toBean(productFavoritesDTO, ProductFavoritesEntity.class));
    }

    /**
     * 根据条件搜索商品评论列表
     *
     * @param productCommentConditionDTO 条件
     * @return 商品评论列表
     */
    @Operation(summary = "根据条件搜索商品评论列表", description = "根据条件搜索商品评论列表")
    @PostMapping("/searchProductComment")
    public ResponsePageEntity<ProductCommentDTO> searchProductComment(@RequestBody ProductCommentConditionDTO productCommentConditionDTO) {
        ResponsePageEntity<ProductCommentWebEntity> responsePageEntity = productCommentService.searchProductComment(BeanUtil.toBean(productCommentConditionDTO, ProductCommentConditionEntity.class));
        List<ProductCommentDTO> productCommentDTOS = BeanUtil.copyToList(responsePageEntity.getData(), ProductCommentDTO.class);
        return ResponsePageEntity.build(productCommentConditionDTO, responsePageEntity.getTotalCount(), productCommentDTOS);
    }

    @Operation(summary = "批量保存的商品用户评价", description = "批量保存的商品用户评价")
    @PostMapping("/saveProductComment")
    @ValidSensitiveWord
    public Boolean saveProductComment(@RequestBody @Valid OrderTradeProductCommentDTO orderTradeProductCommentDTO) {
        productCommentService.saveProductComment(orderTradeProductCommentDTO);
        return Boolean.TRUE;
    }

    @Operation(summary = "批量新增商品评论", description = "批量新增商品评论")
    @PostMapping("/addProductComments")
    @ValidSensitiveWord
    public Boolean addProductComments(@RequestBody List<ProductCommentDTO> list) {
        List<cn.net.mall.product.entity.ProductCommentEntity> entities = BeanUtil.copyToList(list, cn.net.mall.product.entity.ProductCommentEntity.class);
        return productCommentService.batchInsert(entities) > 0;
    }

    /**
     * 根据条件搜索购物车商品列表
     *
     * @param shoppingCartConditionEntity 条件
     * @return 购物车商品列表
     */
    @Operation(summary = "根据条件搜索购物车商品列表", description = "根据条件搜索购物车商品列表")
    @PostMapping("/getShoppingCartProduct")
    public ShoppingCartBuyDTO getShoppingCartProduct(@RequestBody ShoppingCartConditionDTO shoppingCartConditionEntity) {
        return shoppingCartService.getShoppingCartProduct(shoppingCartConditionEntity);
    }

    /**
     * 添加购物车
     *
     * @param shoppingCartEntity 购物车信息
     */
    @Operation(summary = "添加购物车", description = "添加购物车")
    @PostMapping("/addShoppingCart")
    public Boolean addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartEntity) {
        return shoppingCartService.addShoppingCart(shoppingCartEntity);
    }

    /**
     * 修改购物车
     *
     * @param shoppingCartEntity 购物车信息
     */
    @Operation(summary = "修改购物车", description = "修改购物车")
    @PostMapping("/updateShoppingCart")
    public void updateShoppingCart(@RequestBody ShoppingCartDTO shoppingCartEntity) {
        shoppingCartService.updateShoppingCart(shoppingCartEntity);
    }

    /**
     * 批量删除购物车
     *
     * @param ids 购物车ID集合
     * @return 影响行数
     */
    @Operation(summary = "批量删除购物车", description = "批量删除购物车")
    @PostMapping("/deleteShoppingCart")
    public int deleteShoppingCart(@RequestBody @NotNull List<Long> ids) {
        return shoppingCartService.deleteByIds(ids);
    }
}

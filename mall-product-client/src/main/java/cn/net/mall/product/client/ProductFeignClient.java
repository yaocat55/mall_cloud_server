package cn.net.mall.product.client;

import cn.net.mall.annotation.NoLogin;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;


/**
 * 商品/购物车 Feign 客户端
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>order-service（订单服务）— 下单、购物车相关操作</li>
 *   <li>recommend-service（推荐服务）— 商品数据查询</li>
 * </ul>
 */
@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "productFeignClient")
public interface ProductFeignClient {

    /**
     * 通过id集合批量查询商品信息
     *
     * @param ids 商品ID
     * @return 商品信息
     */
    @Operation(summary = "批量查询商品基本信息", description = "由 order-service、recommend-service 通过 Feign 调用，根据ID集合批量获取商品基本信息")
    @GetMapping("/v1/product/findByIds")
    List<ProductDTO> findByIds(List<Long> ids);

    /**
     * 通过id查询商品详情信息(仅仅包含详情，不包含评价、价格、规格、收藏等)
     *
     * @param id 系统ID
     * @return 商品详情信息
     */
    @Operation(summary = "查询商品详情信息", description = "由 order-service 通过 Feign 调用，根据ID查询商品详情（不含评价、价格、规格）")
    @GetMapping("/v1/product/findDetailById")
    ProductDetailDTO findDetailById(Long id);

    /**
     * 通过商品id集合批量查询商品图片信息
     *
     * @param productIds 商品ID
     * @return 商品图片信息
     */
    @Operation(summary = "通过商品id集合批量查询商品图片信息", description = "通过商品id集合批量查询商品图片信息")
    @PostMapping("/v1/productPhoto/findByProductIds")
    List<ProductPhotoDTO> findByProductIds(@RequestBody List<Long> productIds);

    /**
     * 根据条件从ES中分页搜索商品列表
     *
     * @param productSearchConditionDTO 商品信息
     * @return 商品集合
     */
    @Operation(summary = "根据条件从ES中分页搜索商品列表", description = "根据条件从ES中分页搜索商品列表")
    @PostMapping("/v1/product/searchFromES")
    ResponsePageEntity<ProductSearchResultDTO> searchFromES(@RequestBody ProductSearchConditionDTO productSearchConditionDTO);


    /**
     * 获取商品详情
     *
     * @param productId 商品ID
     * @return 商品详情
     */
    @Operation(summary = "获取商品详情", description = "获取商品详情")
    @GetMapping("/v1/mobile/product/getDetail")
    ProductDetailInfoDTO getDetail(@RequestParam("productId") Long productId);

    /**
     * 收藏或取消收藏商品
     *
     * @param productFavoritesDTO 商品收藏实体
     */
    @Operation(summary = "收藏或取消收藏商品", description = "收藏或取消收藏商品")
    @PostMapping("/v1/mobile/product/addOrCancelFavorites")
    Boolean addOrCancelFavorites(@RequestBody @Valid ProductFavoritesDTO productFavoritesDTO);

    /**
     * 根据条件搜索商品评论列表
     *
     * @param productCommentConditionDTO 条件
     * @return 商品评论列表
     */
    @Operation(summary = "根据条件搜索商品评论列表", description = "根据条件搜索商品评论列表")
    @PostMapping("/v1/mobile/product/searchProductComment")
    ResponsePageEntity<ProductCommentDTO> searchProductComment(@RequestBody ProductCommentConditionDTO productCommentConditionDTO);

    /**
     * 批量保存的商品用户评价
     *
     * @param req 订单商品评价实体
     * @return 是否成功
     */
    @Operation(summary = "批量保存的商品用户评价", description = "批量保存的商品用户评价")
    @PostMapping("/v1/mobile/product/saveProductComment")
    Boolean saveProductComment(@RequestBody OrderTradeProductCommentDTO req);

    /**
     * 批量新增商品评论
     *
     * @param list 商品评论列表
     * @return 是否成功
     */
    @Operation(summary = "批量新增商品评论", description = "批量新增商品评论")
    @PostMapping("/v1/mobile/product/addProductComments")
    Boolean addProductComments(@RequestBody List<ProductCommentDTO> list);

    /**
     * 根据父分类ID查询分类列表
     *
     * @param parentId 父分类ID
     * @return 分类列表
     */
    @Operation(summary = "根据父分类ID查询分类列表", description = "根据父分类ID查询分类列表")
    @GetMapping("/v1/mobile/category/getCategoryByParentId")
    List<CategoryDTO> getCategoryByParentId(@RequestParam("parentId") Long parentId);

    /**
     * 通过id集合批量查询购物车信息
     *
     * @param ids 购物车ID
     * @return 购物车信息
     */
    @Operation(summary = "批量查询购物车信息", description = "由 order-service 通过 Feign 调用，根据ID集合批量获取购物车商品信息")
    @PostMapping("/v1/shoppingCart/findByIds")
    List<ShoppingCartDTO> findShoppingCartByIds(@RequestBody List<Long> ids);

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO 购物车信息
     */
    @Operation(summary = "添加购物车", description = "添加购物车")
    @PostMapping("/v1/shoppingCart/addShoppingCart")
    Boolean addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO);

    /**
     * 修改购物车
     *
     * @param shoppingCartDTO 购物车信息
     */
    @Operation(summary = "修改购物车", description = "修改购物车")
    @PostMapping("/v1/shoppingCart/updateShoppingCart")
    void updateShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO);

    /**
     * 根据条件搜索购物车商品列表
     *
     * @param shoppingCartConditionDTO 条件
     * @return 购物车商品列表
     */
    @Operation(summary = "查询购物车商品列表", description = "由 order-service 通过 Feign 调用，根据条件获取购物车商品信息用于下单确认")
    @PostMapping("/v1/shoppingCart/getShoppingCartProduct")
    ShoppingCartBuyDTO getShoppingCartProduct(@RequestBody ShoppingCartConditionDTO shoppingCartConditionDTO);

    /**
     * 批量删除购物车
     *
     * @param ids 购物车ID集合
     * @return 影响行数
     */
    @Operation(summary = "批量删除购物车", description = "批量删除购物车")
    @PostMapping("/v1/shoppingCart/deleteShoppingCart")
    int deleteShoppingCart(@RequestBody List<Long> ids);

    /**
     * 批量扣减商品库存
     *
     * @param items 购物车项（含productId与quantity）
     */
    @Operation(summary = "批量扣减商品库存", description = "由 order-service 提交订单时通过 Feign 调用，批量扣减商品库存")
    @PostMapping("/v1/product/reduceStockBatch")
    void reduceStockBatch(@RequestBody List<ShoppingCartDTO> items);
}

package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.client.DeliveryAddressFeignClient;
import cn.net.mall.admin.dto.DeliveryAddressDTO;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.client.CartFeignClient;
import cn.net.mall.product.client.CommentFeignClient;
import cn.net.mall.product.client.FavoritesFeignClient;
import cn.net.mall.product.client.ProductViewRecordFeignClient;
import cn.net.mall.product.dto.ProductViewRecordConditionDTO;
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
 * 管理后台购物相关数据管理 BFF 控制器
 * 
* 聚合收货地址、商品评论、商品收藏、商品浏览记录、购物车等购物相关实体的 CRUD 操作，
 * 提供管理后台所需的统一管理接口。
 * 
* **认证要求：**需携带 Bearer Token（登录后获取）
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/shopping")
@RequiredArgsConstructor
@Tag(name = "管理后台-购物相关数据", description = "收货地址、商品评论、商品收藏、商品浏览记录、购物车等数据管理接口，需携带 Bearer Token")
public class AdminShoppingController {

    private final DeliveryAddressFeignClient deliveryAddressFeignClient;
    private final CommentFeignClient commentFeignClient;
    private final FavoritesFeignClient favoritesFeignClient;
    private final ProductViewRecordFeignClient productViewRecordFeignClient;
    private final CartFeignClient cartFeignClient;

    // ========== 收货地址 (deliveryAddress) ==========

    @Operation(summary = "分页查询收货地址",
               description = "管理端分页查询收货地址列表，支持按用户ID、收货人姓名、手机号等条件筛选",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/deliveryAddress/page")
    public List searchDeliveryAddressPage(@RequestBody Map c) {
        return deliveryAddressFeignClient.getUserDeliveryAddressList();
    }

    @Operation(summary = "新增收货地址",
               description = "管理端新增收货地址，传入收货人信息、地址详情等字段",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/deliveryAddress/insert")
    public int insertDeliveryAddress(@RequestBody DeliveryAddressDTO e) {
        deliveryAddressFeignClient.save(e);
        return 1;
    }

    @Operation(summary = "修改收货地址",
               description = "管理端修改收货地址，根据ID更新收货人信息、地址详情等字段",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/deliveryAddress/update")
    public int updateDeliveryAddress(@RequestBody DeliveryAddressDTO e) {
        deliveryAddressFeignClient.save(e);
        return 1;
    }

    @Operation(summary = "删除收货地址",
               description = "管理端批量删除收货地址，根据ID列表物理删除",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/deliveryAddress/delete")
    public int deleteDeliveryAddress(@RequestBody @NotNull List ids) {
        return deliveryAddressFeignClient.deleteByIds(ids);
    }

    @Operation(summary = "查询收货地址详情",
               description = "管理端根据ID查询收货地址详细信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/deliveryAddress/detail")
    public DeliveryAddressDTO findDeliveryAddressById(@RequestParam("id") Long id) {
        return deliveryAddressFeignClient.getDetail(id);
    }

    // ========== 商品评论 (productComment) ==========

    @Operation(summary = "分页查询商品评论",
               description = "管理端分页查询商品评论列表，支持按商品ID、用户ID、评论内容等条件筛选，请求参数包含 pageNo、pageSize 及可选筛选条件",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productComment/page")
    public ResponsePageEntity<?> searchCommentPage(@RequestBody Map c) {
        return commentFeignClient.searchByPage(c);
    }

    @Operation(summary = "新增商品评论",
               description = "管理端新增商品评论，传入评论内容、商品ID、评分等信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productComment/insert")
    public int insertComment(@RequestBody Object e) {
        return commentFeignClient.insert(e);
    }

    @Operation(summary = "修改商品评论",
               description = "管理端修改商品评论，根据ID更新评论内容、评分等信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productComment/update")
    public int updateComment(@RequestBody Object e) {
        return commentFeignClient.update(e);
    }

    @Operation(summary = "删除商品评论",
               description = "管理端批量删除商品评论，根据ID列表物理删除",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productComment/delete")
    public int deleteComment(@RequestBody @NotNull List ids) {
        return commentFeignClient.deleteByIds(ids);
    }

    @Operation(summary = "查询商品评论详情",
               description = "管理端根据ID查询商品评论详细信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/productComment/detail")
    public Object findCommentById(@RequestParam("id") Long id) {
        return commentFeignClient.findById(id);
    }

    // ========== 商品收藏 (productFavorites) ==========

    @Operation(summary = "分页查询商品收藏",
               description = "管理端分页查询商品收藏列表，支持按用户ID、商品ID等条件筛选，请求参数包含 pageNo、pageSize 及可选筛选条件",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productFavorites/page")
    public ResponsePageEntity<?> searchFavoritesPage(@RequestBody Map c) {
        return favoritesFeignClient.searchByPage(c);
    }

    @Operation(summary = "新增商品收藏",
               description = "管理端新增商品收藏，传入商品ID、用户ID等信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productFavorites/insert")
    public int insertFavorites(@RequestBody Object e) {
        return favoritesFeignClient.insert(e);
    }

    @Operation(summary = "修改商品收藏",
               description = "管理端修改商品收藏信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productFavorites/update")
    public int updateFavorites(@RequestBody Object e) {
        return favoritesFeignClient.update(e);
    }

    @Operation(summary = "删除商品收藏",
               description = "管理端批量删除商品收藏，根据ID列表物理删除",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productFavorites/delete")
    public int deleteFavorites(@RequestBody @NotNull List ids) {
        return favoritesFeignClient.deleteByIds(ids);
    }

    @Operation(summary = "查询商品收藏详情",
               description = "管理端根据ID查询商品收藏详细信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/productFavorites/detail")
    public Object findFavoritesById(@RequestParam("id") Long id) {
        return favoritesFeignClient.findById(id);
    }

    // ========== 商品浏览记录 (productViewRecord) ==========

    @Operation(summary = "分页查询商品浏览记录",
               description = "管理端分页查询商品浏览记录列表，支持按用户ID、商品ID等条件筛选",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productViewRecord/page")
    public ResponsePageEntity<?> searchViewRecordPage(@RequestBody ProductViewRecordConditionDTO c) {
        return productViewRecordFeignClient.searchByPage(c);
    }

    @Operation(summary = "新增商品浏览记录",
               description = "管理端新增商品浏览记录",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productViewRecord/insert")
    public int insertViewRecord(@RequestBody Object e) {
        return productViewRecordFeignClient.insert(e);
    }

    @Operation(summary = "修改商品浏览记录",
               description = "管理端修改商品浏览记录，根据ID更新记录信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productViewRecord/update")
    public int updateViewRecord(@RequestBody Object e) {
        return productViewRecordFeignClient.update(e);
    }

    @Operation(summary = "删除商品浏览记录",
               description = "管理端批量删除商品浏览记录，根据ID列表物理删除",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/productViewRecord/delete")
    public int deleteViewRecord(@RequestBody @NotNull List ids) {
        return productViewRecordFeignClient.deleteByIds(ids);
    }

    // ========== 购物车 (shoppingCart) ==========

    @Operation(summary = "分页查询购物车",
               description = "管理端分页查询购物车列表，支持按用户ID、商品ID等条件筛选，请求参数包含 pageNo、pageSize 及可选筛选条件",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/shoppingCart/page")
    public ResponsePageEntity<?> searchCartPage(@RequestBody Map c) {
        return cartFeignClient.searchByPage(c);
    }

    @Operation(summary = "新增购物车",
               description = "管理端新增购物车记录，传入商品ID、数量、用户ID等信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/shoppingCart/insert")
    public int insertCart(@RequestBody Object e) {
        return cartFeignClient.insert(e);
    }

    @Operation(summary = "修改购物车",
               description = "管理端修改购物车记录，根据ID更新商品数量等信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/shoppingCart/update")
    public int updateCart(@RequestBody Object e) {
        return cartFeignClient.update(e);
    }

    @Operation(summary = "删除购物车",
               description = "管理端批量删除购物车记录，根据ID列表物理删除",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/shoppingCart/delete")
    public int deleteCart(@RequestBody @NotNull List ids) {
        return cartFeignClient.deleteByIds(ids);
    }

    @Operation(summary = "查询购物车详情",
               description = "管理端根据ID查询购物车记录详细信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/shoppingCart/detail")
    public Object findCartById(@RequestParam("id") Long id) {
        return cartFeignClient.findById(id);
    }
}

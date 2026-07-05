package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

/**
 * 商品收藏 Feign 客户端（管理端）
 * 
* 提供商品收藏的管理端 CRUD 操作，由 mall-admin-api 通过 Feign 调用。
 */
@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "favoritesFeignClient")
public interface FavoritesFeignClient {

    @Operation(summary = "分页查询商品收藏（管理端）",
               description = "管理端分页查询商品收藏列表，支持按用户ID、商品ID等条件筛选，请求参数包含 pageNo、pageSize 及可选筛选条件")
    @PostMapping("/v1/productFavorites/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map<String, Object> condition);

    @Operation(summary = "新增商品收藏（管理端）",
               description = "管理端新增商品收藏，传入商品ID、用户ID等信息")
    @PostMapping("/v1/productFavorites/insert")
    int insert(@RequestBody Object entity);

    @Operation(summary = "修改商品收藏（管理端）",
               description = "管理端修改商品收藏信息")
    @PostMapping("/v1/productFavorites/update")
    int update(@RequestBody Object entity);

    @Operation(summary = "删除商品收藏（管理端）",
               description = "管理端批量删除商品收藏，根据ID列表物理删除")
    @PostMapping("/v1/productFavorites/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);

    @Operation(summary = "查询商品收藏详情（管理端）",
               description = "管理端根据ID查询商品收藏详细信息")
    @GetMapping("/v1/productFavorites/findById")
    Object findById(@RequestParam("id") Long id);
}

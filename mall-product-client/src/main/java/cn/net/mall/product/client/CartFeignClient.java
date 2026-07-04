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
 * 购物车 Feign 客户端（管理端）
 * <p>
 * 提供购物车的管理端 CRUD 操作，由 mall-admin-api 通过 Feign 调用。
 */
@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "cartFeignClient")
public interface CartFeignClient {

    @Operation(summary = "分页查询购物车（管理端）",
               description = "管理端分页查询购物车列表，支持按用户ID、商品ID等条件筛选，请求参数包含 pageNo、pageSize 及可选筛选条件")
    @PostMapping("/v1/shoppingCart/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map<String, Object> condition);

    @Operation(summary = "新增购物车（管理端）",
               description = "管理端新增购物车记录，传入商品ID、数量、用户ID等信息")
    @PostMapping("/v1/shoppingCart/insert")
    int insert(@RequestBody Object entity);

    @Operation(summary = "修改购物车（管理端）",
               description = "管理端修改购物车记录，根据ID更新商品数量等信息")
    @PostMapping("/v1/shoppingCart/update")
    int update(@RequestBody Object entity);

    @Operation(summary = "删除购物车（管理端）",
               description = "管理端批量删除购物车记录，根据ID列表物理删除")
    @PostMapping("/v1/shoppingCart/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);

    @Operation(summary = "查询购物车详情（管理端）",
               description = "管理端根据ID查询购物车记录详细信息")
    @GetMapping("/v1/shoppingCart/findById")
    Object findById(@RequestParam("id") Long id);
}

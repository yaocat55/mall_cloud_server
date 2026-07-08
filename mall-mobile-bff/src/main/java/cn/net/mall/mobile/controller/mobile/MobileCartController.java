package cn.net.mall.mobile.controller.mobile;

import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.ShoppingCartBuyDTO;
import cn.net.mall.product.dto.ShoppingCartConditionDTO;
import cn.net.mall.product.dto.ShoppingCartDTO;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mobile/v1/cart")
@RequiredArgsConstructor
@Tag(name = "移动端-购物车", description = "购物车管理接口")
public class MobileCartController {

    private final ProductFeignClient productFeignClient;

    @Operation(summary = "添加购物车")
    @PostMapping("/add")
    public ApiResult<Boolean> add(@Valid @RequestBody ShoppingCartDTO dto) {
        return ApiResultUtil.success(productFeignClient.addShoppingCart(dto));
    }

    @Operation(summary = "获取购物车商品列表")
    @PostMapping("/list")
    public ApiResult<ShoppingCartBuyDTO> getList(@RequestBody ShoppingCartConditionDTO condition) {
        return ApiResultUtil.success(productFeignClient.getShoppingCartProduct(condition));
    }

    @Operation(summary = "更新购物车商品数量")
    @PostMapping("/update")
    public ApiResult<Void> update(@Valid @RequestBody ShoppingCartDTO dto) {
        productFeignClient.updateShoppingCart(dto);
        return ApiResultUtil.success();
    }

    @Operation(summary = "删除购物车商品")
    @PostMapping("/delete")
    public ApiResult<String> delete(@RequestBody List ids) {
        return ApiResultUtil.success(productFeignClient.deleteShoppingCart(ids));
    }
}

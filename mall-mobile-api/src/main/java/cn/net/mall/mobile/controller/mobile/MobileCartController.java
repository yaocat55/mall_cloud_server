package cn.net.mall.mobile.controller.mobile;

import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.ShoppingCartBuyDTO;
import cn.net.mall.product.dto.ShoppingCartConditionDTO;
import cn.net.mall.product.dto.ShoppingCartDTO;
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
    public Boolean add(@Valid @RequestBody ShoppingCartDTO dto) {
        return productFeignClient.addShoppingCart(dto);
    }

    @Operation(summary = "获取购物车商品列表")
    @PostMapping("/list")
    public ShoppingCartBuyDTO getList(@RequestBody ShoppingCartConditionDTO condition) {
        return productFeignClient.getShoppingCartProduct(condition);
    }

    @Operation(summary = "更新购物车商品数量")
    @PostMapping("/update")
    public void update(@Valid @RequestBody ShoppingCartDTO dto) {
        productFeignClient.updateShoppingCart(dto);
    }

    @Operation(summary = "删除购物车商品")
    @PostMapping("/delete")
    public int delete(@RequestBody List<Long> ids) {
        return productFeignClient.deleteShoppingCart(ids);
    }
}

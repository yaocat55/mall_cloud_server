package cn.net.mall.product.controller.internal;

import cn.net.mall.product.dto.ShoppingCartBuyDTO;
import cn.net.mall.product.dto.ShoppingCartConditionDTO;
import cn.net.mall.product.dto.ShoppingCartDTO;
import cn.net.mall.product.entity.shopping.ShoppingCartEntity;
import cn.net.mall.product.service.shopping.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.CollectionUtils;
import cn.hutool.core.bean.BeanUtil;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 内部购物车接口
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>order-service（订单服务）— 查询购物车商品信息用于下单确认</li>
 * </ul>
 * <b>不对外暴露</b>，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-购物车", description = "内部微服务：order-service 通过 Feign 调用")
@RestController
@RequestMapping("/v1/shoppingCart")
public class ShoppingCartInternalController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartInternalController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @Operation(summary = "批量查询购物车信息",
               description = "内部服务：由 order-service 通过 Feign 调用，根据ID集合批量获取购物车商品信息")
    @PostMapping("/findByIds")
    public List<ShoppingCartDTO> findShoppingCartByIds(@RequestBody List<Long> ids) {
        List<ShoppingCartEntity> entities = shoppingCartService.findByIds(ids);
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream().map(x -> {
            ShoppingCartDTO dto = new ShoppingCartDTO();
            BeanUtil.copyProperties(x, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Operation(summary = "查询购物车商品列表",
               description = "内部服务：由 order-service 通过 Feign 调用，根据条件获取购物车商品信息用于下单确认")
    @PostMapping("/getShoppingCartProduct")
    public ShoppingCartBuyDTO getShoppingCartProduct(@RequestBody ShoppingCartConditionDTO dto) {
        return shoppingCartService.getShoppingCartProduct(dto);
    }
}

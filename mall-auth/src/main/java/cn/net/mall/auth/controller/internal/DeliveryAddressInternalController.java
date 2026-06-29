package cn.net.mall.auth.controller.internal;

import cn.net.mall.auth.service.shopping.DeliveryAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部收货地址接口
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-order（订单服务）— 查询用户收货地址</li>
 * </ul>
 * <b>不对外暴露</b>，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-收货地址", description = "内部微服务：mall-order 通过 Feign 调用")
@RestController
@RequestMapping("/v1/deliveryAddress")
public class DeliveryAddressInternalController {

    private final DeliveryAddressService deliveryAddressService;

    public DeliveryAddressInternalController(DeliveryAddressService deliveryAddressService) {
        this.deliveryAddressService = deliveryAddressService;
    }

    @Operation(summary = "批量查询收货地址",
               description = "内部服务：由 mall-order 通过 Feign 调用，根据ID集合批量查询收货地址")
    @PostMapping("/findByIds")
    public List<?> findByIds(@RequestBody List<Long> ids) {
        return deliveryAddressService.findByIds(ids);
    }
}

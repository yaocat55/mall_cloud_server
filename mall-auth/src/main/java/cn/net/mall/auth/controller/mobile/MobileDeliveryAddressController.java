package cn.net.mall.auth.controller.mobile;

import cn.net.mall.auth.entity.shopping.web.DeliveryAddressDefaultWebEntity;
import cn.net.mall.auth.entity.shopping.web.DeliveryAddressWebEntity;
import cn.net.mall.auth.service.shopping.DeliveryAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * web端收货地址controller
 *
 * @date 2024/9/22 上午10:40
 */
@Tag(name = "web端收货地址操作", description = "web端收货地址操作")
@RestController
@RequestMapping("/v1/mobile/deliveryAddress")
@Validated
@AllArgsConstructor
public class MobileDeliveryAddressController {

    private final DeliveryAddressService deliveryAddressService;


    /**
     * 设置默认收货地址
     *
     * @param deliveryAddressWebEntity 收货地址实体
     */
    @Operation(summary = "设置默认收货地址", description = "设置默认收货地址")
    @PostMapping("/setDefaultDeliveryAddress")
    public void setDefaultDeliveryAddress(@RequestBody @Valid DeliveryAddressDefaultWebEntity deliveryAddressWebEntity) {
        deliveryAddressService.setDefaultDeliveryAddress(deliveryAddressWebEntity);
    }


    /**
     * 获取某用户收货地址列表
     *
     * @return 收货地址列表
     */
    @Operation(summary = "获取某用户收货地址列表", description = "获取某用户收货地址列表")
    @GetMapping("/getUserDeliveryAddressList")
    public List<DeliveryAddressWebEntity> getUserDeliveryAddressList() {
        return deliveryAddressService.getUserDeliveryAddressList();
    }

    /**
     * 获取收货地址详情
     *
     * @return 收货地址详情
     */
    @Operation(summary = "获取收货地址详情", description = "获取收货地址详情")
    @GetMapping("/getDetail")
    public DeliveryAddressWebEntity getDetail(@Parameter(description = "收货地址ID")
    @RequestParam("id") Long id) {
        return deliveryAddressService.getDetail(id);
    }

    /**
     * 批量删除收货地址
     *
     * @param ids 收货地址ID集合
     * @return 影响行数
     */
    @Operation(summary = "批量删除收货地址", description = "批量删除收货地址")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return deliveryAddressService.deleteByIds(ids);
    }

    /**
     * 保存收货地址
     *
     * @param deliveryAddressWebEntity 收货地址实体
     * @return 影响行数
     */
    @Operation(summary = "保存收货地址", description = "保存收货地址")
    @PostMapping("/save")
    public void save(@RequestBody @Valid DeliveryAddressWebEntity deliveryAddressWebEntity) {
        deliveryAddressService.save(deliveryAddressWebEntity);
    }
}

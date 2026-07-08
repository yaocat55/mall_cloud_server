package cn.net.mall.admin.client;

import cn.net.mall.admin.dto.DeliveryAddressDTO;
import cn.net.mall.admin.dto.DeliveryAddressDefaultDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.net.mall.admin.constant.AppConstant.ADMIN_SERVICE_NAME;

/**
 * 收货地址 Feign 客户端
 * 
* **调用方：**
 * 
*   - mall-order（订单服务）— 查询、管理用户收货地址
 *   - mall-admin-api（管理端 BFF）— 管理后台收货地址操作
 * 
* **不对外暴露**，仅限服务间 Feign 调用
 */
@FeignClient(value = ADMIN_SERVICE_NAME, contextId = "deliveryAddressFeignClient")
public interface DeliveryAddressFeignClient {

    /**
     * 设置默认收货地址
     *
     * @param deliveryAddressDefaultDTO 收货地址实体
     */
    @Operation(summary = "设置默认收货地址",
               description = "移动端：设置默认收货地址，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/mobile/deliveryAddress/setDefaultDeliveryAddress")
    void setDefaultDeliveryAddress(@RequestBody DeliveryAddressDefaultDTO deliveryAddressDefaultDTO);

    /**
     * 获取某用户收货地址列表
     *
     * @return 收货地址列表
     */
    @Operation(summary = "获取某用户收货地址列表",
               description = "移动端：获取当前用户收货地址列表，由 mall-admin-api 通过 Feign 调用")
    @GetMapping("/v1/internal/deliveryAddress/getList")
    List getUserDeliveryAddressList();

    /**
     * 获取收货地址详情
     *
     * @return 收货地址详情
     */
    @Operation(summary = "获取收货地址详情",
               description = "移动端：根据ID获取收货地址详情，由 mall-admin-api 通过 Feign 调用")
    @GetMapping("/v1/mobile/deliveryAddress/getDetail")
    DeliveryAddressDTO getDetail(@RequestParam("id") Long id);

    /**
     * 批量删除收货地址
     *
     * @param ids 收货地址ID集合
     * @return 影响行数
     */
    @Operation(summary = "批量删除收货地址",
               description = "移动端：批量删除收货地址，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/internal/deliveryAddress/delete")
    int deleteByIds(@RequestBody List ids);

    /**
     * 保存收货地址
     *
     * @param deliveryAddressDTO 收货地址实体
     */
    @Operation(summary = "保存收货地址",
               description = "移动端：新增或更新收货地址，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/internal/deliveryAddress/save")
    void save(@RequestBody DeliveryAddressDTO deliveryAddressDTO);
}

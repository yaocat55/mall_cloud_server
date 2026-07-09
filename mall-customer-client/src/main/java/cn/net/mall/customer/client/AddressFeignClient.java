package cn.net.mall.customer.client;

import cn.net.mall.customer.dto.AddressDTO;
import cn.net.mall.customer.dto.AddressDefaultDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址 Feign 客户端
 *
 * **调用方：**
 * - mall-mobile-bff（移动端 BFF）
 * - mall-order（订单服务）
 */
@FeignClient(value = "mall-customer-api", contextId = "addressFeignClient")
public interface AddressFeignClient {

    @Operation(summary = "获取用户收货地址列表")
    @GetMapping("/v1/internal/address/list")
    List<AddressDTO> getUserAddressList();

    @Operation(summary = "获取收货地址详情")
    @GetMapping("/v1/internal/address/detail")
    AddressDTO getDetail(@RequestParam("id") Long id);

    @Operation(summary = "保存收货地址")
    @PostMapping("/v1/internal/address/save")
    void save(@RequestBody @Valid AddressDTO dto);

    @Operation(summary = "批量删除收货地址")
    @PostMapping("/v1/internal/address/delete")
    int deleteByIds(@RequestBody List<Long> ids);

    @Operation(summary = "设置默认收货地址")
    @PostMapping("/v1/internal/address/setDefault")
    void setDefaultAddress(@RequestBody AddressDefaultDTO dto);
}

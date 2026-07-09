package cn.net.mall.customer.controller;

import cn.net.mall.customer.dto.AddressDTO;
import cn.net.mall.customer.dto.AddressDefaultDTO;
import cn.net.mall.customer.service.CustomerAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址内部接口（Feign 调用）
 */
@Tag(name = "C端-收货地址", description = "内部Feign调用：C端用户收货地址管理")
@RestController
@RequestMapping("/v1/internal/address")
public class AddressInternalController {

    private final CustomerAddressService customerAddressService;

    public AddressInternalController(CustomerAddressService customerAddressService) {
        this.customerAddressService = customerAddressService;
    }

    @Operation(summary = "获取用户收货地址列表")
    @GetMapping("/list")
    public List<AddressDTO> getUserAddressList() {
        return customerAddressService.getUserAddressList();
    }

    @Operation(summary = "获取收货地址详情")
    @GetMapping("/detail")
    public AddressDTO getDetail(@RequestParam("id") Long id) {
        return customerAddressService.getDetail(id);
    }

    @Operation(summary = "保存收货地址")
    @PostMapping("/save")
    public void save(@RequestBody @Valid AddressDTO dto) {
        customerAddressService.save(dto);
    }

    @Operation(summary = "批量删除收货地址")
    @PostMapping("/delete")
    public int deleteByIds(@RequestBody List<Long> ids) {
        return customerAddressService.deleteByIds(ids);
    }

    @Operation(summary = "设置默认收货地址")
    @PostMapping("/setDefault")
    public void setDefaultAddress(@RequestBody AddressDefaultDTO dto) {
        customerAddressService.setDefaultAddress(dto);
    }
}

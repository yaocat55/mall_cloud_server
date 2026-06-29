package cn.net.mall.admin.controller.admin;

import cn.net.mall.auth.client.DeliveryAddressFeignClient;
import cn.net.mall.auth.client.UserFeignClient;
import cn.net.mall.auth.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台用户管理 BFF 控制器
 * <p>
 * 聚合用户信息、收货地址等数据，提供管理后台所需的用户管理接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/user")
@RequiredArgsConstructor
@Tag(name = "管理后台-用户管理", description = "用户信息、收货地址等管理接口")
public class AdminUserController {

    private final UserFeignClient userFeignClient;
    private final DeliveryAddressFeignClient deliveryAddressFeignClient;

    // ==================== 用户信息 ====================

    @Operation(summary = "通过ID查询用户信息", description = "批量通过ID查询用户信息")
    @GetMapping("/findByIds")
    public List<UserDTO> findByIds(@RequestBody List<Long> ids) {
        return userFeignClient.findByIds(ids);
    }

    @Operation(summary = "通过手机号查询用户信息")
    @GetMapping("/findByPhone")
    public UserDTO findByPhone(@RequestParam String phone) {
        return userFeignClient.findByPhone(phone);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public UserDTO register(@Valid @RequestBody RegisterDTO registerDTO) {
        return userFeignClient.register(registerDTO);
    }

    @Operation(summary = "更新用户头像")
    @PostMapping("/updateAvatar")
    public void updateAvatar(@Valid @RequestBody UserAvatarDTO userAvatarDTO) {
        userFeignClient.updateAvatar(userAvatarDTO);
    }

    @Operation(summary = "更新用户信息")
    @PostMapping("/updateUser")
    public void updateUser(@Valid @RequestBody UpdateUserDTO updateUserDTO) {
        userFeignClient.updateUser(updateUserDTO);
    }

    @Operation(summary = "绑定手机号")
    @PostMapping("/bindPhone")
    public void bindPhone(@Valid @RequestBody BindPhoneDTO bindPhoneDTO) {
        userFeignClient.bindPhone(bindPhoneDTO);
    }

    // ==================== 收货地址 ====================

    @Operation(summary = "获取用户收货地址列表")
    @GetMapping("/deliveryAddress/list")
    public List<DeliveryAddressDTO> getDeliveryAddressList() {
        return deliveryAddressFeignClient.getUserDeliveryAddressList();
    }

    @Operation(summary = "获取收货地址详情")
    @GetMapping("/deliveryAddress/detail")
    public DeliveryAddressDTO getDeliveryAddressDetail(@RequestParam("id") Long id) {
        return deliveryAddressFeignClient.getDetail(id);
    }

    @Operation(summary = "保存收货地址")
    @PostMapping("/deliveryAddress/save")
    public void saveDeliveryAddress(@RequestBody DeliveryAddressDTO deliveryAddressDTO) {
        deliveryAddressFeignClient.save(deliveryAddressDTO);
    }

    @Operation(summary = "批量删除收货地址")
    @PostMapping("/deliveryAddress/delete")
    public int deleteDeliveryAddress(@RequestBody List<Long> ids) {
        return deliveryAddressFeignClient.deleteByIds(ids);
    }

    @Operation(summary = "设置默认收货地址")
    @PostMapping("/deliveryAddress/setDefault")
    public void setDefaultDeliveryAddress(@RequestBody DeliveryAddressDefaultDTO deliveryAddressDefaultDTO) {
        deliveryAddressFeignClient.setDefaultDeliveryAddress(deliveryAddressDefaultDTO);
    }
}

package cn.net.mall.admin.controller.admin;

import cn.net.mall.auth.client.DeliveryAddressFeignClient;
import cn.net.mall.auth.client.DeptFeignClient;
import cn.net.mall.auth.client.JobFeignClient;
import cn.net.mall.auth.client.RoleFeignClient;
import cn.net.mall.auth.client.UserFeignClient;
import cn.net.mall.auth.dto.*;
import cn.net.mall.auth.dto.auth.DeptTreeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 管理后台用户管理 BFF 控制器
 * <p>
 * 聚合用户信息、角色、部门、岗位等数据，提供管理后台所需的用户管理接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/user")
@RequiredArgsConstructor
@Tag(name = "管理后台-用户管理", description = "用户信息、收货地址等管理接口")
public class AdminUserController {

    private final UserFeignClient userFeignClient;
    private final DeliveryAddressFeignClient deliveryAddressFeignClient;
    private final RoleFeignClient roleFeignClient;
    private final DeptFeignClient deptFeignClient;
    private final JobFeignClient jobFeignClient;

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

    // ==================== 聚合接口 ====================

    @Operation(summary = "获取用户编辑页数据",
               description = "聚合用户基本信息 + 角色列表 + 部门树 + 岗位列表，支持管理后台用户编辑页面")
    @GetMapping("/{id}/edit-data")
    public Map<String, Object> getUserEditData(@PathVariable Long id) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 1. 用户基本信息
        try {
            List<UserDTO> users = userFeignClient.findByIds(Collections.singletonList(id));
            result.put("user", users != null && !users.isEmpty() ? users.get(0) : null);
        } catch (Exception e) {
            log.warn("获取用户信息失败, id={}", id, e);
            result.put("user", null);
        }

        // 2. 所有角色（用于角色分配下拉框）
        try {
            result.put("roles", roleFeignClient.getAll());
        } catch (Exception e) {
            log.warn("获取角色列表失败", e);
            result.put("roles", Collections.emptyList());
        }

        // 3. 部门树（用于部门选择）
        try {
            result.put("deptTree", deptFeignClient.searchByTree(Collections.emptyMap()));
        } catch (Exception e) {
            log.warn("获取部门树失败", e);
            result.put("deptTree", Collections.emptyList());
        }

        // 4. 所有岗位（用于岗位分配下拉框）
        try {
            result.put("jobs", jobFeignClient.getAll());
        } catch (Exception e) {
            log.warn("获取岗位列表失败", e);
            result.put("jobs", Collections.emptyList());
        }

        return result;
    }
}

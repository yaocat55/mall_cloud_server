package cn.net.mall.mobile.controller.mobile;

import cn.net.mall.admin.client.DeliveryAddressFeignClient;
import cn.net.mall.admin.client.UserFeignClient;
import cn.net.mall.admin.dto.DeliveryAddressDTO;
import cn.net.mall.admin.dto.DeliveryAddressDefaultDTO;
import cn.net.mall.admin.dto.UpdateUserDTO;
import cn.net.mall.admin.dto.UserAvatarDTO;
import cn.net.mall.basic.client.AreaFeignClient;
import cn.net.mall.basic.client.UploadFeignClient;
import cn.net.mall.basic.dto.AreaDTO;
import cn.net.mall.basic.dto.FileDTO;
import cn.net.mall.mobile.dto.UserProfileDTO;
import cn.net.mall.order.client.OrderFeignClient;
import cn.net.mall.product.client.IndexFeignClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/mobile/v1/user")
@RequiredArgsConstructor
@Tag(name = "移动端-用户", description = "用户信息、收货地址、个人资料接口")
public class MobileUserController {

    private final UserFeignClient userFeignClient;
    private final DeliveryAddressFeignClient deliveryAddressFeignClient;
    private final AreaFeignClient areaFeignClient;
    private final UploadFeignClient uploadFeignClient;
    private final OrderFeignClient orderFeignClient;
    private final IndexFeignClient indexFeignClient;

    @Operation(summary = "获取用户中心聚合数据",
               description = "聚合用户基本信息 + 各状态订单数量 + 推荐商品列表，一次接口返回用户中心所需全部数据\n\n"
                           + "**注意事项：**\n"
                           + "- 需携带 Bearer Token（Authorization 请求头）",
               security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/profile")
    public UserProfileDTO getProfile() {
        UserProfileDTO result = new UserProfileDTO();
        try { result.setUser(userFeignClient.getUserDetail());
        } catch (Exception e) { log.warn("获取用户详情失败", e); }
        try { result.setOrderCounts(orderFeignClient.getUserOrderTradeCount());
        } catch (Exception e) { log.warn("获取订单统计失败", e); }
        try { result.setRecommendProducts(indexFeignClient.getIndexProductList(0));
        } catch (Exception e) { log.warn("获取推荐商品失败", e); }
        return result;
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/detail")
    public Object getUserDetail() {
        return userFeignClient.getUserDetail();
    }

    @Operation(summary = "更新用户资料")
    @PostMapping("/update")
    public void updateUser(@Valid @RequestBody UpdateUserDTO dto) {
        userFeignClient.updateUser(dto);
    }

    @Operation(summary = "更新头像", description = "上传头像图片并更新用户头像")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateAvatar(@RequestParam("file") MultipartFile file) throws Exception {
        FileDTO fileDTO = uploadFeignClient.imageUpload(file);
        UserAvatarDTO avatarDTO = new UserAvatarDTO();
        avatarDTO.setFileName(file.getOriginalFilename());
        avatarDTO.setFileUrl(fileDTO.getDownloadUrl());
        userFeignClient.updateAvatar(avatarDTO);
    }

    @Operation(summary = "获取收货地址列表")
    @GetMapping("/address/list")
    public List getAddressList() {
        return deliveryAddressFeignClient.getUserDeliveryAddressList();
    }

    @Operation(summary = "获取收货地址详情")
    @GetMapping("/address/detail")
    public DeliveryAddressDTO getAddressDetail(@RequestParam("id") Long id) {
        return deliveryAddressFeignClient.getDetail(id);
    }

    @Operation(summary = "保存收货地址")
    @PostMapping("/address/save")
    public void saveAddress(@Valid @RequestBody DeliveryAddressDTO dto) {
        deliveryAddressFeignClient.save(dto);
    }

    @Operation(summary = "删除收货地址")
    @PostMapping("/address/delete")
    public int deleteAddress(@RequestBody List ids) {
        return deliveryAddressFeignClient.deleteByIds(ids);
    }

    @Operation(summary = "设置默认收货地址")
    @PostMapping("/address/setDefault")
    public void setDefaultAddress(@RequestBody DeliveryAddressDefaultDTO dto) {
        deliveryAddressFeignClient.setDefaultDeliveryAddress(dto);
    }

    @Operation(summary = "获取地区列表", description = "根据父级ID获取地区")
    @GetMapping("/area")
    public List getArea(@RequestParam("parentId") Long parentId) {
        return areaFeignClient.queryByParentId(parentId);
    }
}

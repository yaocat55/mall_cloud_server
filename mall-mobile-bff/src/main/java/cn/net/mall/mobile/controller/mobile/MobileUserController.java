package cn.net.mall.mobile.controller.mobile;

import cn.net.mall.basic.client.AreaFeignClient;
import cn.net.mall.basic.client.UploadFeignClient;
import cn.net.mall.basic.dto.AreaDTO;
import cn.net.mall.basic.dto.FileDTO;
import cn.net.mall.customer.client.AddressFeignClient;
import cn.net.mall.customer.client.MemberFeignClient;
import cn.net.mall.customer.dto.AddressDTO;
import cn.net.mall.customer.dto.AddressDefaultDTO;
import cn.net.mall.customer.dto.CustomerAvatarDTO;
import cn.net.mall.customer.dto.CustomerUpdateDTO;
import cn.net.mall.mobile.dto.UserProfileDTO;
import cn.net.mall.order.client.OrderFeignClient;
import cn.net.mall.product.client.IndexFeignClient;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
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

    private final MemberFeignClient memberFeignClient;
    private final AddressFeignClient addressFeignClient;
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
    public ApiResult<UserProfileDTO> getProfile() {
        UserProfileDTO result = new UserProfileDTO();
        try { result.setUser(memberFeignClient.getUserDetail());
        } catch (Exception e) { log.warn("获取用户详情失败", e); }
        try { result.setOrderCounts(orderFeignClient.getUserOrderTradeCount());
        } catch (Exception e) { log.warn("获取订单统计失败", e); }
        try { result.setRecommendProducts(indexFeignClient.getIndexProductList(0));
        } catch (Exception e) { log.warn("获取推荐商品失败", e); }
        return ApiResultUtil.success(result);
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/detail")
    public ApiResult<Object> getUserDetail() {
        return ApiResultUtil.success(memberFeignClient.getUserDetail());
    }

    @Operation(summary = "更新用户资料")
    @PostMapping("/update")
    public ApiResult<Void> updateUser(@Valid @RequestBody CustomerUpdateDTO dto) {
        memberFeignClient.updateUser(dto);
        return ApiResultUtil.success();
    }

    @Operation(summary = "更新头像", description = "上传头像图片并更新用户头像")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<Void> updateAvatar(@RequestParam("file") MultipartFile file) throws Exception {
        FileDTO fileDTO = uploadFeignClient.imageUpload(file);
        CustomerAvatarDTO avatarDTO = new CustomerAvatarDTO();
        avatarDTO.setFileName(file.getOriginalFilename());
        avatarDTO.setFileUrl(fileDTO.getDownloadUrl());
        memberFeignClient.updateAvatar(avatarDTO);
        return ApiResultUtil.success();
    }

    @Operation(summary = "获取收货地址列表")
    @GetMapping("/address/list")
    public ApiResult<List> getAddressList() {
        return ApiResultUtil.success(addressFeignClient.getUserAddressList());
    }

    @Operation(summary = "获取收货地址详情")
    @GetMapping("/address/detail")
    public ApiResult<AddressDTO> getAddressDetail(@RequestParam("id") Long id) {
        return ApiResultUtil.success(addressFeignClient.getDetail(id));
    }

    @Operation(summary = "保存收货地址")
    @PostMapping("/address/save")
    public ApiResult<Void> saveAddress(@Valid @RequestBody AddressDTO dto) {
        addressFeignClient.save(dto);
        return ApiResultUtil.success();
    }

    @Operation(summary = "删除收货地址")
    @PostMapping("/address/delete")
    public ApiResult<Integer> deleteAddress(@RequestBody List<Long> ids) {
        return ApiResultUtil.success(addressFeignClient.deleteByIds(ids));
    }

    @Operation(summary = "设置默认收货地址")
    @PostMapping("/address/setDefault")
    public ApiResult<Void> setDefaultAddress(@RequestBody AddressDefaultDTO dto) {
        addressFeignClient.setDefaultAddress(dto);
        return ApiResultUtil.success();
    }

    @Operation(summary = "获取地区列表", description = "根据父级ID获取地区")
    @GetMapping("/area")
    public ApiResult<List> getArea(@RequestParam("parentId") Long parentId) {
        return ApiResultUtil.success(areaFeignClient.queryByParentId(parentId));
    }
}

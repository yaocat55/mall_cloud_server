package cn.net.mall.customer.client;

import cn.net.mall.customer.client.fallback.MemberFeignFallbackFactory;
import cn.net.mall.customer.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "mall-customer-api", contextId = "memberFeignClient",
        fallbackFactory = MemberFeignFallbackFactory.class)
public interface MemberFeignClient {

    @Operation(summary = "会员注册")
    @PostMapping("/v1/mobile/user/register")
    MemberDTO register(@RequestBody @Validated MemberRegisterDTO registerDTO);

    @Operation(summary = "账号密码登录")
    @PostMapping("/v1/mobile/user/login")
    MemberDTO login(@RequestBody @Valid MemberLoginDTO loginDTO);

    @Operation(summary = "手机号验证码登录")
    @PostMapping("/v1/mobile/user/loginByPhone")
    MemberDTO loginByPhone(@RequestBody @Valid MemberPhoneLoginDTO phoneLoginDTO);

    @Operation(summary = "退出登录")
    @PostMapping("/v1/mobile/user/logout")
    void logout(@RequestHeader("Authorization") String authorization);

    @Operation(summary = "获取图形验证码 Key")
    @GetMapping("/v1/mobile/user/getCode")
    String getCode(@RequestParam("uuid") String uuid);

    // ========== 用户信息管理 ==========

    @Operation(summary = "获取当前用户详情")
    @GetMapping("/v1/mobile/user/detail")
    CustomerUserDTO getUserDetail();

    @Operation(summary = "更新用户资料")
    @PostMapping("/v1/mobile/user/update")
    void updateUser(@RequestBody @Valid CustomerUpdateDTO dto);

    @Operation(summary = "更新用户头像")
    @PostMapping("/v1/mobile/user/avatar")
    void updateAvatar(@RequestBody @Valid CustomerAvatarDTO dto);

    // ========== 内部调用 ==========

    @Operation(summary = "批量查询用户信息", description = "内部Feign调用：根据ID集合批量查询C端用户")
    @PostMapping("/v1/internal/user/findByIds")
    List<CustomerUserDTO> findByIds(@RequestBody List<Long> ids);
}

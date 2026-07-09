package cn.net.mall.customer.controller;

import cn.net.mall.customer.dto.*;
import cn.net.mall.customer.service.CustomerUserProfileService;
import cn.net.mall.customer.service.CustomerUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "C端-会员认证", description = "C端：会员注册、登录、个人信息管理")
@AllArgsConstructor
@RestController
@RequestMapping("/v1/mobile/user")
@Validated
public class CustomerUserController {
    private final CustomerUserService customerUserService;
    private final CustomerUserProfileService customerUserProfileService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public MemberDTO register(@RequestBody @Validated MemberRegisterDTO registerDTO) {
        return customerUserService.register(registerDTO);
    }

    @Operation(summary = "账号密码登录")
    @PostMapping("/login")
    public MemberDTO login(@RequestBody @Valid MemberLoginDTO loginDTO) {
        return customerUserService.login(loginDTO);
    }

    @Operation(summary = "手机号验证码登录")
    @PostMapping("/loginByPhone")
    public MemberDTO loginByPhone(@RequestBody @Valid MemberPhoneLoginDTO phoneLoginDTO) {
        return customerUserService.loginByPhone(phoneLoginDTO);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String authorization) {
        customerUserService.logout(authorization);
    }

    @Operation(summary = "获取图形验证码")
    @GetMapping("/getCode")
    public String getCode(String uuid) {
        return customerUserService.getCaptchaKey(uuid);
    }

    // ========== 用户信息管理 ==========

    @Operation(summary = "获取当前用户详情")
    @GetMapping("/detail")
    public CustomerUserDTO getUserDetail() {
        return customerUserProfileService.getUserDetail();
    }

    @Operation(summary = "更新用户资料")
    @PostMapping("/update")
    public void updateUser(@RequestBody @Valid CustomerUpdateDTO dto) {
        customerUserProfileService.updateUser(dto);
    }

    @Operation(summary = "更新用户头像")
    @PostMapping("/avatar")
    public void updateAvatar(@RequestBody @Valid CustomerAvatarDTO dto) {
        customerUserProfileService.updateAvatar(dto);
    }
}

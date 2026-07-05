package cn.net.mall.customer.controller;

import cn.net.mall.customer.client.dto.*;
import cn.net.mall.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "C端-会员认证", description = "C端：会员注册、登录、登出")
@AllArgsConstructor
@RestController
@RequestMapping("/v1/mobile/user")
@Validated
public class CustomerController {
    private final CustomerService customerService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public MemberDTO register(@RequestBody @Validated MemberRegisterDTO registerDTO) {
        return customerService.register(registerDTO);
    }

    @Operation(summary = "账号密码登录")
    @PostMapping("/login")
    public MemberDTO login(@RequestBody @Valid MemberLoginDTO loginDTO) {
        return customerService.login(loginDTO);
    }

    @Operation(summary = "手机号验证码登录")
    @PostMapping("/loginByPhone")
    public MemberDTO loginByPhone(@RequestBody @Valid MemberPhoneLoginDTO phoneLoginDTO) {
        return customerService.loginByPhone(phoneLoginDTO);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String authorization) {
        customerService.logout(authorization);
    }

    @Operation(summary = "获取图形验证码")
    @GetMapping("/getCode")
    public String getCode(String uuid) {
        return customerService.getCaptchaKey(uuid);
    }
}

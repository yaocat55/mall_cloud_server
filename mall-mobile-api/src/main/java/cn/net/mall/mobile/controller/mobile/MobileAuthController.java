package cn.net.mall.mobile.controller.mobile;

import cn.net.mall.auth.client.UserFeignClient;
import cn.net.mall.auth.dto.*;
import cn.net.mall.basic.client.SmsFeignClient;
import cn.net.mall.basic.dto.SendCodeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/mobile/v1/auth")
@RequiredArgsConstructor
@Tag(name = "移动端-认证", description = "移动端登录注册聚合接口")
public class MobileAuthController {

    private final UserFeignClient userFeignClient;
    private final SmsFeignClient smsFeignClient;

    @Operation(summary = "手机号密码登录", description = "手机号+密码登录，返回token")
    @PostMapping("/login")
    public TokenDTO login(@Valid @RequestBody UserLoginDTO dto) {
        return userFeignClient.login(dto);
    }

    @Operation(summary = "手机号验证码登录", description = "手机号+短信验证码登录")
    @PostMapping("/loginByPhone")
    public TokenDTO loginByPhone(@Valid @RequestBody UserPhoneLoginDTO dto) {
        return userFeignClient.loginByPhone(dto);
    }

    @Operation(summary = "用户注册", description = "手机号注册")
    @PostMapping("/register")
    public UserDTO register(@Validated @RequestBody RegisterDTO dto) {
        return userFeignClient.register(dto);
    }

    @Operation(summary = "获取图形验证码")
    @GetMapping("/getCode")
    public CaptchaDTO getCode() {
        return userFeignClient.getCode();
    }

    @Operation(summary = "发送短信验证码", description = "发送手机短信验证码")
    @PostMapping("/sendSms")
    public void sendSms(@Validated @RequestBody SendCodeDTO dto) {
        smsFeignClient.sendSmsCode(dto);
    }

    @Operation(summary = "重置密码")
    @PostMapping("/resetPassword")
    public boolean resetPassword(@RequestBody ResetPasswordDTO dto) {
        return userFeignClient.resetPassword(dto);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public void logout() {
        // token清理由前端处理
    }
}

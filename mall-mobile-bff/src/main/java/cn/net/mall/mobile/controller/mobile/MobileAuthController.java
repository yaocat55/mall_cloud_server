package cn.net.mall.mobile.controller.mobile;

import cn.net.mall.basic.client.SmsFeignClient;
import cn.net.mall.basic.dto.SendCodeDTO;
import cn.net.mall.customer.client.MemberFeignClient;
import cn.net.mall.customer.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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

    private final MemberFeignClient memberFeignClient;
    private final SmsFeignClient smsFeignClient;

    @Operation(summary = "手机号密码登录", description = "手机号+密码登录，返回token")
    @PostMapping("/login")
    public ApiResult<MemberDTO> login(@Valid @RequestBody MemberLoginDTO dto) {
        return memberFeignClient.login(dto);
    }

    @Operation(summary = "手机号验证码登录", description = "手机号+短信验证码登录")
    @PostMapping("/loginByPhone")
    public ApiResult<MemberDTO> loginByPhone(@Valid @RequestBody MemberPhoneLoginDTO dto) {
        return memberFeignClient.loginByPhone(dto);
    }

    @Operation(summary = "用户注册", description = "手机号注册")
    @PostMapping("/register")
    public ApiResult<MemberDTO> register(@Validated @RequestBody MemberRegisterDTO dto) {
        return memberFeignClient.register(dto);
    }

    @Operation(summary = "获取图形验证码")
    @GetMapping("/getCode")
    public ApiResult<String> getCode(@RequestParam(value = "uuid") String uuid) {
        return memberFeignClient.getCode(uuid);
    }

    @Operation(summary = "发送短信验证码", description = "发送手机短信验证码")
    @PostMapping("/sendSms")
    public ApiResult<Void> sendSms(@Validated @RequestBody SendCodeDTO dto) {
        smsFeignClient.sendSmsCode(dto);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public ApiResult<Void> logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        memberFeignClient.logout(authorization);
    }
}

package cn.net.mall.customer.client;

import cn.net.mall.customer.client.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "mall-customer-api")
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
}

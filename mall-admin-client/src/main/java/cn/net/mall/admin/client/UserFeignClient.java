package cn.net.mall.admin.client;

import cn.net.mall.admin.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static cn.net.mall.admin.constant.AppConstant.ADMIN_SERVICE_NAME;

/**
 * 用户 Feign 客户端
 *
 * **调用方：**
 *   - mall-admin-api（管理端 BFF）
 *
 * **不对外暴露**，仅限服务间 Feign 调用
 */
@FeignClient(value = ADMIN_SERVICE_NAME)
public interface UserFeignClient {

    @Operation(summary = "批量查询用户信息",
               description = "内部服务：由 mall-admin-api 通过 Feign 调用，根据ID集合批量查询用户信息")
    @PostMapping("/v1/internal/user/findByIds")
    List<UserDTO> findByIds(@RequestBody List<Long> ids);

    @Operation(summary = "通过手机号查询用户信息",
               description = "内部服务：由 mall-admin-api 通过 Feign 调用，根据手机号精确查询用户")
    @GetMapping("/v1/internal/user/findByPhone")
    UserDTO findByPhone(@RequestParam String phone);

    @Operation(summary = "获取动态验证码",
               description = "内部服务：获取图形验证码（base64图片），由 mall-admin-api 调用")
    @GetMapping(value = "/v1/auth/web/user/getCode")
    CaptchaDTO getCode();

    @Operation(summary = "用户登录",
               description = "Web端：用户账号密码登录，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/auth/web/user/login")
    TokenDTO login(@Valid @RequestBody UserLoginDTO userLoginDTO);

    @Operation(summary = "手机号登录",
               description = "Web端：用户手机号+验证码登录，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/auth/web/user/loginByPhone")
    TokenDTO loginByPhone(@Valid @RequestBody UserPhoneLoginDTO userPhoneLoginDTO);

    @Operation(summary = "重置密码",
               description = "Web端：用户重置密码，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/auth/web/user/resetPassword")
    boolean resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO);

    @Operation(summary = "获取用户信息",
               description = "内部服务：获取当前用户基本信息，由 mall-admin-api 通过 Feign 调用")
    @GetMapping(value = "/v1/auth/web/user/info")
    UserInfoDTO getUserInfo();

    @Operation(summary = "获取当前用户详情",
               description = "内部服务：获取当前登录用户完整详情，由 mall-admin-api 通过 Feign 调用")
    @GetMapping(value = "/v1/auth/web/user/getUserDetail")
    UserDTO getUserDetail();

    @Operation(summary = "更新头像",
               description = "内部服务：由 mall-admin-api 通过 Feign 调用，更新用户头像地址")
    @PostMapping("/v1/internal/user/updateAvatar")
    void updateAvatar(@RequestBody @Valid UserAvatarDTO userAvatarDTO);

    @Operation(summary = "更新用户信息",
               description = "内部服务：更新用户基本信息，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/auth/web/user/updateUser")
    void updateUser(@RequestBody @Valid UpdateUserDTO updateUserDTO);

    @Operation(summary = "测试登录",
               description = "内部测试：跳过图形验证码校验，仅限开发/测试环境使用")
    @PostMapping("/v1/internal/user/testLogin")
    TokenDTO testLogin(@RequestBody @Valid UserLoginDTO userLoginDTO);

    @Operation(summary = "获取在线用户列表",
               description = "查询当前登录状态未过期的管理端用户")
    @GetMapping("/v1/auth/web/user/onlineUsers")
    List<UserDTO> onlineUsers();

    @Operation(summary = "退出登录",
               description = "将当前 token 加入 Redis 黑名单")
    @PostMapping("/v1/auth/web/user/logout")
    void logout();
}

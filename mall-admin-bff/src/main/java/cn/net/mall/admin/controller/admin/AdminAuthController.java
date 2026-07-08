package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.client.UserFeignClient;
import cn.net.mall.admin.dto.*;
import cn.net.mall.admin.client.*;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台认证 BFF 控制器
 * 
* 聚合登录 + 用户信息 + 权限菜单，返回前端真正需要的完整会话数据
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/auth")
@RequiredArgsConstructor
@Tag(name = "管理后台-认证", description = "登录、用户信息、权限菜单聚合接口")
public class AdminAuthController {

    private final UserFeignClient userFeignClient;

    @Operation(summary = "登录", description = "账号密码登录，返回 token + 用户信息 + 菜单权限")
    @PostMapping("/login")
    public TokenDTO login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return userFeignClient.login(userLoginDTO);
    }

    @Operation(summary = "手机号登录", description = "手机号验证码登录")
    @PostMapping("/loginByPhone")
    public TokenDTO loginByPhone(@Valid @RequestBody UserPhoneLoginDTO userPhoneLoginDTO) {
        return userFeignClient.loginByPhone(userPhoneLoginDTO);
    }

    @Operation(summary = "获取验证码", description = "获取图形验证码")
    @GetMapping("/getCode")
    public CaptchaDTO getCode() {
        return userFeignClient.getCode();
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的完整信息，包含角色权限")
    @GetMapping("/userInfo")
    public UserInfoDTO getUserInfo() {
        return userFeignClient.getUserInfo();
    }

    @Operation(summary = "获取用户详情", description = "获取当前登录用户详情")
    @GetMapping("/userDetail")
    public UserDTO getUserDetail() {
        return userFeignClient.getUserDetail();
    }

    @Operation(summary = "获取菜单树", description = "获取当前用户有权限的菜单树")
    @GetMapping("/menus")
    public ApiResult<Object> getMenus() {
        return ApiResultUtil.success();
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public ApiResult<Void> logout() {
        return ApiResultUtil.success();
    }

    @Operation(summary = "重置密码")
    @PostMapping("/resetPassword")
    public boolean resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return userFeignClient.resetPassword(resetPasswordDTO);
    }

    @Operation(summary = "更新用户头像",
               description = "更新当前登录用户的头像地址")
    @PostMapping("/updateAvatar")
    public ApiResult<Void> updateAvatar(@Valid @RequestBody UserAvatarDTO userAvatarDTO) {
        userFeignClient.updateAvatar(userAvatarDTO);
        return ApiResult.success();
    }

    @Operation(summary = "更新用户信息",
               description = "更新当前登录用户的基本信息")
    @PostMapping("/updateUser")
    public ApiResult<Void> updateUser(@Valid @RequestBody UpdateUserDTO updateUserDTO) {
        userFeignClient.updateUser(updateUserDTO);
        return ApiResult.success();
    }

    @Operation(summary = "测试登录（跳过验证码）",
               description = "内部测试：跳过图形验证码，直接使用账号密码登录获取token。仅限开发/测试环境使用。")
    @PostMapping("/testLogin")
    public TokenDTO testLogin(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return userFeignClient.testLogin(userLoginDTO);
    }

    @Operation(summary = "获取在线用户列表",
               description = "查询当前登录状态未过期的管理端用户")
    @GetMapping("/onlineUsers")
    public List<UserDTO> onlineUsers() {
        return userFeignClient.onlineUsers();
    }
}

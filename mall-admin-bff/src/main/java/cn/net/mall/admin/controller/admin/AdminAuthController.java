package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.client.*;
import cn.net.mall.admin.dto.*;
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
@Tag(name = "认证", description = "登录退出、用户信息、权限菜单")
public class AdminAuthController {

    private final UserFeignClient userFeignClient;
    private final MenuFeignClient menuFeignClient;

    @Operation(summary = "登录", description = "账号密码登录，返回 token + 用户信息 + 菜单权限")
    @PostMapping("/login")
    public ApiResult<TokenDTO> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return ApiResultUtil.success(userFeignClient.login(userLoginDTO));
    }

    @Operation(summary = "获取验证码", description = "获取图形验证码")
    @GetMapping("/getCode")
    public ApiResult<CaptchaDTO> getCode() {
        return ApiResultUtil.success(userFeignClient.getCode());
    }

    @Operation(summary = "获取当前用户信息",
               description = "获取当前登录用户的完整信息（含角色权限）。" +
                       "无需请求参数，根据当前 token 识别用户身份。",
               security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/userInfo")
    public ApiResult<UserInfoDTO> getUserInfo() {
        return ApiResultUtil.success(userFeignClient.getUserInfo());
    }

    @Operation(summary = "获取用户详情",
               description = "获取当前登录用户详情。" +
                       "无需请求参数，根据当前 token 识别用户身份。",
               security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/userDetail")
    public ApiResult<UserDTO> getUserDetail() {
        return ApiResultUtil.success(userFeignClient.getUserDetail());
    }

    @Operation(summary = "获取菜单树", description = "获取当前用户有权限的菜单树，决定前端侧边栏渲染内容")
    @GetMapping("/menus")
    public ApiResult<Object> getMenus() {
        return ApiResultUtil.success(menuFeignClient.getMenuTree());
    }

    @Operation(summary = "测试登录（跳过验证码）",
               description = "内部测试：跳过图形验证码，直接使用账号密码登录获取token。仅限开发/测试环境使用。")
    @PostMapping("/testLogin")
    public ApiResult<TokenDTO> testLogin(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return ApiResultUtil.success(userFeignClient.testLogin(userLoginDTO));
    }

    @Operation(summary = "获取在线用户列表",
               description = "查询当前登录状态未过期的管理端用户。" +
                       "无需请求参数，根据当前 token 鉴权（仅管理员可查看）。",
               security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/onlineUsers")
    public ApiResult<List<UserDTO>> onlineUsers() {
        return ApiResultUtil.success(userFeignClient.onlineUsers());
    }
}

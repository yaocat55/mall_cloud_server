package cn.net.mall.admin.controller.internal;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.admin.dto.*;
import cn.net.mall.admin.dto.auth.MenuTreeDTO;
import cn.net.mall.admin.entity.auth.CaptchaEntity;
import cn.net.mall.admin.service.auth.MenuService;
import cn.net.mall.admin.service.auth.UserService;
import cn.net.mall.entity.auth.JwtUserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Web用户 内部接口层（供 Feign 调用）
 *
 * 镜像 WebUserController 的方法，路径改为 /v1/internal/，返回裸 DTO（避免被 GlobalApiResultHandler 二次包装）
 */
@Tag(name = "内部服务-Web用户")
@RestController
@RequestMapping("/v1/internal/auth/web/user")
@Validated
public class WebUserInternalController {

    private final UserService userService;
    private final MenuService menuService;

    public WebUserInternalController(UserService userService, MenuService menuService) {
        this.userService = userService;
        this.menuService = menuService;
    }

    @Operation(summary = "获取图形验证码", description = "内部服务：获取图形验证码（base64图片）")
    @GetMapping("/getCode")
    public CaptchaDTO getCode() {
        CaptchaEntity captchaEntity = userService.getCode();
        if (Objects.nonNull(captchaEntity)) {
            return BeanUtil.copyProperties(captchaEntity, CaptchaDTO.class);
        }
        return new CaptchaDTO();
    }

    @Operation(summary = "获取当前登录的用户详情", description = "内部服务：获取当前登录的用户详情")
    @GetMapping("/getUserDetail")
    public UserDTO getUserDetail() {
        return userService.getUserDetail();
    }

    @Operation(summary = "用户登录", description = "内部服务：用户账号密码登录")
    @PostMapping("/login")
    public TokenDTO login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO);
    }

    @Operation(summary = "用户手机号登录", description = "内部服务：用户手机号登录")
    @PostMapping("/loginByPhone")
    public TokenDTO loginByPhone(@Valid @RequestBody UserPhoneLoginDTO userPhoneLoginDTO) {
        return userService.loginByPhone(userPhoneLoginDTO);
    }

    @Operation(summary = "用户退出登录", description = "内部服务：用户退出登录")
    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        userService.logout(request);
    }

    @Operation(summary = "获取用户信息", description = "内部服务：获取用户基本信息")
    @GetMapping("/info")
    public UserInfoDTO getUserInfo() {
        JwtUserEntity userInfo = userService.getUserInfo();
        if (Objects.nonNull(userInfo)) {
            return BeanUtil.toBean(userInfo, UserInfoDTO.class);
        }
        return null;
    }

    @Operation(summary = "获取当前用户菜单树", description = "内部服务：根据当前用户的角色返回可见的菜单树")
    @GetMapping("/menus")
    public List<MenuTreeDTO> getCurrentUserMenus() {
        return menuService.getCurrentUserMenuTree();
    }

    @Operation(summary = "重置密码", description = "内部服务：重置密码")
    @PostMapping("/resetPassword")
    public boolean resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return userService.resetPassword(resetPasswordDTO);
    }

    @Operation(summary = "更新用户信息", description = "内部服务：更新用户基本信息")
    @PostMapping("/updateUser")
    public void updateUser(@RequestBody @Valid UpdateUserDTO updateUserDTO) {
        userService.updateUser(updateUserDTO);
    }

    @Operation(summary = "获取在线用户列表", description = "内部服务：查询当前登录状态未过期的管理端用户")
    @GetMapping("/onlineUsers")
    public List<UserDTO> onlineUsers() {
        return userService.getOnlineUsers();
    }
}

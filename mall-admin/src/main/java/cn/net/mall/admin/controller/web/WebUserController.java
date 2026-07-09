package cn.net.mall.admin.controller.web;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.admin.dto.*;
import cn.net.mall.admin.dto.auth.MenuTreeDTO;
import cn.net.mall.admin.service.auth.MenuService;
import cn.net.mall.admin.entity.auth.web.UserPhoneLoginWebEntity;
import cn.net.mall.admin.entity.auth.web.UserWebEntity;
import cn.net.mall.admin.service.auth.UserService;
import cn.net.mall.admin.entity.auth.AuthUserEntity;
import cn.net.mall.admin.entity.auth.CaptchaEntity;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.admin.entity.auth.TokenEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


/**
 * 给前端用的用户接口
 *
 * @date 2024/1/9 下午4:58
 */
@Tag(name = "Web 用户", description = "管理后台：Web端用户登录、信息管理")
@RestController
@RequestMapping("/v1/auth/web/user")
@Validated
public class WebUserController {

    private final UserService userService;
    private final MenuService menuService;

    public WebUserController(UserService userService, MenuService menuService) {
        this.userService = userService;
        this.menuService = menuService;
    }

    @Operation(summary = "获取图形验证码", description = "获取图形验证码（base64图片）")
    @GetMapping(value = "/getCode")
    public CaptchaDTO getCode() {
        CaptchaEntity captchaEntity = userService.getCode();
        if (Objects.nonNull(captchaEntity)) {
            return BeanUtil.copyProperties(captchaEntity, CaptchaDTO.class);
        }
        return new CaptchaDTO();
    }

    /**
     * 获取当前登录的用户详情
     *
     * @return 用户详情
     */
    @Operation(summary = "获取当前登录的用户详情", description = "获取当前登录的用户详情")
    @GetMapping(value = "/getUserDetail")
    public UserDTO getUserDetail() {
        return userService.getUserDetail();
    }


    /**
     * 用户手机号登录
     *
     * @param userLoginDTO 用户实体
     * @return 影响行数
     */
    @Operation(summary = "用户登录", description = "用户登录")
    @PostMapping("/login")
    public TokenDTO login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO);
    }

    /**
     * 用户手机号登录
     *
     * @param userPhoneLoginDTO 用户实体
     * @return 影响行数
     */
    @Operation(summary = "用户手机号登录", description = "用户手机号登录")
    @PostMapping("/loginByPhone")
    public TokenDTO loginByPhone(@Valid @RequestBody UserPhoneLoginDTO userPhoneLoginDTO) {
        return userService.loginByPhone(userPhoneLoginDTO);
    }

    /**
     * 用户退出登录
     *
     * @param request 请求
     * @return 影响行数
     */
    @Operation(summary = "用户退出登录", description = "用户退出登录")
    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        userService.logout(request);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @Operation(summary = "获取用户信息", description = "获取用户信息")
    @GetMapping(value = "/info")
    public UserInfoDTO getUserInfo() {
        JwtUserEntity userInfo = userService.getUserInfo();
        if (Objects.nonNull(userInfo)) {
            return BeanUtil.toBean(userInfo, UserInfoDTO.class);
        }
        return null;
    }

    @Operation(summary = "获取当前用户菜单树", description = "根据当前用户的角色返回可见的菜单树（前端渲染侧边栏用）")
    @GetMapping("/menus")
    public List<MenuTreeDTO> getCurrentUserMenus() {
        return menuService.getCurrentUserMenuTree();
    }


    /**
     * 重置密码
     */
    @Operation(summary = "重置密码", description = "重置密码")
    @PostMapping("/resetPassword")
    public boolean resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return userService.resetPassword(resetPasswordDTO);
    }

    /**
     * 更新用户信息
     *
     * @param updateUserDTO
     */
    @Operation(summary = "更新用户信息")
    @PostMapping("/updateUser")
    public void updateUser(@RequestBody @Valid UpdateUserDTO updateUserDTO) {
        userService.updateUser(updateUserDTO);
    }

    @Operation(summary = "获取在线用户列表",
               description = "查询当前登录状态未过期的管理端用户")
    @GetMapping("/onlineUsers")
    public List<UserDTO> onlineUsers() {
        return userService.getOnlineUsers();
    }
}

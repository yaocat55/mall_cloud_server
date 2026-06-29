package cn.net.mall.auth.controller.web;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.auth.dto.*;
import cn.net.mall.auth.entity.auth.web.UserPhoneLoginWebEntity;
import cn.net.mall.auth.entity.auth.web.UserWebEntity;
import cn.net.mall.auth.service.auth.UserService;
import cn.net.mall.annotation.NoLogin;
import cn.net.mall.auth.entity.auth.AuthUserEntity;
import cn.net.mall.auth.entity.auth.CaptchaEntity;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.auth.entity.auth.TokenEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


/**
 * 给前端用的用户接口
 *
 * @date 2024/1/9 下午4:58
 */
@Tag(name = "web用户操作", description = "web用户接口")
@RestController
@RequestMapping("/v1/web/user")
@Validated
public class WebUserController {

    private final UserService userService;

    public WebUserController(UserService userService) {
        this.userService = userService;
    }

    @NoLogin
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
    @NoLogin
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
    @NoLogin
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
    @NoLogin
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
    @NoLogin
    @Operation(summary = "获取用户信息", description = "获取用户信息")
    @GetMapping(value = "/info")
    public UserInfoDTO getUserInfo() {
        JwtUserEntity userInfo = userService.getUserInfo();
        if (Objects.nonNull(userInfo)) {
            return BeanUtil.toBean(userInfo, UserInfoDTO.class);
        }
        return null;
    }


    /**
     * 重置密码
     */
    @NoLogin
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
}

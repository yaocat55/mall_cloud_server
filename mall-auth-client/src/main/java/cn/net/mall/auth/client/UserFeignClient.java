package cn.net.mall.auth.client;

import cn.net.mall.annotation.NoLogin;
import cn.net.mall.auth.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static cn.net.mall.auth.constant.AppConstant.AUTH_SERVICE_NAME;


@FeignClient(value = AUTH_SERVICE_NAME)
public interface UserFeignClient {

    /**
     * 通过id集合批量查询用户信息
     *
     * @param ids 用户ID
     * @return 用户信息
     */
    @Operation(summary = "通过id查询用户信息", description = "通过id查询用户信息")
    @GetMapping("/v1/user/findByIds")
    List<UserDTO> findByIds(@RequestBody List<Long> ids);


    /**
     * 通过手机号查询用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @Operation(summary = "通过手机号查询用户信息", description = "通过手机号查询用户信息")
    @GetMapping("/v1/user/findByPhone")
    UserDTO findByPhone(@RequestParam String phone);

    /**
     * 获取动态验证码
     *
     * @return 验证码
     */
    @Operation(summary = "获取动态验证码", description = "获取动态验证码")
    @GetMapping(value = "/v1/web/user/code")
    CaptchaDTO getCode();

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/v1/mobile/user/register")
    UserDTO register(@RequestBody @Validated RegisterDTO registerDTO);

    /**
     * 用户手机号登录
     *
     * @param userLoginDTO 用户实体
     * @return 影响行数
     */
    @NoLogin
    @Operation(summary = "用户登录", description = "用户登录")
    @PostMapping("/v1/web/user/login")
    TokenDTO login(@Valid @RequestBody UserLoginDTO userLoginDTO);


    /**
     * 用户手机号登录
     *
     * @param userPhoneLoginDTO 用户实体
     * @return 影响行数
     */
    @NoLogin
    @Operation(summary = "用户手机号登录", description = "用户手机号登录")
    @PostMapping("/v1/web/user/loginByPhone")
    TokenDTO loginByPhone(@Valid @RequestBody UserPhoneLoginDTO userPhoneLoginDTO);

    /**
     * 重置密码
     */
    @Operation(summary = "重置密码", description = "重置密码")
    @PostMapping("/v1/web/user/resetPassword")
    boolean resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO);

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @Operation(summary = "获取用户信息", description = "获取用户信息")
    @GetMapping(value = "/v1/web/user/info")
    UserInfoDTO getUserInfo();

    /**
     * 获取当前登录的用户详情
     *
     * @return 用户信息
     */
    @Operation(summary = "获取当前登录的用户详情", description = "获取当前登录的用户详情")
    @GetMapping(value = "/v1/web/user/getUserDetail")
    UserDTO getUserDetail();

    /**
     * 更新用户头像
     *
     * @param userAvatarDTO 用户头像实体
     * @return 用户头像地址
     */
    @Operation(summary = "更新用户头像", description = "更新用户头像")
    @PostMapping("/v1/user/updateAvatar")
    void updateAvatar(@RequestBody @Valid UserAvatarDTO userAvatarDTO);

    /**
     * 绑定手机号
     */
    @Operation(summary = "绑定手机号", description = "绑定手机号")
    @PostMapping("/v1/mobile/user/bindPhone")
    void bindPhone(@RequestBody @Valid BindPhoneDTO bindPhoneDTO);

    /**
     * 更新用户信息
     *
     * @param updateUserDTO
     */
    @Operation(summary = "更新用户信息")
    @PostMapping("/v1/web/user/updateUser")
    void updateUser(@RequestBody @Valid UpdateUserDTO updateUserDTO);
}

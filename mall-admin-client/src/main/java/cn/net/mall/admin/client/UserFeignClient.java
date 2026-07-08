package cn.net.mall.admin.client;

import cn.net.mall.admin.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
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
 * 
*   - mall-admin-api（管理端 BFF）
 *   - mall-order（订单服务）
 * 
* **不对外暴露**，仅限服务间 Feign 调用
 */
@FeignClient(value = ADMIN_SERVICE_NAME)
public interface UserFeignClient {

    /**
     * 通过id集合批量查询用户信息
     *
     * @param ids 用户ID
     * @return 用户信息
     */
    @Operation(summary = "批量查询用户信息",
               description = "内部服务：由 mall-admin-api 通过 Feign 调用，根据ID集合批量查询用户信息")
    @PostMapping("/v1/internal/user/findByIds")
    List<UserDTO> findByIds(@RequestBody List<Long> ids);

    /**
     * 通过手机号查询用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @Operation(summary = "通过手机号查询用户信息",
               description = "内部服务：由 mall-admin-api 通过 Feign 调用，根据手机号精确查询用户")
    @GetMapping("/v1/internal/user/findByPhone")
    UserDTO findByPhone(@RequestParam String phone);

    /**
     * 获取动态验证码
     *
     * @return 验证码
     */
    @Operation(summary = "获取动态验证码",
               description = "内部服务：获取图形验证码（base64图片），由 mall-admin-api 调用")
    @GetMapping(value = "/v1/web/user/getCode")
    CaptchaDTO getCode();

    /**
     * 用户注册（创建管理员账号）
     */
    @Operation(summary = "用户注册",
               description = "内部服务：创建管理员账号，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/web/user/register")
    UserDTO register(@RequestBody @Validated RegisterDTO registerDTO);

    /**
     * 用户手机号登录
     *
     * @param userLoginDTO 用户实体
     * @return 影响行数
     */
    @Operation(summary = "用户登录",
               description = "Web端：用户账号密码登录，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/web/user/login")
    TokenDTO login(@Valid @RequestBody UserLoginDTO userLoginDTO);

    /**
     * 用户手机号登录
     *
     * @param userPhoneLoginDTO 用户实体
     * @return 影响行数
     */
    @Operation(summary = "用户手机号登录",
               description = "Web端：用户手机号+验证码登录，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/web/user/loginByPhone")
    TokenDTO loginByPhone(@Valid @RequestBody UserPhoneLoginDTO userPhoneLoginDTO);

    /**
     * 重置密码
     */
    @Operation(summary = "重置密码",
               description = "Web端：用户重置密码，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/web/user/resetPassword")
    boolean resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO);

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @Operation(summary = "获取用户信息",
               description = "内部服务：获取当前用户基本信息，由 mall-admin-api 通过 Feign 调用")
    @GetMapping(value = "/v1/web/user/info")
    UserInfoDTO getUserInfo();

    /**
     * 获取当前登录的用户详情
     *
     * @return 用户信息
     */
    @Operation(summary = "获取当前登录的用户详情",
               description = "内部服务：获取当前登录用户完整详情，由 mall-admin-api 通过 Feign 调用")
    @GetMapping(value = "/v1/web/user/getUserDetail")
    UserDTO getUserDetail();

    /**
     * 更新用户头像
     *
     * @param userAvatarDTO 用户头像实体
     * @return 用户头像地址
     */
    @Operation(summary = "更新用户头像",
               description = "内部服务：由 mall-admin-api 通过 Feign 调用，更新用户头像地址")
    @PostMapping("/v1/internal/user/updateAvatar")
    void updateAvatar(@RequestBody @Valid UserAvatarDTO userAvatarDTO);

    /**
     * 绑定手机号
     */
    @Operation(summary = "绑定手机号",
               description = "内部服务：管理员绑定手机号，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/web/user/bindPhone")
    void bindPhone(@RequestBody @Valid BindPhoneDTO bindPhoneDTO);

    /**
     * 更新用户信息
     *
     * @param updateUserDTO
     */
    @Operation(summary = "更新用户信息",
               description = "内部服务：更新用户基本信息，由 mall-admin-api 通过 Feign 调用")
    @PostMapping("/v1/web/user/updateUser")
    void updateUser(@RequestBody @Valid UpdateUserDTO updateUserDTO);

    /**
     * 测试登录（跳过验证码）
     *
     * 仅限开发/测试环境使用，生产环境请走 login() 正常流程
     */
    @Operation(summary = "测试登录",
               description = "内部测试：跳过图形验证码校验，仅限开发/测试环境使用")
    @PostMapping("/v1/internal/user/testLogin")
    TokenDTO testLogin(@RequestBody @Valid UserLoginDTO userLoginDTO);

    /**
     * 获取在线用户列表
     */
    @Operation(summary = "获取在线用户列表",
               description = "查询当前登录状态未过期的管理端用户")
    @GetMapping("/v1/web/user/onlineUsers")
    List<UserDTO> onlineUsers();
}

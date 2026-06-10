package cn.net.mall.auth.controller.mobile;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import cn.net.mall.annotation.NoLogin;
import cn.net.mall.auth.dto.BindPhoneDTO;
import cn.net.mall.auth.dto.CaptchaDTO;
import cn.net.mall.auth.dto.RegisterDTO;
import cn.net.mall.auth.dto.UserDTO;
import cn.net.mall.auth.service.auth.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 移动端用户相关接口
 *
 * @date 2025/5/15 21:18
 */
@Tag(name = "移动端用户相关接口", description = "移动端用户相关接口")
@AllArgsConstructor
@RestController
@RequestMapping("/v1/mobile/user")
@Validated
public class MobileUserController {

    private final UserService userService;

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @NoLogin
    @Operation(summary = "用户注册", description = "用户注册")
    @PostMapping("/register")
    public UserDTO register(@RequestBody @Validated RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    /**
     * 绑定手机号
     */
    @PostMapping("/bindPhone")
    public void bindPhone(@RequestBody @Valid BindPhoneDTO bindPhoneDTO) {
        userService.bindPhone(bindPhoneDTO);
    }

    @Operation(summary = "注销账号", description = "注销账号")
    @PostMapping("/cancelAccount")
    public void cancelAccount(HttpServletRequest request) {
        userService.cancelAccount(request);
    }
}

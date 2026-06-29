package cn.net.mall.auth.controller.internal;

import cn.net.mall.auth.dto.UserAvatarDTO;
import cn.net.mall.auth.dto.UserDTO;
import cn.net.mall.auth.entity.auth.UserEntity;
import cn.net.mall.auth.service.auth.UserService;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 内部用户接口
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-admin-api（管理端 BFF）— 用户登录、信息查询</li>
 *   <li>mall-order（订单服务）— 查询用户信息</li>
 * </ul>
 * <b>不对外暴露</b>，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-用户", description = "内部微服务：mall-admin-api、mall-order 通过 Feign 调用")
@RestController
@RequestMapping("/v1/user")
public class UserInternalController {

    private final UserService userService;

    public UserInternalController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "批量查询用户信息",
               description = "内部服务：由 mall-admin-api 通过 Feign 调用，根据ID集合批量查询用户信息")
    @PostMapping("/findByIds")
    public List<UserDTO> findByIds(@RequestBody List<Long> ids) {
        return userService.findByIds(ids);
    }

    @Operation(summary = "通过手机号查询用户信息",
               description = "内部服务：由 mall-admin-api 通过 Feign 调用，根据手机号精确查询用户")
    @GetMapping("/findByPhone")
    public UserDTO findByPhone(@RequestParam String phone) {
        UserEntity entity = userService.findByIdPhone(phone);
        if (Objects.nonNull(entity)) {
            return BeanUtil.toBean(entity, UserDTO.class);
        }
        return null;
    }

    @Operation(summary = "更新用户头像",
               description = "内部服务：由 mall-admin-api 通过 Feign 调用，更新用户头像地址")
    @PostMapping("/updateAvatar")
    public void updateAvatar(@RequestBody @Valid UserAvatarDTO dto) {
        userService.updateAvatar(dto);
    }
}

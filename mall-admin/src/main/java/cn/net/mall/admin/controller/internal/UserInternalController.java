package cn.net.mall.admin.controller.internal;

import cn.net.mall.admin.dto.UserAvatarDTO;
import cn.net.mall.admin.dto.UserDTO;
import cn.net.mall.admin.entity.auth.UserEntity;
import cn.net.mall.admin.service.auth.UserService;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 内部用户接口
 * 
* **调用方：**
 * 
*   - mall-admin-api（管理端 BFF）— 用户登录、信息查询
 *   - mall-order（订单服务）— 查询用户信息
 * 
* **不对外暴露**，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-用户", description = "内部微服务：mall-admin-api、mall-order 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/user")
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

package cn.net.mall.admin.controller.internal;

import cn.net.mall.admin.dto.RowsDTO;
import cn.net.mall.admin.dto.TokenDTO;
import cn.net.mall.admin.dto.UserAvatarDTO;
import cn.net.mall.admin.dto.UserDTO;
import cn.net.mall.admin.dto.UserLoginDTO;
import cn.net.mall.admin.entity.auth.UserConditionEntity;
import cn.net.mall.admin.entity.auth.UserEntity;
import cn.net.mall.admin.service.auth.UserService;
import cn.net.mall.entity.ResponsePageEntity;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 用户 内部接口层（供 Feign 调用）
 *
 * @date 2024-01-08 17:18:18
 */
@Validated
@Slf4j
@Tag(name = "内部服务-用户")
@RestController
@RequestMapping("/v1/internal/auth/user")
public class UserInternalController {

    private final UserService userService;

    public UserInternalController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "通过id查询用户信息", description = "内部服务：根据用户ID查询用户信息")
    @GetMapping("/findById")
    public UserEntity findById(Long id) {
        UserEntity userEntity = userService.findById(id);
        log.info("userEntity:{}", userEntity);
        return userEntity;
    }

    @Operation(summary = "分页查询用户列表", description = "内部服务：按条件分页查询用户列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<UserEntity> searchByPage(@RequestBody UserConditionEntity userConditionEntity) {
        return userService.searchByPage(userConditionEntity);
    }

    @Operation(summary = "添加用户", description = "内部服务：创建新用户")
    @PostMapping("/insert")
    public RowsDTO insert(@RequestBody UserEntity userEntity) {
        userService.insert(userEntity);
        return new RowsDTO(1);
    }

    @Operation(summary = "修改用户", description = "内部服务：更新用户信息")
    @PostMapping("/update")
    public RowsDTO update(@RequestBody UserEntity userEntity) {
        return new RowsDTO(userService.update(userEntity));
    }

    @Operation(summary = "删除用户", description = "内部服务：批量删除用户")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteById(@RequestBody @NotNull List<Long> ids) {
        return new RowsDTO(userService.deleteByIds(ids));
    }

    @Operation(summary = "重置密码", description = "内部服务：批量重置用户密码")
    @PostMapping("/resetPwd")
    public RowsDTO resetPwd(@RequestBody @NotNull List<Long> ids) {
        return new RowsDTO(userService.resetPwd(ids));
    }

    @Operation(summary = "今日新增用户数", description = "内部服务：查询今日注册且未删除的用户总数")
    @GetMapping("/todayCount")
    public int todayCount() {
        return userService.todayCount();
    }

    @Operation(summary = "批量查询用户信息", description = "内部服务：根据ID集合批量查询用户信息")
    @PostMapping("/findByIds")
    public List<UserDTO> findByIds(@RequestBody List<Long> ids) {
        return userService.findByIds(ids);
    }

    @Operation(summary = "通过手机号查询用户信息", description = "内部服务：根据手机号精确查询用户")
    @GetMapping("/findByPhone")
    public UserDTO findByPhone(@RequestParam String phone) {
        UserEntity entity = userService.findByIdPhone(phone);
        if (Objects.nonNull(entity)) {
            return BeanUtil.toBean(entity, UserDTO.class);
        }
        return null;
    }

    @Operation(summary = "更新用户头像", description = "内部服务：更新用户头像地址")
    @PostMapping("/updateAvatar")
    public RowsDTO updateAvatar(@RequestBody @Valid UserAvatarDTO dto) {
        userService.updateAvatar(dto);
        return new RowsDTO(1);
    }

    @Operation(summary = "测试登录（跳过验证码）", description = "内部服务：跳过图形验证码，仅限开发/测试环境使用")
    @PostMapping("/testLogin")
    public TokenDTO testLogin(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.testLogin(userLoginDTO);
    }
}

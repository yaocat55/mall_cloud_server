package cn.net.mall.admin.controller.auth;

import cn.net.mall.admin.dto.TokenDTO;
import cn.net.mall.admin.dto.UserAvatarDTO;
import cn.net.mall.admin.dto.UserDTO;
import cn.net.mall.admin.dto.UserLoginDTO;
import cn.net.mall.admin.entity.auth.UserConditionEntity;
import cn.net.mall.admin.entity.auth.UserEntity;
import cn.net.mall.admin.service.auth.UserService;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.util.FillUserUtil;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * 用户 接口层
 *
 * @date 2024-01-08 17:18:18
 */
@Validated
@Slf4j
@Tag(name = "用户管理", description = "管理后台：用户 CRUD、密码重置。除 testLogin 外均需 Bearer Token + admin 角色")
@RestController
@RequestMapping("/v1/auth/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 通过id查询用户信息
     *
     * @param id 系统ID
     * @return 用户信息
     */
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "通过id查询用户信息", description = "需 Bearer Token + admin 角色 | 查询参数：id（用户ID）")
    @GetMapping("/findById")
    public UserEntity findById(Long id) {
        UserEntity userEntity = userService.findById(id);
        log.info("userEntity:{}", userEntity);
        return userEntity;
    }

    /**
     * 根据条件查询用户列表
     *
     * @param userConditionEntity 条件
     * @return 用户列表
     */
    @Operation(summary = "分页查询用户列表", description = "需 Bearer Token + admin 角色 | 请求体：UserConditionEntity（分页条件，含 page/pageSize）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<UserEntity> searchByPage(@RequestBody UserConditionEntity userConditionEntity) {
        return userService.searchByPage(userConditionEntity);
    }


    /**
     * 添加用户
     *
     * @param userEntity 用户实体
     * @return 影响行数
     */
    @Operation(summary = "添加用户", description = "需 Bearer Token + admin 角色 | 请求体：UserEntity（用户信息，含 userName/password/phone 等）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/insert")
    public void insert(@RequestBody UserEntity userEntity) {
        userService.insert(userEntity);
    }

    /**
     * 修改用户
     *
     * @param userEntity 用户实体
     * @return 影响行数
     */
    @Operation(summary = "修改用户", description = "需 Bearer Token + admin 角色 | 请求体：UserEntity（待修改的完整用户信息，含 id）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/update")
    public int update(@RequestBody UserEntity userEntity) {
        return userService.update(userEntity);
    }

    /**
     * 删除用户
     *
     * @param ids 用户ID
     * @return 影响行数
     */
    @Operation(summary = "删除用户", description = "需 Bearer Token + admin 角色 | 请求体：ids（用户ID列表）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/deleteByIds")
    public int deleteById(@RequestBody @NotNull List<Long> ids) {
        return userService.deleteByIds(ids);
    }


    /**
     * 重置密码
     *
     * @param ids 用户ID
     * @return 影响行数
     */
    @Operation(summary = "重置密码", description = "需 Bearer Token + admin 角色 | 请求体：ids（用户ID列表），密码重置为默认值")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/resetPwd")
    public int resetPwd(@RequestBody @NotNull List<Long> ids) {
        return userService.resetPwd(ids);
    }

    // ========== 以下方法原在 UserInternalController，合并到此处 ==========

    @Operation(summary = "批量查询用户信息",
               description = "需 Bearer Token + admin 角色 | 请求体：ids（用户ID列表）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/findByIds")
    public List<UserDTO> findByIds(@RequestBody List<Long> ids) {
        return userService.findByIds(ids);
    }

    @Operation(summary = "通过手机号查询用户信息",
               description = "需 Bearer Token + admin 角色 | 查询参数：phone（手机号）")
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/findByPhone")
    public UserDTO findByPhone(@RequestParam String phone) {
        UserEntity entity = userService.findByIdPhone(phone);
        if (Objects.nonNull(entity)) {
            return BeanUtil.toBean(entity, UserDTO.class);
        }
        return null;
    }

    @Operation(summary = "更新用户头像",
               description = "需 Bearer Token + admin 角色 | 请求体：UserAvatarDTO（用户ID + 头像URL）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/updateAvatar")
    public void updateAvatar(@RequestBody @Valid UserAvatarDTO dto) {
        userService.updateAvatar(dto);
    }

    @Operation(summary = "测试登录（跳过验证码）",
               description = "无需认证（公开接口，仅开发/测试环境使用）| 请求体：UserLoginDTO（username + password）")
    @PostMapping("/testLogin")
    public TokenDTO testLogin(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.testLogin(userLoginDTO);
    }
}

package cn.net.mall.admin.controller.auth;

import cn.net.mall.admin.entity.auth.UserConditionEntity;
import cn.net.mall.admin.entity.auth.UserEntity;
import cn.net.mall.admin.service.auth.UserService;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.util.FillUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户 接口层
 *
 * @date 2024-01-08 17:18:18
 */
@Validated
@Slf4j
@Tag(name = "用户管理", description = "管理后台：用户 CRUD、密码重置")
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
    @Operation(summary = "通过id查询用户信息", description = "通过id查询用户信息")
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
    @Operation(summary = "根据条件查询用户列表", description = "根据条件查询用户列表")
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
    @Operation(summary = "添加用户", description = "添加用户")
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
    @Operation(summary = "修改用户", description = "修改用户")
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
    @Operation(summary = "删除用户", description = "删除用户")
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
    @Operation(summary = "重置密码", description = "重置密码")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/resetPwd")
    public int resetPwd(@RequestBody @NotNull List<Long> ids) {
        return userService.resetPwd(ids);
    }

}

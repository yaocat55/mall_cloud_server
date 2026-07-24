package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.client.UserFeignClient;
import cn.net.mall.admin.dto.*;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 管理后台用户管理 BFF 控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/user")
@RequiredArgsConstructor
@Tag(name = "系统管理", description = "用户管理、角色、部门、岗位查询")
public class AdminUserController {

    private final UserFeignClient userFeignClient;

    // ==================== 用户列表（聚合） ====================

    @Operation(summary = "分页查询用户列表")
    @PostMapping("/page")
    public ResponsePageEntity<?> userPage(@RequestBody Map<String, Object> condition) {
        return userFeignClient.searchByPage(condition);
    }

    // ==================== 用户信息 ====================

    @Operation(summary = "通过ID列表查询用户信息（传 [id] 查单个，传 [id1,id2] 批量）")
    @PostMapping("/findByIds")
    public List<UserDTO> findByIds(@RequestBody List<Long> ids) {
        return userFeignClient.findByIds(ids);
    }

    @Operation(summary = "通过手机号查询用户信息")
    @GetMapping("/findByPhone")
    public UserDTO findByPhone(@RequestParam String phone) {
        return userFeignClient.findByPhone(phone);
    }
}

package cn.net.mall.auth.controller.internal;

import cn.net.mall.auth.dto.RoleDTO;
import cn.net.mall.auth.entity.auth.RoleEntity;
import cn.net.mall.auth.service.auth.RoleService;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 内部角色接口
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-admin-api（管理端 BFF）— 角色数据查询</li>
 * </ul>
 * <b>不对外暴露</b>，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-角色", description = "内部微服务：mall-admin-api 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/role")
public class RoleInternalController {

    private final RoleService roleService;

    public RoleInternalController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "查询所有角色",
               description = "内部服务：由 mall-admin-api 通过 Feign 调用，获取所有角色")
    @GetMapping("/all")
    public List<RoleDTO> all() {
        List<RoleEntity> entities = roleService.all();
        return entities.stream()
                .map(entity -> BeanUtil.toBean(entity, RoleDTO.class))
                .collect(Collectors.toList());
    }
}

package cn.net.mall.auth.client;

import cn.net.mall.auth.dto.RoleDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static cn.net.mall.auth.constant.AppConstant.AUTH_SERVICE_NAME;

/**
 * 角色 Feign 客户端
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-admin-api（管理端 BFF）</li>
 * </ul>
 * <b>不对外暴露</b>，仅限服务间 Feign 调用
 */
@FeignClient(value = AUTH_SERVICE_NAME, contextId = "roleFeignClient")
public interface RoleFeignClient {

    /**
     * 查询所有角色
     *
     * @return 所有角色列表
     */
    @Operation(summary = "查询所有角色",
               description = "内部Feign调用：获取所有角色，由 mall-admin-api 调用")
    @GetMapping("/v1/internal/role/all")
    List<RoleDTO> getAll();
}

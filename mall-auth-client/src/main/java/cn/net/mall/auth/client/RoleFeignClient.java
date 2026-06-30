package cn.net.mall.auth.client;

import cn.net.mall.auth.dto.RoleDTO;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import static cn.net.mall.auth.constant.AppConstant.AUTH_SERVICE_NAME;

@FeignClient(value = AUTH_SERVICE_NAME, contextId = "roleFeignClient")
public interface RoleFeignClient {
    @Operation(summary = "分页查询角色")
    @PostMapping("/v1/role/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map<String, Object> condition);
    @Operation(summary = "查询所有角色")
    @GetMapping("/v1/role/all")
    List<RoleDTO> all();
    @Operation(summary = "新增角色")
    @PostMapping("/v1/role/insert")
    int insert(@RequestBody Object entity);
    @Operation(summary = "修改角色")
    @PostMapping("/v1/role/update")
    int update(@RequestBody Object entity);
    @Operation(summary = "删除角色")
    @PostMapping("/v1/role/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
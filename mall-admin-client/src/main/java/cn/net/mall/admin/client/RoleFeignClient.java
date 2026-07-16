package cn.net.mall.admin.client;

import cn.net.mall.admin.dto.RowsDTO;
import cn.net.mall.admin.dto.RoleDTO;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import static cn.net.mall.admin.constant.AppConstant.ADMIN_SERVICE_NAME;

@FeignClient(value = ADMIN_SERVICE_NAME, contextId = "roleFeignClient")
public interface RoleFeignClient {
    @Operation(summary = "分页查询角色")
    @PostMapping("/v1/auth/role/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map condition);
    @Operation(summary = "查询所有角色")
    @GetMapping("/v1/auth/role/all")
    List all();
    @Operation(summary = "新增角色")
    @PostMapping("/v1/auth/role/insert")
    RowsDTO insert(@RequestBody Object entity);
    @Operation(summary = "修改角色")
    @PostMapping("/v1/auth/role/update")
    RowsDTO update(@RequestBody Object entity);
    @Operation(summary = "删除角色")
    @PostMapping("/v1/auth/role/deleteByIds")
    RowsDTO deleteByIds(@RequestBody @NotNull List ids);
}
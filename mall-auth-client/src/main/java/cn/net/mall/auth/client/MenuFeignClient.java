package cn.net.mall.auth.client;

import cn.net.mall.auth.dto.MenuDTO;
import cn.net.mall.auth.dto.auth.MenuTreeDTO;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import static cn.net.mall.auth.constant.AppConstant.AUTH_SERVICE_NAME;

@FeignClient(value = AUTH_SERVICE_NAME, contextId = "menuFeignClient")
public interface MenuFeignClient {
    @Operation(summary = "分页查询菜单")
    @PostMapping("/v1/menu/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map<String, Object> condition);
    @Operation(summary = "获取菜单树")
    @GetMapping("/v1/menu/getMenuTree")
    List<MenuTreeDTO> getMenuTree();
    @Operation(summary = "查询菜单列表")
    @PostMapping("/v1/menu/getMenu")
    List<MenuTreeDTO> getMenu(@RequestBody Map<String, Object> condition);
    @Operation(summary = "新增菜单")
    @PostMapping("/v1/menu/insert")
    int insert(@RequestBody Object entity);
    @Operation(summary = "修改菜单")
    @PostMapping("/v1/menu/update")
    int update(@RequestBody Object entity);
    @Operation(summary = "删除菜单")
    @PostMapping("/v1/menu/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
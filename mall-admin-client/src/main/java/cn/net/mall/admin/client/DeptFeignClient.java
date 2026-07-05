package cn.net.mall.admin.client;

import cn.net.mall.admin.dto.auth.DeptTreeDTO;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import static cn.net.mall.admin.constant.AppConstant.ADMIN_SERVICE_NAME;

@FeignClient(value = ADMIN_SERVICE_NAME, contextId = "deptFeignClient")
public interface DeptFeignClient {
    @Operation(summary = "分页查询部门")
    @PostMapping("/v1/dept/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map condition);
    @Operation(summary = "查询部门树")
    @PostMapping("/v1/dept/searchByTree")
    List searchByTree(@RequestBody Map params);
    @Operation(summary = "新增部门")
    @PostMapping("/v1/dept/insert")
    int insert(@RequestBody Object entity);
    @Operation(summary = "修改部门")
    @PostMapping("/v1/dept/update")
    int update(@RequestBody Object entity);
    @Operation(summary = "删除部门")
    @PostMapping("/v1/dept/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List ids);
}
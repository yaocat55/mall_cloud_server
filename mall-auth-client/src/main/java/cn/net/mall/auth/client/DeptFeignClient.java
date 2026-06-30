package cn.net.mall.auth.client;

import cn.net.mall.auth.dto.auth.DeptTreeDTO;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import static cn.net.mall.auth.constant.AppConstant.AUTH_SERVICE_NAME;

@FeignClient(value = AUTH_SERVICE_NAME, contextId = "deptFeignClient")
public interface DeptFeignClient {
    @Operation(summary = "分页查询部门")
    @PostMapping("/v1/dept/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map<String, Object> condition);
    @Operation(summary = "查询部门树")
    @PostMapping("/v1/dept/searchByTree")
    List<DeptTreeDTO> searchByTree(@RequestBody Map<String, Object> params);
    @Operation(summary = "新增部门")
    @PostMapping("/v1/dept/insert")
    int insert(@RequestBody Object entity);
    @Operation(summary = "修改部门")
    @PostMapping("/v1/dept/update")
    int update(@RequestBody Object entity);
    @Operation(summary = "删除部门")
    @PostMapping("/v1/dept/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
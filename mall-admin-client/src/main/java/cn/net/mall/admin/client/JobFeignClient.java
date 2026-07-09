package cn.net.mall.admin.client;

import cn.net.mall.admin.dto.JobDTO;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import static cn.net.mall.admin.constant.AppConstant.ADMIN_SERVICE_NAME;

@FeignClient(value = ADMIN_SERVICE_NAME, contextId = "jobFeignClient")
public interface JobFeignClient {
    @Operation(summary = "分页查询岗位")
    @PostMapping("/v1/auth/job/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map condition);
    @Operation(summary = "查询所有岗位")
    @GetMapping("/v1/auth/job/all")
    List all();
    @Operation(summary = "新增岗位")
    @PostMapping("/v1/auth/job/insert")
    int insert(@RequestBody Object entity);
    @Operation(summary = "修改岗位")
    @PostMapping("/v1/auth/job/update")
    int update(@RequestBody Object entity);
    @Operation(summary = "删除岗位")
    @PostMapping("/v1/auth/job/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List ids);
}
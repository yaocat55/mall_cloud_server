package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.CommonJobLogConditionDTO;
import cn.net.mall.basic.dto.CommonJobLogDTO;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static cn.net.mall.basic.constant.AppConstant.BASIC_SERVICE_NAME;

/**
 * [Service] Feign 客户端
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-admin-api（管理后台）— 定时任务日志查询</li>
 * </ul>
 *
 * @date 2025/7/3
 */
@FeignClient(value = BASIC_SERVICE_NAME, contextId = "jobLogFeignClient")
public interface JobLogFeignClient {

    @Operation(summary = "通过id查询定时任务日志信息", description = "内部Feign调用：根据主键查询定时任务执行日志")
    @GetMapping("/v1/commonJobLog/findById")
    Object findById(@RequestParam("id") Long id);

    @Operation(summary = "分页查询定时任务日志（管理端）", description = "分页查询定时任务执行日志，支持按任务名称、执行状态、执行时间等条件筛选")
    @PostMapping("/v1/commonJobLog/searchByPage")
    ResponsePageEntity<CommonJobLogDTO> searchByPage(@RequestBody CommonJobLogConditionDTO condition);

    @Operation(summary = "新增定时任务日志（管理端）", description = "新增定时任务执行日志记录")
    @PostMapping("/v1/commonJobLog/insert")
    int insert(@RequestBody CommonJobLogDTO entity);

    @Operation(summary = "修改定时任务日志（管理端）", description = "修改定时任务执行日志记录")
    @PostMapping("/v1/commonJobLog/update")
    int update(@RequestBody CommonJobLogDTO entity);

    @Operation(summary = "删除定时任务日志（管理端）", description = "根据ID列表批量删除定时任务日志")
    @PostMapping("/v1/commonJobLog/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}

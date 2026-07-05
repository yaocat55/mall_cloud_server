package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.CommonJobConditionDTO;
import cn.net.mall.basic.dto.CommonJobDTO;
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
 * 
* **调用方：**
 * 
*   - mall-admin-api（管理后台）— 定时任务配置管理
 * 
*
 * @date 2025/7/3
 */
@FeignClient(value = BASIC_SERVICE_NAME, contextId = "basicJobFeignClient")
public interface JobFeignClient {

    @Operation(summary = "通过id查询定时任务信息", description = "内部Feign调用：根据主键查询定时任务")
    @GetMapping("/v1/commonJob/findById")
    Object findById(@RequestParam("id") Long id);

    @Operation(summary = "分页查询定时任务（管理端）", description = "分页查询定时任务列表，支持按任务名称、任务状态等条件筛选")
    @PostMapping("/v1/commonJob/searchByPage")
    ResponsePageEntity<CommonJobDTO> searchByPage(@RequestBody CommonJobConditionDTO condition);

    @Operation(summary = "新增定时任务（管理端）", description = "新增定时任务配置，包括任务名称、Cron表达式、调用目标等参数")
    @PostMapping("/v1/commonJob/insert")
    int insert(@RequestBody CommonJobDTO entity);

    @Operation(summary = "修改定时任务（管理端）", description = "修改定时任务配置，支持更新任务名称、Cron表达式、调用目标等参数")
    @PostMapping("/v1/commonJob/update")
    int update(@RequestBody CommonJobDTO entity);

    @Operation(summary = "删除定时任务（管理端）", description = "根据ID列表批量删除定时任务配置")
    @PostMapping("/v1/commonJob/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);

    @Operation(summary = "立即执行定时任务（管理端）", description = "立即触发一次定时任务执行，忽略Cron表达式设定的调度计划")
    @PostMapping("/v1/commonJob/runNow")
    void runNow(@RequestBody CommonJobDTO entity);

    @Operation(summary = "暂停定时任务（管理端）", description = "暂停指定定时任务，暂停后任务将不再按照Cron表达式执行")
    @PostMapping("/v1/commonJob/pause")
    void pause(@RequestBody CommonJobDTO entity);

    @Operation(summary = "恢复定时任务（管理端）", description = "恢复已暂停的定时任务，任务将重新按照Cron表达式执行")
    @PostMapping("/v1/commonJob/resume")
    void resume(@RequestBody CommonJobDTO entity);
}

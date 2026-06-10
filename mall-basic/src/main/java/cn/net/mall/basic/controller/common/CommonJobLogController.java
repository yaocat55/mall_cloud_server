package cn.net.mall.basic.controller.common;

import cn.net.mall.basic.entity.common.CommonJobLogConditionEntity;
import cn.net.mall.basic.entity.common.CommonJobLogEntity;
import cn.net.mall.basic.service.common.CommonJobLogService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务执行日志 接口层
 *
 * @date 2024-04-30 15:09:07
 */
@Tag(name = "定时任务执行日志操作", description = "定时任务执行日志接口")
@RestController
@RequestMapping("/v1/commonJobLog")
public class CommonJobLogController {

    private final CommonJobLogService commonJobLogService;

    public CommonJobLogController(CommonJobLogService commonJobLogService) {
        this.commonJobLogService = commonJobLogService;
    }

    /**
     * 通过id查询定时任务执行日志信息
     *
     * @param id 系统ID
     * @return 定时任务执行日志信息
     */
    @Operation(summary = "通过id查询定时任务执行日志信息", description = "通过id查询定时任务执行日志信息")
    @GetMapping("/findById")
    public CommonJobLogEntity findById(Long id) {
        return commonJobLogService.findById(id);
    }

    /**
     * 根据条件查询定时任务执行日志列表
     *
     * @param commonJobLogConditionEntity 条件
     * @return 定时任务执行日志列表
     */
    @Operation(summary = "根据条件查询定时任务执行日志列表", description = "根据条件查询定时任务执行日志列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CommonJobLogEntity> searchByPage(@RequestBody CommonJobLogConditionEntity commonJobLogConditionEntity) {
        return commonJobLogService.searchByPage(commonJobLogConditionEntity);
    }


    /**
     * 添加定时任务执行日志
     *
     * @param commonJobLogEntity 定时任务执行日志实体
     * @return 影响行数
     */
    @Operation(summary = "添加定时任务执行日志", description = "添加定时任务执行日志")
    @PostMapping("/insert")
    public int insert(@RequestBody CommonJobLogEntity commonJobLogEntity) {
        return commonJobLogService.insert(commonJobLogEntity);
    }

    /**
     * 修改定时任务执行日志
     *
     * @param commonJobLogEntity 定时任务执行日志实体
     * @return 影响行数
     */
    @Operation(summary = "修改定时任务执行日志", description = "修改定时任务执行日志")
    @PostMapping("/update")
    public int update(@RequestBody CommonJobLogEntity commonJobLogEntity) {
        return commonJobLogService.update(commonJobLogEntity);
    }

    /**
     * 批量删除定时任务执行日志
     *
     * @param ids 定时任务执行日志ID集合
     * @return 影响行数
     */
    @Operation(summary = "批量删除定时任务执行日志", description = "批量删除定时任务执行日志")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return commonJobLogService.deleteByIds(ids);
    }
}

package cn.net.mall.basic.controller.common;

import cn.net.mall.basic.entity.common.CommonJobConditionEntity;
import cn.net.mall.basic.entity.common.CommonJobEntity;
import cn.net.mall.basic.service.common.CommonJobService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务 接口层
 *
 * @date 2024-04-30 15:09:06
 */
@Tag(name = "定时任务管理", description = "管理后台：定时任务配置")
@RestController
@RequestMapping("/v1/commonJob")
public class CommonJobController {

    private final CommonJobService commonJobService;

    public CommonJobController(CommonJobService commonJobService) {
        this.commonJobService = commonJobService;
    }

    /**
     * 通过id查询定时任务信息
     *
     * @param id 系统ID
     * @return 定时任务信息
     */
    @Operation(summary = "通过id查询定时任务信息", description = "通过id查询定时任务信息")
    @GetMapping("/findById")
    public CommonJobEntity findById(Long id) {
        return commonJobService.findById(id);
    }

    /**
     * 根据条件查询定时任务列表
     *
     * @param commonJobConditionEntity 条件
     * @return 定时任务列表
     */
    @Operation(summary = "根据条件查询定时任务列表", description = "根据条件查询定时任务列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CommonJobEntity> searchByPage(@RequestBody CommonJobConditionEntity commonJobConditionEntity) {
        return commonJobService.searchByPage(commonJobConditionEntity);
    }


    /**
     * 添加定时任务
     *
     * @param commonJobEntity 定时任务实体
     * @return 影响行数
     */
    @Operation(summary = "添加定时任务", description = "添加定时任务")
    @PostMapping("/insert")
    public void insert(@RequestBody CommonJobEntity commonJobEntity) {
        commonJobService.insert(commonJobEntity);
    }

    /**
     * 修改定时任务
     *
     * @param commonJobEntity 定时任务实体
     * @return 影响行数
     */
    @Operation(summary = "修改定时任务", description = "修改定时任务")
    @PostMapping("/update")
    public int update(@RequestBody CommonJobEntity commonJobEntity) {
        return commonJobService.update(commonJobEntity);
    }

    /**
     * 批量删除定时任务
     *
     * @param ids 定时任务ID集合
     * @return 影响行数
     */
    @Operation(summary = "批量删除定时任务", description = "批量删除定时任务")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return commonJobService.deleteByIds(ids);
    }


    /**
     * 立即执行定时任务
     *
     * @param commonJobEntity 定时任务实体
     * @return 影响行数
     */
    @Operation(summary = "立即执行定时任务", description = "立即执行定时任务")
    @PostMapping("/runNow")
    public void runNow(@RequestBody CommonJobEntity commonJobEntity) {
        commonJobService.runNow(commonJobEntity);
    }

    /**
     * 暂停定时任务
     *
     * @param commonJobEntity 定时任务实体
     * @return 影响行数
     */
    @Operation(summary = "暂停定时任务", description = "暂停定时任务")
    @PostMapping("/pause")
    public void pause(@RequestBody CommonJobEntity commonJobEntity) {
        commonJobService.pause(commonJobEntity);
    }


    /**
     * 恢复定时任务
     *
     * @param commonJobEntity 定时任务实体
     * @return 影响行数
     */
    @Operation(summary = "恢复定时任务", description = "恢复定时任务")
    @PostMapping("/resume")
    public void resume(@RequestBody CommonJobEntity commonJobEntity) {
        commonJobService.resume(commonJobEntity);
    }
}

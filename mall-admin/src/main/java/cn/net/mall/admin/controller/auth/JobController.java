package cn.net.mall.admin.controller.auth;

import cn.net.mall.admin.entity.auth.JobConditionEntity;
import cn.net.mall.admin.entity.auth.JobEntity;
import cn.net.mall.admin.service.auth.JobService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位 接口层
 *
 * @date 2024-01-08 17:18:17
 */
@Tag(name = "岗位管理", description = "管理后台：岗位 CRUD")
@RestController
@RequestMapping("/v1/job")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * 通过id查询岗位信息
     *
     * @param id 系统ID
     * @return 岗位信息
     */
    @Operation(summary = "通过id查询岗位信息", description = "通过id查询岗位信息")
    @GetMapping("/findById")
    public JobEntity findById(Long id) {
        return jobService.findById(id);
    }

    /**
     * 根据条件查询岗位列表
     *
     * @param jobConditionEntity 条件
     * @return 岗位列表
     */
    @Operation(summary = "根据条件查询岗位列表", description = "根据条件查询岗位列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<JobEntity> searchByPage(@RequestBody JobConditionEntity jobConditionEntity) {
        return jobService.searchByPage(jobConditionEntity);
    }


    /**
     * 添加岗位
     *
     * @param jobEntity 岗位实体
     * @return 影响行数
     */
    @Operation(summary = "添加岗位", description = "添加岗位")
    @PostMapping("/insert")
    public int insert(@RequestBody JobEntity jobEntity) {
        return jobService.insert(jobEntity);
    }

    /**
     * 修改岗位
     *
     * @param jobEntity 岗位实体
     * @return 影响行数
     */
    @Operation(summary = "修改岗位", description = "修改岗位")
    @PostMapping("/update")
    public int update(@RequestBody JobEntity jobEntity) {
        return jobService.update(jobEntity);
    }

    /**
     * 删除岗位
     *
     * @param ids 岗位ID
     * @return 影响行数
     */
    @Operation(summary = "删除岗位", description = "删除岗位")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return jobService.deleteByIds(ids);
    }
}

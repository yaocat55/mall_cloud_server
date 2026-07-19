package cn.net.mall.admin.controller.internal;

import cn.net.mall.admin.dto.RowsDTO;
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
 * 岗位 内部接口层（供 Feign 调用）
 *
 * @date 2024-01-08 17:18:17
 */
@Tag(name = "内部服务-岗位")
@RestController
@RequestMapping("/v1/internal/auth/job")
public class JobInternalController {

    private final JobService jobService;

    public JobInternalController(JobService jobService) {
        this.jobService = jobService;
    }

    @Operation(summary = "通过id查询岗位信息", description = "内部服务：根据岗位ID查询岗位信息")
    @GetMapping("/findById")
    public JobEntity findById(Long id) {
        return jobService.findById(id);
    }

    @Operation(summary = "分页查询岗位列表", description = "内部服务：按条件分页查询岗位列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<JobEntity> searchByPage(@RequestBody JobConditionEntity jobConditionEntity) {
        return jobService.searchByPage(jobConditionEntity);
    }

    @Operation(summary = "查询所有岗位", description = "内部服务：获取全部岗位列表")
    @GetMapping("/all")
    public List<JobEntity> all() {
        JobConditionEntity condition = new JobConditionEntity();
        condition.setPageNo(0);
        ResponsePageEntity<JobEntity> pageResult = jobService.searchByPage(condition);
        return pageResult.getData();
    }

    @Operation(summary = "添加岗位", description = "内部服务：新增岗位")
    @PostMapping("/insert")
    public RowsDTO insert(@RequestBody JobEntity jobEntity) {
        return new RowsDTO(jobService.insert(jobEntity));
    }

    @Operation(summary = "修改岗位", description = "内部服务：修改岗位信息")
    @PostMapping("/update")
    public RowsDTO update(@RequestBody JobEntity jobEntity) {
        return new RowsDTO(jobService.update(jobEntity));
    }

    @Operation(summary = "删除岗位", description = "内部服务：批量删除岗位")
    @PostMapping("/deleteByIds")
    public RowsDTO deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return new RowsDTO(jobService.deleteByIds(ids));
    }
}

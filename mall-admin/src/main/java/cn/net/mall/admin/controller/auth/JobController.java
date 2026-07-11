package cn.net.mall.admin.controller.auth;

import cn.net.mall.admin.entity.auth.JobConditionEntity;
import cn.net.mall.admin.entity.auth.JobEntity;
import cn.net.mall.admin.service.auth.JobService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位 接口层
 *
 * @date 2024-01-08 17:18:17
 */
@Tag(name = "岗位管理", description = "管理后台：岗位 CRUD。searchByPage / deleteByIds 无需认证；其余需 Bearer Token；insert / update 额外需 admin 角色")
@RestController
@RequestMapping("/v1/auth/job")
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
    @Operation(summary = "通过id查询岗位信息", description = "需 Bearer Token | 查询参数：id（岗位ID）")
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
    @Operation(summary = "分页查询岗位列表", description = "无需认证 | 请求体：JobConditionEntity（分页条件，含 page/pageSize）")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<JobEntity> searchByPage(@RequestBody JobConditionEntity jobConditionEntity) {
        return jobService.searchByPage(jobConditionEntity);
    }


    @Operation(summary = "查询所有岗位", description = "需 Bearer Token | 无参，返回全部岗位列表")
    @GetMapping("/all")
    public List<JobEntity> all() {
        JobConditionEntity condition = new JobConditionEntity();
        condition.setPageNo(0);
        ResponsePageEntity<JobEntity> pageResult = jobService.searchByPage(condition);
        return pageResult.getData();
    }

    /**
     * 添加岗位
     *
     * @param jobEntity 岗位实体
     * @return 影响行数
     */
    @Operation(summary = "添加岗位", description = "需 Bearer Token + admin 角色 | 请求体：JobEntity（岗位信息，含 name/code/sort/status 等）")
    @PreAuthorize("hasRole('admin')")
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
    @Operation(summary = "修改岗位", description = "需 Bearer Token + admin 角色 | 请求体：JobEntity（待修改的完整岗位信息，含 id）")
    @PreAuthorize("hasRole('admin')")
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
    @Operation(summary = "删除岗位", description = "需 Bearer Token + admin 角色 | 请求体：ids（岗位ID列表）")
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return jobService.deleteByIds(ids);
    }
}

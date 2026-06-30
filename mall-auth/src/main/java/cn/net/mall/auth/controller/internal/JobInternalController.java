package cn.net.mall.auth.controller.internal;

import cn.net.mall.auth.dto.JobDTO;
import cn.net.mall.auth.entity.auth.JobConditionEntity;
import cn.net.mall.auth.entity.auth.JobEntity;
import cn.net.mall.auth.service.auth.JobService;
import cn.net.mall.entity.ResponsePageEntity;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 内部岗位接口
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-admin-api（管理端 BFF）— 岗位数据查询</li>
 * </ul>
 * <b>不对外暴露</b>，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-岗位", description = "内部微服务：mall-admin-api 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/job")
public class JobInternalController {

    private final JobService jobService;

    public JobInternalController(JobService jobService) {
        this.jobService = jobService;
    }

    @Operation(summary = "查询所有岗位",
               description = "内部服务：由 mall-admin-api 通过 Feign 调用，获取所有岗位")
    @GetMapping("/all")
    public List<JobDTO> all() {
        JobConditionEntity condition = new JobConditionEntity();
        condition.setPageNo(0);
        ResponsePageEntity<JobEntity> pageResult = jobService.searchByPage(condition);
        return pageResult.getData().stream()
                .map(entity -> BeanUtil.toBean(entity, JobDTO.class))
                .collect(Collectors.toList());
    }
}

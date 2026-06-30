package cn.net.mall.auth.client;

import cn.net.mall.auth.dto.JobDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static cn.net.mall.auth.constant.AppConstant.AUTH_SERVICE_NAME;

/**
 * 岗位 Feign 客户端
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-admin-api（管理端 BFF）</li>
 * </ul>
 * <b>不对外暴露</b>，仅限服务间 Feign 调用
 */
@FeignClient(value = AUTH_SERVICE_NAME, contextId = "jobFeignClient")
public interface JobFeignClient {

    /**
     * 查询所有岗位
     *
     * @return 所有岗位列表
     */
    @Operation(summary = "查询所有岗位",
               description = "内部Feign调用：获取所有岗位，由 mall-admin-api 调用")
    @GetMapping("/v1/internal/job/all")
    List<JobDTO> getAll();
}

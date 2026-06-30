package cn.net.mall.auth.client;

import cn.net.mall.auth.dto.auth.DeptTreeDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

import static cn.net.mall.auth.constant.AppConstant.AUTH_SERVICE_NAME;

/**
 * 部门 Feign 客户端
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-admin-api（管理端 BFF）</li>
 * </ul>
 * <b>不对外暴露</b>，仅限服务间 Feign 调用
 */
@FeignClient(value = AUTH_SERVICE_NAME, contextId = "deptFeignClient")
public interface DeptFeignClient {

    /**
     * 查询部门树
     *
     * @param params 查询条件（可为空，传空Map获取完整部门树）
     * @return 部门树列表
     */
    @Operation(summary = "查询部门树",
               description = "内部Feign调用：获取部门树列表，由 mall-admin-api 调用")
    @PostMapping("/v1/internal/dept/searchByTree")
    List<DeptTreeDTO> searchByTree(@RequestBody Map<String, Object> params);
}

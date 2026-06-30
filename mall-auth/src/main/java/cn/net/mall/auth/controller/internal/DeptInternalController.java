package cn.net.mall.auth.controller.internal;

import cn.net.mall.auth.dto.auth.DeptTreeDTO;
import cn.net.mall.auth.entity.auth.DeptConditionEntity;
import cn.net.mall.auth.service.auth.DeptService;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 内部部门接口
 * <p>
 * <b>调用方：</b>
 * <ul>
 *   <li>mall-admin-api（管理端 BFF）— 部门树数据查询</li>
 * </ul>
 * <b>不对外暴露</b>，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-部门", description = "内部微服务：mall-admin-api 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/dept")
public class DeptInternalController {

    private final DeptService deptService;

    public DeptInternalController(DeptService deptService) {
        this.deptService = deptService;
    }

    @Operation(summary = "查询部门树",
               description = "内部服务：由 mall-admin-api 通过 Feign 调用，获取部门树列表")
    @PostMapping("/searchByTree")
    public List<DeptTreeDTO> searchByTree(@RequestBody Map<String, Object> params) {
        DeptConditionEntity condition = new DeptConditionEntity();
        if (params != null && !params.isEmpty()) {
            BeanUtil.copyProperties(params, condition);
        }
        return deptService.searchByTree(condition);
    }
}

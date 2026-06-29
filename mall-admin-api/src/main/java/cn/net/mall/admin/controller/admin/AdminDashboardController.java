package cn.net.mall.admin.controller.admin;

import cn.net.mall.auth.client.UserFeignClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 管理后台仪表盘 BFF 控制器
 * <p>
 * 聚合各服务数据，生成仪表盘统计
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "管理后台-仪表盘", description = "仪表盘数据聚合接口")
public class AdminDashboardController {

    private final UserFeignClient userFeignClient;

    @Operation(summary = "获取仪表盘统计数据", description = "聚合用户数、订单数等统计数据")
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        // 仪表盘统计数据 - 由前端具体需求决定
        stats.put("status", "ok");
        return stats;
    }
}

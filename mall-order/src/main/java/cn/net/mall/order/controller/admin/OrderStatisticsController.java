package cn.net.mall.order.controller.admin;

import cn.net.mall.order.dto.OrderStatisticsDTO;
import cn.net.mall.order.service.admin.OrderStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单统计（管理端）.
 *
 * <p>供 admin-bff 调用，返回订单核心统计数据。</p>
 */
@Tag(name = "订单统计", description = "管理后台：订单统计")
@RestController
@RequestMapping("/v1/admin/trade")
@RequiredArgsConstructor
public class OrderStatisticsController {

    private final OrderStatisticsService orderStatisticsService;

    @Operation(summary = "订单统计", description = "返回总订单数、今日订单数、总销售额、今日销售额、各状态订单数量")
    @PostMapping("/statistics")
    public OrderStatisticsDTO statistics() {
        return orderStatisticsService.getOrderStatistics();
    }
}

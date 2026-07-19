package cn.net.mall.admin.controller.admin;

import cn.net.mall.admin.dto.DashboardDTO;
import cn.net.mall.admin.client.UserFeignClient;
import cn.net.mall.inventory.client.InventoryFeignClient;
import cn.net.mall.inventory.dto.InventoryDTO;
import cn.net.mall.order.client.OrderFeignClient;
import cn.net.mall.order.dto.OrderConditionDTO;
import cn.net.mall.order.dto.OrderReturnConditionDTO;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.ProductConditionDTO;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "工作台", description = "统计数据")
public class AdminDashboardController {

    private final UserFeignClient userFeignClient;
    private final OrderFeignClient orderFeignClient;
    private final ProductFeignClient productFeignClient;
    private final InventoryFeignClient inventoryFeignClient;

    @Operation(summary = "获取仪表盘统计数据", description = "聚合用户数、订单数等统计数据")
    @GetMapping("/stats")
    public ApiResult<DashboardDTO> getStats() {
        DashboardDTO dto = new DashboardDTO();

        // 商品总数
        try {
            ProductConditionDTO prodCond = new ProductConditionDTO();
            prodCond.setPageNo(1);
            prodCond.setPageSize(1);
            var productPage = productFeignClient.searchByPage(prodCond);
            if (productPage != null && productPage.getTotalCount() != null) dto.setProductCount(productPage.getTotalCount());
        } catch (Exception e) {
            log.warn("获取商品总数失败", e);
        }

        // 用户总数
        try {
            Map<String, Object> userCond = new HashMap<>();
            userCond.put("page", Map.of("pageNum", 1, "pageSize", 1));
            var userPage = userFeignClient.searchByPage(userCond);
            if (userPage != null) dto.setUserCount(userPage.getTotalCount());
        } catch (Exception e) {
            log.warn("获取用户总数失败", e);
        }

        // 今日新增用户数
        try {
            dto.setUserTodayCount(userFeignClient.todayCount());
        } catch (Exception e) {
            log.warn("获取今日新增用户数失败", e);
        }

        // 订单统计：总数、今日数、销售额、各状态数量
        try {
            var stats = orderFeignClient.statistics();
            if (stats != null) {
                dto.setOrderCount(stats.getOrderCount());
                dto.setOrderTodayCount(stats.getOrderTodayCount());
                dto.setTotalSalesAmount(stats.getTotalSalesAmount() != null ? stats.getTotalSalesAmount() : BigDecimal.ZERO);
                dto.setTodaySalesAmount(stats.getTodaySalesAmount() != null ? stats.getTodaySalesAmount() : BigDecimal.ZERO);
                dto.setOrderByStatus(Arrays.asList(
                    statusCount(1, "待付款", stats.getStatusWaitPay()),
                    statusCount(2, "已支付/待发货", stats.getStatusPaid()),
                    statusCount(3, "已发货", stats.getStatusShipped()),
                    statusCount(4, "已完成", stats.getStatusCompleted())
                ));
            }
        } catch (Exception e) {
            log.warn("获取订单统计数据失败", e);
        }

        // 最近订单
        try {
            OrderConditionDTO orderCond = new OrderConditionDTO();
            orderCond.setPageNo(1);
            orderCond.setPageSize(10);
            var orderPage = orderFeignClient.search(orderCond);
            if (orderPage != null && orderPage.getData() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dto.setRecentOrders(orderPage.getData().stream().map(o -> {
                    DashboardDTO.RecentOrderDTO r = new DashboardDTO.RecentOrderDTO();
                    r.setId(o.getId());
                    r.setCode(o.getCode());
                    r.setUserName(o.getUserName());
                    r.setTotalAmount(o.getTotalAmount());
                    r.setOrderStatus(o.getOrderStatus() != null ? o.getOrderStatus() : 0);
                    r.setOrderTime(o.getCreateTime() != null ? sdf.format(o.getCreateTime()) : "");
                    return r;
                }).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            log.warn("获取最近订单失败", e);
        }

        // 退货统计（全部）
        try {
            OrderReturnConditionDTO retCond = new OrderReturnConditionDTO();
            retCond.setPageNo(1);
            retCond.setPageSize(1);
            var returnPage = orderFeignClient.searchReturnByPage(retCond);
            if (returnPage != null) dto.setRefundCount(returnPage.getTotalCount());
        } catch (Exception e) {
            log.warn("获取退货总数失败", e);
        }

        // 待审核退货数
        try {
            OrderReturnConditionDTO retPendingCond = new OrderReturnConditionDTO();
            retPendingCond.setPageNo(1);
            retPendingCond.setPageSize(1);
            retPendingCond.setApplyStatus(1);
            var returnPendingPage = orderFeignClient.searchReturnByPage(retPendingCond);
            if (returnPendingPage != null) dto.setRefundPendingCount(returnPendingPage.getTotalCount());
        } catch (Exception e) {
            log.warn("获取待审核退货数失败", e);
        }

        // 销量排行 Top 5（从库存服务读取 sale_count）
        try {
            var topSalesList = productFeignClient.getTopSales(5);
            if (topSalesList != null) {
                dto.setTopProducts(topSalesList.stream().map(p -> {
                    DashboardDTO.TopProductDTO t = new DashboardDTO.TopProductDTO();
                    t.setId(p.getId());
                    t.setName(p.getName());
                    // 从库存服务获取实际销量
                    try {
                        InventoryDTO inv = inventoryFeignClient.getByProductId(p.getId());
                        t.setSaleCount(inv != null ? (inv.getSaleCount() != null ? inv.getSaleCount() : 0) : 0);
                    } catch (Exception e) {
                        t.setSaleCount(0);
                    }
                    t.setTotalAmount(BigDecimal.ZERO);
                    return t;
                }).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            log.warn("获取销量排行失败", e);
        }

        return ApiResultUtil.success(dto);
    }

    private DashboardDTO.OrderStatusCountDTO statusCount(int status, String name, int count) {
        DashboardDTO.OrderStatusCountDTO s = new DashboardDTO.OrderStatusCountDTO();
        s.setStatus(status);
        s.setName(name);
        s.setCount(count);
        return s;
    }
}

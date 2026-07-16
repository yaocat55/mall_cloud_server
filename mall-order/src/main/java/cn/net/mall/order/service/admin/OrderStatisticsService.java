package cn.net.mall.order.service.admin;

import cn.net.mall.order.dto.OrderStatisticsDTO;
import cn.net.mall.order.es.repository.OrderEsRepository;
import cn.net.mall.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单统计服务（管理端）.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatisticsService {

    private final OrderMapper orderMapper;
    private final OrderEsRepository orderEsRepository;

    /**
     * 获取订单统计数据.
     */
    public OrderStatisticsDTO getOrderStatistics() {
        OrderStatisticsDTO dto = new OrderStatisticsDTO();

        try {
            dto.setOrderCount((int) orderEsRepository.count());
        } catch (Exception e) {
            log.warn("从ES获取订单总数失败", e);
        }

        try {
            dto.setTotalSalesAmount(orderMapper.selectTotalSalesAmount());
            dto.setTodaySalesAmount(orderMapper.selectTodaySalesAmount());
        } catch (Exception e) {
            log.warn("获取销售额统计失败", e);
        }

        try {
            List<Map<String, Object>> statusCounts = orderMapper.selectOrderStatusGroupCount();
            for (Map<String, Object> row : statusCounts) {
                Integer status = (Integer) row.get("order_status");
                Number count = (Number) row.get("cnt");
                int cnt = count != null ? count.intValue() : 0;
                if (status != null) {
                    switch (status) {
                        case 1 -> dto.setStatusWaitPay(cnt);
                        case 2 -> dto.setStatusPaid(cnt);
                        case 3 -> dto.setStatusShipped(cnt);
                        case 4 -> dto.setStatusCompleted(cnt);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取订单状态统计失败", e);
        }

        return dto;
    }
}

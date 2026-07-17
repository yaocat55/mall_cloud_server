package cn.net.mall.order.service.admin;

import cn.net.mall.order.dto.OrderStatisticsDTO;
import cn.net.mall.order.es.document.OrderDocument;
import cn.net.mall.order.es.repository.OrderEsRepository;
import cn.net.mall.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
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
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 获取订单统计数据.
     */
    public OrderStatisticsDTO getOrderStatistics() {
        OrderStatisticsDTO dto = new OrderStatisticsDTO();

        // 订单总数（ES）
        try {
            dto.setOrderCount((int) orderEsRepository.count());
        } catch (Exception e) {
            log.warn("从ES获取订单总数失败", e);
        }

        // 总销售额 / 今日销售额（ES 全量查询后 Java 聚合）
        try {
            var query = NativeQuery.builder()
                .withQuery(q -> q.matchAll(m -> m))
                .build();
            SearchHits<OrderDocument> result = elasticsearchOperations.search(query, OrderDocument.class);
            BigDecimal total = BigDecimal.ZERO;
            BigDecimal today = BigDecimal.ZERO;
            java.time.LocalDate todayDate = java.time.LocalDate.now();
            for (var hit : result) {
                OrderDocument doc = hit.getContent();
                if (doc.getPaymentAmount() != null) {
                    total = total.add(doc.getPaymentAmount());
                    if (doc.getCreateTime() != null) {
                        java.time.LocalDate orderDate = new java.sql.Date(doc.getCreateTime().getTime()).toLocalDate();
                        if (orderDate.equals(todayDate)) {
                            today = today.add(doc.getPaymentAmount());
                        }
                    }
                }
            }
            dto.setTotalSalesAmount(total);
            dto.setTodaySalesAmount(today);
        } catch (Exception e) {
            log.warn("从ES获取销售额失败", e);
        }

        // 订单状态（MySQL兜底）
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

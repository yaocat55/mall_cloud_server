package cn.net.mall.order.es.repository;

import cn.net.mall.order.es.document.OrderDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 订单ES Repository
 */
public interface OrderEsRepository extends ElasticsearchRepository<OrderDocument, Long> {

    /**
     * 根据用户ID查询订单
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 订单分页列表
     */
    Page<OrderDocument> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID与订单状态查询
     * @param userId 用户ID
     * @param orderStatus 订单状态
     * @param pageable 分页参数
     * @return 订单分页列表
     */
    Page<OrderDocument> findByUserIdAndOrderStatus(Long userId, Integer orderStatus, Pageable pageable);

    /**
     * 根据用户ID与创建时间范围查询（开始<=OrderTime<结束）
     * @param userId 用户ID
     * @param begin 开始时间（含）
     * @param end 结束时间（不含）
     * @param pageable 分页参数
     * @return 订单分页列表
     */
    Page<OrderDocument> findByUserIdAndOrderTimeGreaterThanEqualAndOrderTimeLessThan(Long userId, java.util.Date begin, java.util.Date end, Pageable pageable);

    /**
     * 根据用户ID、订单状态与创建时间范围查询（开始<=OrderTime<结束）
     * @param userId 用户ID
     * @param orderStatus 订单状态
     * @param begin 开始时间（含）
     * @param end 结束时间（不含）
     * @param pageable 分页参数
     * @return 订单分页列表
     */
    Page<OrderDocument> findByUserIdAndOrderStatusAndOrderTimeGreaterThanEqualAndOrderTimeLessThan(Long userId, Integer orderStatus, java.util.Date begin, java.util.Date end, Pageable pageable);

    /**
     * 根据用户ID与创建开始时间查询（开始<=OrderTime）
     */
    Page<OrderDocument> findByUserIdAndOrderTimeGreaterThanEqual(Long userId, java.util.Date begin, Pageable pageable);

    /**
     * 根据用户ID与创建结束时间查询（OrderTime<结束）
     */
    Page<OrderDocument> findByUserIdAndOrderTimeLessThan(Long userId, java.util.Date end, Pageable pageable);

    /**
     * 根据用户ID、订单状态与创建开始时间查询（开始<=OrderTime）
     */
    Page<OrderDocument> findByUserIdAndOrderStatusAndOrderTimeGreaterThanEqual(Long userId, Integer orderStatus, java.util.Date begin, Pageable pageable);

    /**
     * 根据用户ID、订单状态与创建结束时间查询（OrderTime<结束）
     */
    Page<OrderDocument> findByUserIdAndOrderStatusAndOrderTimeLessThan(Long userId, Integer orderStatus, java.util.Date end, Pageable pageable);
}

package cn.net.mall.order.mapper;

import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.order.entity.OrderConditionEntity;
import cn.net.mall.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单 mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity, OrderConditionEntity> {
    /**
     * 查询订单信息
     *
     * @param id 订单ID
     * @return 订单信息
     */
    OrderEntity findById(Long id);

    /**
     * 添加订单
     *
     * @param orderEntity 订单信息
     * @return 结果
     */
    int insert(OrderEntity orderEntity);

    /**
     * 修改订单
     *
     * @param orderEntity 订单信息
     * @return 结果
     */
    int update(OrderEntity orderEntity);

    /**
     * 批量删除订单
     *
     * @param ids    id集合
     * @param entity 订单实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") OrderEntity entity);

    /**
     * 批量更新订单状态
     *
     * @param ids         id集合
     * @param entity      订单实体
     * @param orderStatus 订单状态
     * @return 结果
     */
    int updateOrderStatusByIds(@Param("ids") List<Long> ids, @Param("entity") OrderEntity entity, @Param("orderStatus") Integer orderStatus);

    /**
     * 批量查询订单信息
     *
     * @param ids ID集合
     * @return 订单信息
     */
    List<OrderEntity> findByIds(List<Long> ids);

    /**
     * 原子取消：仅当订单为已下单且待支付时取消
     *
     * @param id 订单ID
     * @param cancelStatus 取消后的状态值
     * @return 影响行数
     */
    int cancelIfUnpaid(@Param("id") Long id, @Param("cancelStatus") Integer cancelStatus);

    /**
     * 查询订单总数（is_del = 0）
     */
    int selectOrderCount();

    /**
     * 查询今日订单数（is_del = 0 且创建时间 >= 当天）
     */
    int selectOrderTodayCount();

    /**
     * 查询总销售额（SUM payment_amount）
     */
    BigDecimal selectTotalSalesAmount();

    /**
     * 查询今日销售额
     */
    BigDecimal selectTodaySalesAmount();

    /**
     * 按订单状态分组统计数量
     *
     * @return List<Map>，每个 Map 包含 order_status 和 cnt
     */
    List<Map<String, Object>> selectOrderStatusGroupCount();
}

package cn.net.mall.order.mapper;

import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.order.entity.OrderItemConditionEntity;
import cn.net.mall.order.entity.OrderItemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单明细 mapper
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItemEntity, OrderItemConditionEntity> {
    /**
     * 查询订单明细信息
     *
     * @param id 订单明细ID
     * @return 订单明细信息
     */
    OrderItemEntity findById(Long id);

    /**
     * 添加订单明细
     *
     * @param orderItemEntity 订单明细信息
     * @return 结果
     */
    int insert(OrderItemEntity orderItemEntity);

    /**
     * 批量添加订单明细
     *
     * @param list
     * @return
     */
    int batchInsert(List<OrderItemEntity> list);

    /**
     * 修改订单明细
     *
     * @param orderItemEntity 订单明细信息
     * @return 结果
     */
    int update(OrderItemEntity orderItemEntity);

    /**
     * 批量删除订单明细
     *
     * @param ids    id集合
     * @param entity 订单明细实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") OrderItemEntity entity);

    /**
     * 批量查询订单明细信息
     *
     * @param ids ID集合
     * @return 订单明细信息
     */
    List<OrderItemEntity> findByIds(List<Long> ids);

    /**
     * 统计商品的销量
     *
     * @param productId   商品ID
     * @param orderStatus 订单状态
     * @return 销量
     */
    OrderItemEntity statProductSaleQuantity(@Param("productId") Long productId, @Param("orderStatus") Integer orderStatus);
}

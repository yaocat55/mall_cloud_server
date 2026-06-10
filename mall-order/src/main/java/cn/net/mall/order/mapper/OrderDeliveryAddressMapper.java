package cn.net.mall.order.mapper;

import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.order.entity.OrderDeliveryAddressConditionEntity;
import cn.net.mall.order.entity.OrderDeliveryAddressEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单收货地址 mapper
 */
@Mapper
public interface OrderDeliveryAddressMapper extends BaseMapper<OrderDeliveryAddressEntity, OrderDeliveryAddressConditionEntity> {
	/**
     * 查询订单收货地址信息
     *
     * @param id 订单收货地址ID
     * @return 订单收货地址信息
     */
	OrderDeliveryAddressEntity findById(Long id);

	/**
     * 添加订单收货地址
     *
     * @param orderDeliveryAddressEntity 订单收货地址信息
     * @return 结果
     */
	int insert(OrderDeliveryAddressEntity orderDeliveryAddressEntity);

	/**
     * 修改订单收货地址
     *
     * @param orderDeliveryAddressEntity 订单收货地址信息
     * @return 结果
     */
	int update(OrderDeliveryAddressEntity orderDeliveryAddressEntity);

    /**
     * 批量删除订单收货地址
     *
     * @param ids id集合
     * @param entity 订单收货地址实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") OrderDeliveryAddressEntity entity);

    /**
     * 批量查询订单收货地址信息
     *
     * @param ids ID集合
     * @return 订单收货地址信息
    */
    List<OrderDeliveryAddressEntity> findByIds(List<Long> ids);
}

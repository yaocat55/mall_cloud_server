package cn.net.mall.order.service;

import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.order.entity.OrderDeliveryAddressConditionEntity;
import cn.net.mall.order.entity.OrderDeliveryAddressEntity;
import cn.net.mall.order.mapper.OrderDeliveryAddressMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单收货地址 服务层
 */
@Service
@RequiredArgsConstructor
public class OrderDeliveryAddressService extends BaseService<OrderDeliveryAddressEntity, OrderDeliveryAddressConditionEntity> {

    private final OrderDeliveryAddressMapper orderDeliveryAddressMapper;

    @Override
    protected BaseMapper<OrderDeliveryAddressEntity, OrderDeliveryAddressConditionEntity> getBaseMapper() {
        return orderDeliveryAddressMapper;
    }

    /**
     * 查询订单收货地址信息
     *
     * @param orderId 订单ID
     * @return 订单收货地址信息
     */
    public List<OrderDeliveryAddressEntity> findByOrderId(Long orderId) {
        OrderDeliveryAddressConditionEntity condition = new OrderDeliveryAddressConditionEntity();
        condition.setOrderId(orderId);
        return orderDeliveryAddressMapper.searchByCondition(condition);
    }

    /**
     * 查询订单收货地址信息
     *
     * @param id 订单收货地址ID
     * @return 订单收货地址信息
     */
    public OrderDeliveryAddressEntity findById(Long id) {
        return orderDeliveryAddressMapper.findById(id);
    }

    /**
     * 新增订单收货地址
     *
     * @param entity 订单收货地址信息
     * @return 结果
     */
    public int insert(OrderDeliveryAddressEntity entity) {
        return orderDeliveryAddressMapper.insert(entity);
    }

    /**
     * 修改订单收货地址
     *
     * @param entity 订单收货地址信息
     * @return 结果
     */
    public int update(OrderDeliveryAddressEntity entity) {
        return orderDeliveryAddressMapper.update(entity);
    }

    /**
     * 批量删除订单收货地址
     *
     * @param ids ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<OrderDeliveryAddressEntity> entities = orderDeliveryAddressMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "订单收货地址已被删除");

        OrderDeliveryAddressEntity entity = new OrderDeliveryAddressEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return orderDeliveryAddressMapper.deleteByIds(ids, entity);
    }
}

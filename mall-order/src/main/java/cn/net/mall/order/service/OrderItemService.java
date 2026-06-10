package cn.net.mall.order.service;

import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.order.entity.OrderItemConditionEntity;
import cn.net.mall.order.entity.OrderItemEntity;
import cn.net.mall.order.mapper.OrderItemMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 订单明细 服务层
 */
@Service
@RequiredArgsConstructor
public class OrderItemService extends BaseService<OrderItemEntity, OrderItemConditionEntity> {

    private final OrderItemMapper orderItemMapper;

    @Override
    protected BaseMapper<OrderItemEntity, OrderItemConditionEntity> getBaseMapper() {
        return orderItemMapper;
    }

    /**
     * 根据订单ID集合批量查询订单明细信息
     *
     * @param orderIdList 订单ID集合
     * @return 订单明细信息
     */
    public List<OrderItemEntity> findByOrderIdList(List<Long> orderIdList) {
        OrderItemConditionEntity condition = new OrderItemConditionEntity();
        condition.setOrderIdList(orderIdList);
        condition.setPageSize(0);
        return orderItemMapper.searchByCondition(condition);
    }

    /**
     * 查询订单明细信息
     *
     * @param id 订单明细ID
     * @return 订单明细信息
     */
    public OrderItemEntity findById(Long id) {
        return orderItemMapper.findById(id);
    }

    /**
     * 根据订单编码和订单明细ID查询订单明细
     *
     * @param code 订单编码
     * @param id   订单明细ID
     * @return 订单明细
     */
    public OrderItemEntity findByOrderCodeAndId(String code, Long id) {
        OrderItemConditionEntity condition = new OrderItemConditionEntity();
        condition.setOrderCode(code);
        condition.setId(id);
        List<OrderItemEntity> list = orderItemMapper.searchByCondition(condition);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 统计商品的销量
     *
     * @param productId   商品ID
     * @param orderStatus 订单状态
     * @return 销量
     */
    public OrderItemEntity statProductSaleQuantity(Long productId, Integer orderStatus) {
        return orderItemMapper.statProductSaleQuantity(productId, orderStatus);
    }

    /**
     * 新增订单明细
     *
     * @param entity 订单明细信息
     * @return 结果
     */
    public int insert(OrderItemEntity entity) {
        return orderItemMapper.insert(entity);
    }

    /**
     * 批量新增订单明细
     *
     * @param list 订单明细信息集合
     * @return 结果
     */
    public int batchInsert(List<OrderItemEntity> list) {
        return orderItemMapper.batchInsert(list);
    }

    /**
     * 修改订单明细
     *
     * @param entity 订单明细信息
     * @return 结果
     */
    public int update(OrderItemEntity entity) {
        return orderItemMapper.update(entity);
    }

    /**
     * 批量删除订单明细
     *
     * @param ids ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<OrderItemEntity> entities = orderItemMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "订单明细已被删除");

        OrderItemEntity entity = new OrderItemEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return orderItemMapper.deleteByIds(ids, entity);
    }
}

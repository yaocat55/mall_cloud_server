package cn.net.mall.order.mapper;

import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.order.entity.OrderReturnItemConditionEntity;
import cn.net.mall.order.entity.OrderReturnItemEntity;

public interface OrderReturnItemMapper extends BaseMapper<OrderReturnItemEntity, OrderReturnItemConditionEntity> {
    int insertBatch(java.util.List<OrderReturnItemEntity> list);
}


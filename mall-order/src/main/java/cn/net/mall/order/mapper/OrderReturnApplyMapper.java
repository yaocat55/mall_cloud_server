package cn.net.mall.order.mapper;

import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.order.entity.OrderReturnApplyConditionEntity;
import cn.net.mall.order.entity.OrderReturnApplyEntity;

public interface OrderReturnApplyMapper extends BaseMapper<OrderReturnApplyEntity, OrderReturnApplyConditionEntity> {
    int insert(OrderReturnApplyEntity entity);
    int update(OrderReturnApplyEntity entity);
}


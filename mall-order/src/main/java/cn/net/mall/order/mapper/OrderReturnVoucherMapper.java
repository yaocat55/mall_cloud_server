package cn.net.mall.order.mapper;

import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.order.entity.OrderReturnVoucherConditionEntity;
import cn.net.mall.order.entity.OrderReturnVoucherEntity;

public interface OrderReturnVoucherMapper extends BaseMapper<OrderReturnVoucherEntity, OrderReturnVoucherConditionEntity> {
    int insertBatch(java.util.List<OrderReturnVoucherEntity> list);
}


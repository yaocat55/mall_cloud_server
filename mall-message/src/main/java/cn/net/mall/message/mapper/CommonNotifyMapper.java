package cn.net.mall.message.mapper;

import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.message.entity.CommonNotifyConditionEntity;
import cn.net.mall.message.entity.CommonNotifyEntity;

public interface CommonNotifyMapper extends BaseMapper<CommonNotifyEntity, CommonNotifyConditionEntity> {
    CommonNotifyEntity findById(Long id);
    int insert(CommonNotifyEntity commonNotifyEntity);
    int update(CommonNotifyEntity commonNotifyEntity);
    int deleteById(Long id);
}

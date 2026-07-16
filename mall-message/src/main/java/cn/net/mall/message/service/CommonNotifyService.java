package cn.net.mall.message.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.message.entity.CommonNotifyConditionEntity;
import cn.net.mall.message.entity.CommonNotifyEntity;
import cn.net.mall.message.mapper.CommonNotifyMapper;
import cn.net.mall.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommonNotifyService extends BaseService<CommonNotifyEntity, CommonNotifyConditionEntity> {

    private final CommonNotifyMapper commonNotifyMapper;

    public CommonNotifyService(CommonNotifyMapper commonNotifyMapper) {
        this.commonNotifyMapper = commonNotifyMapper;
    }

    public CommonNotifyEntity findById(Long id) {
        return commonNotifyMapper.findById(id);
    }

    public ResponsePageEntity<CommonNotifyEntity> searchByPage(CommonNotifyConditionEntity commonNotifyConditionEntity) {
        try {
            return super.searchByPage(commonNotifyConditionEntity);
        } catch (Exception e) {
            log.error("searchByPage 异常", e);
            throw e;
        }
    }

    public int insert(CommonNotifyEntity commonNotifyEntity) {
        return commonNotifyMapper.insert(commonNotifyEntity);
    }

    public int update(CommonNotifyEntity commonNotifyEntity) {
        return commonNotifyMapper.update(commonNotifyEntity);
    }

    public int deleteById(Long id) {
        return commonNotifyMapper.deleteById(id);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return commonNotifyMapper;
    }
}

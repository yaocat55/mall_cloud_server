package cn.net.mall.basic.service.common;

import cn.net.mall.basic.entity.common.CommonJobLogConditionEntity;
import cn.net.mall.basic.entity.common.CommonJobLogEntity;
import cn.net.mall.basic.mapper.common.CommonJobLogMapper;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务执行日志 服务层
 *
 * @date 2024-04-30 15:09:07
 */
@Service
public class CommonJobLogService extends BaseService<CommonJobLogEntity, CommonJobLogConditionEntity> {

    private final CommonJobLogMapper commonJobLogMapper;

    public CommonJobLogService(CommonJobLogMapper commonJobLogMapper) {
        this.commonJobLogMapper = commonJobLogMapper;
    }

    /**
     * 查询定时任务执行日志信息
     *
     * @param id 定时任务执行日志ID
     * @return 定时任务执行日志信息
     */
    public CommonJobLogEntity findById(Long id) {
        return commonJobLogMapper.findById(id);
    }

    /**
     * 根据条件分页查询定时任务执行日志列表
     *
     * @param commonJobLogConditionEntity 定时任务执行日志信息
     * @return 定时任务执行日志集合
     */
    public ResponsePageEntity<CommonJobLogEntity> searchByPage(CommonJobLogConditionEntity commonJobLogConditionEntity) {
        return super.searchByPage(commonJobLogConditionEntity);
    }

    /**
     * 新增定时任务执行日志
     *
     * @param commonJobLogEntity 定时任务执行日志信息
     * @return 结果
     */
    public int insert(CommonJobLogEntity commonJobLogEntity) {
        return commonJobLogMapper.insert(commonJobLogEntity);
    }

    /**
     * 修改定时任务执行日志
     *
     * @param commonJobLogEntity 定时任务执行日志信息
     * @return 结果
     */
    public int update(CommonJobLogEntity commonJobLogEntity) {
        return commonJobLogMapper.update(commonJobLogEntity);
    }

    /**
     * 批量删除定时任务执行日志对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<CommonJobLogEntity> entities = commonJobLogMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "定时任务执行日志已被删除");

        CommonJobLogEntity entity = new CommonJobLogEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return commonJobLogMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return commonJobLogMapper;
    }

}

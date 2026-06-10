package cn.net.mall.basic.service.common;

import cn.net.mall.basic.entity.common.CommonJobConditionEntity;
import cn.net.mall.basic.entity.common.CommonJobEntity;
import cn.net.mall.basic.enums.CommonJobOperateTypeEnum;
import cn.net.mall.basic.helper.MqHelper;
import cn.net.mall.basic.mapper.common.CommonJobMapper;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 定时任务 服务层
 *
 * @date 2024-04-30 15:09:06
 */
@Slf4j
@Service
public class CommonJobService extends BaseService<CommonJobEntity, CommonJobConditionEntity> {

    private final CommonJobMapper commonJobMapper;
    private final MqHelper mqHelper;

    public CommonJobService(CommonJobMapper commonJobMapper, MqHelper mqHelper) {
        this.commonJobMapper = commonJobMapper;
        this.mqHelper = mqHelper;
    }

    @Value("${mall.mgt.commonJobTopic:COMMON_JOB_TOPIC}")
    private String commonJobTopic;

    /**
     * 查询定时任务信息
     *
     * @param id 定时任务ID
     * @return 定时任务信息
     */
    public CommonJobEntity findById(Long id) {
        return commonJobMapper.findById(id);
    }

    /**
     * 根据条件分页查询定时任务列表
     *
     * @param commonJobConditionEntity 定时任务信息
     * @return 定时任务集合
     */
    public ResponsePageEntity<CommonJobEntity> searchByPage(CommonJobConditionEntity commonJobConditionEntity) {
        return super.searchByPage(commonJobConditionEntity);
    }

    /**
     * 立即执行定时任务
     *
     * @param commonJobEntity 定时任务实体
     */
    public void runNow(CommonJobEntity commonJobEntity) {
        changeJob(commonJobEntity, CommonJobOperateTypeEnum.RUN_NOW);
    }

    /**
     * 恢复定时任务
     *
     * @param commonJobEntity 定时任务实体
     */
    public void resume(CommonJobEntity commonJobEntity) {
        CommonJobEntity jobEntity = checkChangeJobParam(commonJobEntity);
        jobEntity.setPauseStatus(false);
        FillUserUtil.fillUpdateUserInfo(jobEntity);
        commonJobMapper.update(jobEntity);

        jobEntity.setOperateTypeEnum(CommonJobOperateTypeEnum.RESUME);
        sendDynamicJobMessage(jobEntity);
    }

    /**
     * 暂停定时任务
     *
     * @param commonJobEntity 定时任务实体
     */
    public void pause(CommonJobEntity commonJobEntity) {
        CommonJobEntity jobEntity = checkChangeJobParam(commonJobEntity);
        jobEntity.setPauseStatus(true);
        FillUserUtil.fillUpdateUserInfo(jobEntity);
        commonJobMapper.update(jobEntity);

        jobEntity.setOperateTypeEnum(CommonJobOperateTypeEnum.PAUSE);
        sendDynamicJobMessage(jobEntity);
    }

    /**
     * 立即执行订单任务
     *
     * @param commonJobEntity 定时任务实体
     */
    private void changeJob(CommonJobEntity commonJobEntity, CommonJobOperateTypeEnum operateTypeEnum) {
        CommonJobEntity jobEntity = checkChangeJobParam(commonJobEntity);
        jobEntity.setOperateTypeEnum(operateTypeEnum);
        sendDynamicJobMessage(jobEntity);
    }

    private CommonJobEntity checkChangeJobParam(CommonJobEntity commonJobEntity) {
        AssertUtil.notNull(commonJobEntity.getId(), "id不能为空");
        CommonJobEntity jobEntity = commonJobMapper.findById(commonJobEntity.getId());
        AssertUtil.notNull(jobEntity, "当前job不存在");
        return jobEntity;
    }

    /**
     * 新增定时任务
     *
     * @param commonJobEntity 定时任务信息
     * @return 结果
     */
    public int insert(CommonJobEntity commonJobEntity) {
        checkParam(commonJobEntity);
        commonJobEntity.setPauseStatus(false);
        int insert = commonJobMapper.insert(commonJobEntity);
        commonJobEntity.setOperateTypeEnum(CommonJobOperateTypeEnum.NEW);
        sendDynamicJobMessage(commonJobEntity);
        return insert;
    }

    private void sendDynamicJobMessage(CommonJobEntity commonJobEntity) {
        mqHelper.send(commonJobTopic, commonJobEntity);
    }

    /**
     * 修改定时任务
     *
     * @param commonJobEntity 定时任务信息
     * @return 结果
     */
    public int update(CommonJobEntity commonJobEntity) {
        AssertUtil.notNull(commonJobEntity.getId(), "id不能为空");
        checkParam(commonJobEntity);
        int update = commonJobMapper.update(commonJobEntity);
        commonJobEntity.setOperateTypeEnum(CommonJobOperateTypeEnum.UPDATE);
        sendDynamicJobMessage(commonJobEntity);
        return update;
    }

    private void checkParam(CommonJobEntity commonJobEntity) {
        if (StringUtils.hasLength(commonJobEntity.getCronExpression())) {
            try {
                new CronExpression(commonJobEntity.getCronExpression());
            } catch (Exception e) {
                log.info("cron解析失败，原因：", e);
                throw new BusinessException("cron表达式错误");
            }
        }

        commonJobEntity.setBeanName(commonJobEntity.getBeanName().trim());

        CommonJobConditionEntity commonJobConditionEntity = new CommonJobConditionEntity();
        commonJobConditionEntity.setBeanName(commonJobEntity.getBeanName());
        List<CommonJobEntity> commonJobEntities = commonJobMapper.searchByCondition(commonJobConditionEntity);
        if (Objects.nonNull(commonJobEntity.getId())) {
            Optional<CommonJobEntity> optional = commonJobEntities.stream()
                    .filter(x -> !x.getId().equals(commonJobEntity.getId())).findAny();
            if (optional.isPresent()) {
                throw new BusinessException("该定时任务已存在，请重新修改");
            }
        } else {
            if (CollectionUtils.isNotEmpty(commonJobEntities)) {
                throw new BusinessException("该定时任务已存在，请勿重复添加");
            }
        }
    }

    /**
     * 批量删除定时任务对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<CommonJobEntity> entities = commonJobMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "定时任务已被删除");

        CommonJobEntity entity = new CommonJobEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        int delete = commonJobMapper.deleteByIds(ids, entity);

        for (CommonJobEntity commonJobEntity : entities) {
            commonJobEntity.setOperateTypeEnum(CommonJobOperateTypeEnum.DELETE);
            sendDynamicJobMessage(commonJobEntity);
        }
        return delete;
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return commonJobMapper;
    }

}

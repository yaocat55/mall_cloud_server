package cn.net.mall.basic.service.common;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.basic.dto.SmsRecordConditionDTO;
import cn.net.mall.basic.dto.SmsRecordDTO;
import cn.net.mall.basic.entity.common.CommonSmsRecordConditionEntity;
import cn.net.mall.basic.entity.common.CommonSmsRecordEntity;
import cn.net.mall.basic.mapper.common.CommonSmsRecordMapper;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Objects;

/**
 * 短信发送记录 服务层
 *
 * @date 2024-11-08 13:03:15
 */
@Service
public class CommonSmsRecordService extends BaseService<CommonSmsRecordEntity, CommonSmsRecordConditionEntity> {

    private final CommonSmsRecordMapper commonSmsRecordMapper;

    public CommonSmsRecordService(CommonSmsRecordMapper commonSmsRecordMapper) {
        this.commonSmsRecordMapper = commonSmsRecordMapper;
    }

    /**
     * 根据手机号和类型查询短信发送记录
     *
     * @param smsRecordConditionDTO 查询条件
     * @return 短信发送记录
     */
    public SmsRecordDTO findSmsRecord(SmsRecordConditionDTO smsRecordConditionDTO) {
        CommonSmsRecordEntity smsRecordEntity = commonSmsRecordMapper.findSmsRecord(smsRecordConditionDTO.getPhone(), smsRecordConditionDTO.getSmsTypeEnum().getValue());
        if (Objects.nonNull(smsRecordEntity)) {
            return BeanUtil.toBean(smsRecordEntity, SmsRecordDTO.class);
        }

        return null;
    }

    /**
     * 查询短信发送记录信息
     *
     * @param id 短信发送记录ID
     * @return 短信发送记录信息
     */
    public CommonSmsRecordEntity findById(Long id) {
        return commonSmsRecordMapper.findById(id);
    }

    /**
     * 根据条件分页查询短信发送记录列表
     *
     * @param commonSmsRecordConditionEntity 短信发送记录信息
     * @return 短信发送记录集合
     */
    public ResponsePageEntity<CommonSmsRecordEntity> searchByPage(CommonSmsRecordConditionEntity commonSmsRecordConditionEntity) {
        return super.searchByPage(commonSmsRecordConditionEntity);
    }

    /**
     * 新增短信发送记录
     *
     * @param commonSmsRecordEntity 短信发送记录信息
     * @return 结果
     */
    public int insert(CommonSmsRecordEntity commonSmsRecordEntity) {
        return commonSmsRecordMapper.insert(commonSmsRecordEntity);
    }

    /**
     * 修改短信发送记录
     *
     * @param commonSmsRecordEntity 短信发送记录信息
     * @return 结果
     */
    public int update(CommonSmsRecordEntity commonSmsRecordEntity) {
        return commonSmsRecordMapper.update(commonSmsRecordEntity);
    }

    /**
     * 批量删除短信发送记录
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<CommonSmsRecordEntity> entities = commonSmsRecordMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "短信发送记录已被删除");

        CommonSmsRecordEntity entity = new CommonSmsRecordEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return commonSmsRecordMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return commonSmsRecordMapper;
    }
}

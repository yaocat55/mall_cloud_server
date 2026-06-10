package cn.net.mall.basic.mapper.common;

import cn.net.mall.basic.entity.common.CommonSmsRecordConditionEntity;
import cn.net.mall.basic.entity.common.CommonSmsRecordEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 短信发送记录 mapper
 *
 * @date 2024-11-08 13:03:15
 */
public interface CommonSmsRecordMapper extends BaseMapper<CommonSmsRecordEntity, CommonSmsRecordConditionEntity> {
    /**
     * 查询短信发送记录信息
     *
     * @param id 短信发送记录ID
     * @return 短信发送记录信息
     */
    CommonSmsRecordEntity findById(Long id);

    /**
     * 添加短信发送记录
     *
     * @param commonSmsRecordEntity 短信发送记录信息
     * @return 结果
     */
    int insert(CommonSmsRecordEntity commonSmsRecordEntity);

    /**
     * 修改短信发送记录
     *
     * @param commonSmsRecordEntity 短信发送记录信息
     * @return 结果
     */
    int update(CommonSmsRecordEntity commonSmsRecordEntity);

    /**
     * 批量删除短信发送记录
     *
     * @param ids    id集合
     * @param entity 短信发送记录实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") CommonSmsRecordEntity entity);

    /**
     * 删除历史的短信发送记录
     *
     * @param phone 手机号
     * @param type  类型
     * @return 结果
     */
    int deleteByPhoneAndType(@Param("phone") String phone, @Param("type") Integer type);

    /**
     * 批量查询短信发送记录信息
     *
     * @param ids ID集合
     * @return 短信发送记录信息
     */
    List<CommonSmsRecordEntity> findByIds(List<Long> ids);


    /**
     * 根据手机号和类型查询短信发送记录
     *
     * @param phone 手机号
     * @param type  类型
     * @return 短信发送记录
     */
    CommonSmsRecordEntity findSmsRecord(@Param("phone") String phone,
                                        @Param("type") Integer type);
}

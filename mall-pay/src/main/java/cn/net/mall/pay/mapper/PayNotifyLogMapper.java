package cn.net.mall.pay.mapper;

import cn.net.mall.pay.entity.PayNotifyLogConditionEntity;
import cn.net.mall.pay.entity.PayNotifyLogEntity;
import cn.net.mall.pay.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 支付渠道回调日志 mapper
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
public interface PayNotifyLogMapper extends BaseMapper<PayNotifyLogEntity, PayNotifyLogConditionEntity> {
	/**
     * 查询支付渠道回调日志信息
     *
     * @param id 支付渠道回调日志ID
     * @return 支付渠道回调日志信息
     */
	PayNotifyLogEntity findById(Long id);

	/**
     * 添加支付渠道回调日志
     *
     * @param payNotifyLogEntity 支付渠道回调日志信息
     * @return 结果
     */
	int insert(PayNotifyLogEntity payNotifyLogEntity);

	/**
     * 修改支付渠道回调日志
     *
     * @param payNotifyLogEntity 支付渠道回调日志信息
     * @return 结果
     */
	int update(PayNotifyLogEntity payNotifyLogEntity);

	/**
     * 批量删除支付渠道回调日志
     *
     * @param ids id集合
     * @param entity 支付渠道回调日志实体
     * @return 结果
     */
	int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") PayNotifyLogEntity entity);

	/**
     * 批量查询支付渠道回调日志信息
     *
     * @param ids ID集合
     * @return 部门信息
    */
	List<PayNotifyLogEntity> findByIds(List<Long> ids);
}

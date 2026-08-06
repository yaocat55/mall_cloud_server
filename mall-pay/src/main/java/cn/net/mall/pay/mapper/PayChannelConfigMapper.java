package cn.net.mall.pay.mapper;

import cn.net.mall.pay.entity.PayChannelConfigConditionEntity;
import cn.net.mall.pay.entity.PayChannelConfigEntity;
import cn.net.mall.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 支付渠道配置 mapper
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
public interface PayChannelConfigMapper extends BaseMapper<PayChannelConfigEntity, PayChannelConfigConditionEntity> {
	/**
     * 查询支付渠道配置信息
     *
     * @param id 支付渠道配置ID
     * @return 支付渠道配置信息
     */
	PayChannelConfigEntity findById(Long id);

	/**
     * 添加支付渠道配置
     *
     * @param payChannelConfigEntity 支付渠道配置信息
     * @return 结果
     */
	int insert(PayChannelConfigEntity payChannelConfigEntity);

	/**
     * 修改支付渠道配置
     *
     * @param payChannelConfigEntity 支付渠道配置信息
     * @return 结果
     */
	int update(PayChannelConfigEntity payChannelConfigEntity);

	/**
     * 批量删除支付渠道配置
     *
     * @param ids id集合
     * @param entity 支付渠道配置实体
     * @return 结果
     */
	int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") PayChannelConfigEntity entity);

	/**
     * 批量查询支付渠道配置信息
     *
     * @param ids ID集合
     * @return 部门信息
    */
	List<PayChannelConfigEntity> findByIds(List<Long> ids);
}

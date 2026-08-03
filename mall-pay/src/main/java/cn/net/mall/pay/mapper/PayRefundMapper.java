package cn.net.mall.pay.mapper;

import cn.net.mall.pay.entity.PayRefundConditionEntity;
import cn.net.mall.pay.entity.PayRefundEntity;
import cn.net.mall.pay.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 支付退款单 mapper
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
public interface PayRefundMapper extends BaseMapper<PayRefundEntity, PayRefundConditionEntity> {
	/**
     * 查询支付退款单信息
     *
     * @param id 支付退款单ID
     * @return 支付退款单信息
     */
	PayRefundEntity findById(Long id);

	/**
     * 添加支付退款单
     *
     * @param payRefundEntity 支付退款单信息
     * @return 结果
     */
	int insert(PayRefundEntity payRefundEntity);

	/**
     * 修改支付退款单
     *
     * @param payRefundEntity 支付退款单信息
     * @return 结果
     */
	int update(PayRefundEntity payRefundEntity);

	/**
     * 批量删除支付退款单
     *
     * @param ids id集合
     * @param entity 支付退款单实体
     * @return 结果
     */
	int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") PayRefundEntity entity);

	/**
     * 批量查询支付退款单信息
     *
     * @param ids ID集合
     * @return 部门信息
    */
	List<PayRefundEntity> findByIds(List<Long> ids);
}

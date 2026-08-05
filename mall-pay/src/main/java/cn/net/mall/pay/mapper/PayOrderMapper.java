package cn.net.mall.pay.mapper;

import cn.net.mall.pay.entity.PayOrderConditionEntity;
import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 支付订单 mapper
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
public interface PayOrderMapper extends BaseMapper<PayOrderEntity, PayOrderConditionEntity> {
	/**
     * 查询支付订单信息
     *
     * @param id 支付订单ID
     * @return 支付订单信息
     */
	PayOrderEntity findById(Long id);

	/**
     * 添加支付订单
     *
     * @param payOrderEntity 支付订单信息
     * @return 结果
     */
	int insert(PayOrderEntity payOrderEntity);

	/**
     * 修改支付订单
     *
     * @param payOrderEntity 支付订单信息
     * @return 结果
     */
	int update(PayOrderEntity payOrderEntity);

	/**
     * 批量删除支付订单
     *
     * @param ids id集合
     * @param entity 支付订单实体
     * @return 结果
     */
	int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") PayOrderEntity entity);

	/**
     * 批量查询支付订单信息
     *
     * @param ids ID集合
     * @return 部门信息
    */
	List<PayOrderEntity> findByIds(List<Long> ids);

	// ============ 对账专用 ============

	/** 按渠道 + 成功时间范围查询支付成功订单 */
	List<PayOrderEntity> selectByChannelAndSuccessTime(@Param("channelCode") String channelCode,
			@Param("startTime") java.util.Date startTime, @Param("endTime") java.util.Date endTime);

	/** 按渠道 + 成功时间统计平台侧总额和笔数 */
	java.util.Map<String, Object> sumByChannelAndSuccessTime(@Param("channelCode") String channelCode,
			@Param("startTime") java.util.Date startTime, @Param("endTime") java.util.Date endTime);

	// ============ 状态机 CAS update ============

	/** 回调更新支付状态（乐观锁：WHERE pay_status = 10，防止并发回调重复处理） */
	int updatePayStatusOnNotify(PayOrderEntity payOrderEntity);

	/** 关闭支付单（乐观锁：WHERE pay_status = 10，防止并发关单覆盖已支付单） */
	int updatePayStatusOnClose(PayOrderEntity payOrderEntity);
}

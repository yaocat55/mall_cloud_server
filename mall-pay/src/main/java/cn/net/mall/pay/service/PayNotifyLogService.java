package cn.net.mall.pay.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.net.mall.pay.mapper.PayNotifyLogMapper;
import cn.net.mall.pay.entity.PayNotifyLogConditionEntity;
import cn.net.mall.pay.entity.PayNotifyLogEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
/**
 * 支付渠道回调日志 服务层
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Service
public class PayNotifyLogService extends BaseService< PayNotifyLogEntity,  PayNotifyLogConditionEntity> {

	@Autowired
	private PayNotifyLogMapper payNotifyLogMapper;

	/**
     * 查询支付渠道回调日志信息
     *
     * @param id 支付渠道回调日志ID
     * @return 支付渠道回调日志信息
     */
	public PayNotifyLogEntity findById(Long id) {
	    PayNotifyLogEntity payNotifyLogEntity = payNotifyLogMapper.findById(id);
		return payNotifyLogEntity;
	}

	/**
     * 根据条件分页查询支付渠道回调日志列表
     *
     * @param payNotifyLogConditionEntity 支付渠道回调日志信息
     * @return 支付渠道回调日志集合
     */
	public ResponsePageEntity<PayNotifyLogEntity> searchByPage(PayNotifyLogConditionEntity payNotifyLogConditionEntity) {
		int count = payNotifyLogMapper.searchCount(payNotifyLogConditionEntity);
		if (count == 0) {
			return ResponsePageEntity.buildEmpty(payNotifyLogConditionEntity);
		}
		List<PayNotifyLogEntity> dataList = payNotifyLogMapper.searchByCondition(payNotifyLogConditionEntity);
		return ResponsePageEntity.build(payNotifyLogConditionEntity, count, dataList);
	}

    /**
     * 新增支付渠道回调日志
     *
     * @param payNotifyLogEntity 支付渠道回调日志信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int insert(PayNotifyLogEntity payNotifyLogEntity) {
	    int rows = payNotifyLogMapper.insert(payNotifyLogEntity);
		return rows;
	}

	/**
     * 修改支付渠道回调日志
     *
     * @param payNotifyLogEntity 支付渠道回调日志信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int update(PayNotifyLogEntity payNotifyLogEntity) {
	    int rows = payNotifyLogMapper.update(payNotifyLogEntity);
		return rows;
	}

	/**
     * 批量删除支付渠道回调日志对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
	public int deleteByIds(List<Long> ids) {
		List<PayNotifyLogEntity> entities = payNotifyLogMapper.findByIds(ids);
		AssertUtil.notEmpty(entities, "支付渠道回调日志已被删除");

		PayNotifyLogEntity entity = new PayNotifyLogEntity();
		FillUserUtil.fillUpdateUserInfo(entity);
		return payNotifyLogMapper.deleteByIds(ids, entity);
	}

	@Override
	protected BaseMapper getBaseMapper() {
		return payNotifyLogMapper;
	}

}

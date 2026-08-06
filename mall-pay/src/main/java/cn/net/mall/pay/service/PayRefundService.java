package cn.net.mall.pay.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.net.mall.pay.mapper.PayRefundMapper;
import cn.net.mall.pay.entity.PayRefundConditionEntity;
import cn.net.mall.pay.entity.PayRefundEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
/**
 * 支付退款单 服务层
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Service
public class PayRefundService extends BaseService< PayRefundEntity,  PayRefundConditionEntity> {

	@Autowired
	private PayRefundMapper payRefundMapper;

	/**
     * 查询支付退款单信息
     *
     * @param id 支付退款单ID
     * @return 支付退款单信息
     */
	public PayRefundEntity findById(Long id) {
	    PayRefundEntity payRefundEntity = payRefundMapper.findById(id);
		return payRefundEntity;
	}

	/**
     * 根据条件分页查询支付退款单列表
     *
     * @param payRefundConditionEntity 支付退款单信息
     * @return 支付退款单集合
     */
	public ResponsePageEntity<PayRefundEntity> searchByPage(PayRefundConditionEntity payRefundConditionEntity) {
		int count = payRefundMapper.searchCount(payRefundConditionEntity);
		if (count == 0) {
			return ResponsePageEntity.buildEmpty(payRefundConditionEntity);
		}
		List<PayRefundEntity> dataList = payRefundMapper.searchByCondition(payRefundConditionEntity);
		return ResponsePageEntity.build(payRefundConditionEntity, count, dataList);
	}

    /**
     * 新增支付退款单
     *
     * @param payRefundEntity 支付退款单信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int insert(PayRefundEntity payRefundEntity) {
	    int rows = payRefundMapper.insert(payRefundEntity);
		return rows;
	}

	/**
     * 修改支付退款单
     *
     * @param payRefundEntity 支付退款单信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int update(PayRefundEntity payRefundEntity) {
	    int rows = payRefundMapper.update(payRefundEntity);
		return rows;
	}

	/**
     * 批量删除支付退款单对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
	public int deleteByIds(List<Long> ids) {
		List<PayRefundEntity> entities = payRefundMapper.findByIds(ids);
		AssertUtil.notEmpty(entities, "支付退款单已被删除");

		PayRefundEntity entity = new PayRefundEntity();
		FillUserUtil.fillUpdateUserInfo(entity);
		return payRefundMapper.deleteByIds(ids, entity);
	}

	@Override
	protected BaseMapper getBaseMapper() {
		return payRefundMapper;
	}

}

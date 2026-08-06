package cn.net.mall.pay.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.net.mall.pay.mapper.PayOrderMapper;
import cn.net.mall.pay.entity.PayOrderConditionEntity;
import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
/**
 * 支付订单 服务层
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Service
public class PayOrderService extends BaseService< PayOrderEntity,  PayOrderConditionEntity> {

	@Autowired
	private PayOrderMapper payOrderMapper;

	/**
     * 查询支付订单信息
     *
     * @param id 支付订单ID
     * @return 支付订单信息
     */
	public PayOrderEntity findById(Long id) {
	    PayOrderEntity payOrderEntity = payOrderMapper.findById(id);
		return payOrderEntity;
	}

	/**
     * 根据条件分页查询支付订单列表
     *
     * @param payOrderConditionEntity 支付订单信息
     * @return 支付订单集合
     */
	public ResponsePageEntity<PayOrderEntity> searchByPage(PayOrderConditionEntity payOrderConditionEntity) {
		int count = payOrderMapper.searchCount(payOrderConditionEntity);
		if (count == 0) {
			return ResponsePageEntity.buildEmpty(payOrderConditionEntity);
		}
		List<PayOrderEntity> dataList = payOrderMapper.searchByCondition(payOrderConditionEntity);
		return ResponsePageEntity.build(payOrderConditionEntity, count, dataList);
	}

    /**
     * 新增支付订单
     *
     * @param payOrderEntity 支付订单信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int insert(PayOrderEntity payOrderEntity) {
	    int rows = payOrderMapper.insert(payOrderEntity);
		return rows;
	}

	/**
     * 修改支付订单
     *
     * @param payOrderEntity 支付订单信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int update(PayOrderEntity payOrderEntity) {
	    int rows = payOrderMapper.update(payOrderEntity);
		return rows;
	}

	/**
     * 批量删除支付订单对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
	public int deleteByIds(List<Long> ids) {
		List<PayOrderEntity> entities = payOrderMapper.findByIds(ids);
		AssertUtil.notEmpty(entities, "支付订单已被删除");

		PayOrderEntity entity = new PayOrderEntity();
		FillUserUtil.fillUpdateUserInfo(entity);
		return payOrderMapper.deleteByIds(ids, entity);
	}

	@Override
	protected BaseMapper getBaseMapper() {
		return payOrderMapper;
	}

}

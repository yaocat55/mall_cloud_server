package cn.net.mall.pay.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.net.mall.pay.mapper.PayBizConfigMapper;
import cn.net.mall.pay.entity.PayBizConfigConditionEntity;
import cn.net.mall.pay.entity.PayBizConfigEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.pay.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
/**
 * 业务渠道接入 服务层
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Service
public class PayBizConfigService extends BaseService< PayBizConfigEntity,  PayBizConfigConditionEntity> {

	@Autowired
	private PayBizConfigMapper payBizConfigMapper;

	/**
     * 查询业务渠道接入信息
     *
     * @param id 业务渠道接入ID
     * @return 业务渠道接入信息
     */
	public PayBizConfigEntity findById(Long id) {
	    PayBizConfigEntity payBizConfigEntity = payBizConfigMapper.findById(id);
		return payBizConfigEntity;
	}

	/**
     * 根据条件分页查询业务渠道接入列表
     *
     * @param payBizConfigConditionEntity 业务渠道接入信息
     * @return 业务渠道接入集合
     */
	public ResponsePageEntity<PayBizConfigEntity> searchByPage(PayBizConfigConditionEntity payBizConfigConditionEntity) {
		int count = payBizConfigMapper.searchCount(payBizConfigConditionEntity);
		if (count == 0) {
			return ResponsePageEntity.buildEmpty(payBizConfigConditionEntity);
		}
		List<PayBizConfigEntity> dataList = payBizConfigMapper.searchByCondition(payBizConfigConditionEntity);
		return ResponsePageEntity.build(payBizConfigConditionEntity, count, dataList);
	}

    /**
     * 新增业务渠道接入
     *
     * @param payBizConfigEntity 业务渠道接入信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int insert(PayBizConfigEntity payBizConfigEntity) {
	    int rows = payBizConfigMapper.insert(payBizConfigEntity);
		return rows;
	}

	/**
     * 修改业务渠道接入
     *
     * @param payBizConfigEntity 业务渠道接入信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int update(PayBizConfigEntity payBizConfigEntity) {
	    int rows = payBizConfigMapper.update(payBizConfigEntity);
		return rows;
	}

	/**
     * 批量删除业务渠道接入对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
	public int deleteByIds(List<Long> ids) {
		List<PayBizConfigEntity> entities = payBizConfigMapper.findByIds(ids);
		AssertUtil.notEmpty(entities, "业务渠道接入已被删除");

		PayBizConfigEntity entity = new PayBizConfigEntity();
		FillUserUtil.fillUpdateUserInfo(entity);
		return payBizConfigMapper.deleteByIds(ids, entity);
	}

	@Override
	protected BaseMapper getBaseMapper() {
		return payBizConfigMapper;
	}

}

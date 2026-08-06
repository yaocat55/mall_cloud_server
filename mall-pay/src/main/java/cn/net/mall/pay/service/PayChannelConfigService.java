package cn.net.mall.pay.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.net.mall.pay.mapper.PayChannelConfigMapper;
import cn.net.mall.pay.entity.PayChannelConfigConditionEntity;
import cn.net.mall.pay.entity.PayChannelConfigEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
/**
 * 支付渠道配置 服务层
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Service
public class PayChannelConfigService extends BaseService< PayChannelConfigEntity,  PayChannelConfigConditionEntity> {

	@Autowired
	private PayChannelConfigMapper payChannelConfigMapper;

	@Autowired
	private PayChannelService payChannelService;

	/**
     * 查询支付渠道配置信息
     *
     * @param id 支付渠道配置ID
     * @return 支付渠道配置信息
     */
	public PayChannelConfigEntity findById(Long id) {
	    PayChannelConfigEntity payChannelConfigEntity = payChannelConfigMapper.findById(id);
		return payChannelConfigEntity;
	}

	/**
     * 根据条件分页查询支付渠道配置列表
     *
     * @param payChannelConfigConditionEntity 支付渠道配置信息
     * @return 支付渠道配置集合
     */
	public ResponsePageEntity<PayChannelConfigEntity> searchByPage(PayChannelConfigConditionEntity payChannelConfigConditionEntity) {
		int count = payChannelConfigMapper.searchCount(payChannelConfigConditionEntity);
		if (count == 0) {
			return ResponsePageEntity.buildEmpty(payChannelConfigConditionEntity);
		}
		List<PayChannelConfigEntity> dataList = payChannelConfigMapper.searchByCondition(payChannelConfigConditionEntity);
		return ResponsePageEntity.build(payChannelConfigConditionEntity, count, dataList);
	}

    /**
     * 新增支付渠道配置
     *
     * @param payChannelConfigEntity 支付渠道配置信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int insert(PayChannelConfigEntity payChannelConfigEntity) {
	    int rows = payChannelConfigMapper.insert(payChannelConfigEntity);
	    payChannelService.evictConfig(payChannelConfigEntity.getChannelCode());
	    return rows;
	}

	/**
     * 修改支付渠道配置
     *
     * @param payChannelConfigEntity 支付渠道配置信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int update(PayChannelConfigEntity payChannelConfigEntity) {
	    int rows = payChannelConfigMapper.update(payChannelConfigEntity);
	    payChannelService.evictConfig(payChannelConfigEntity.getChannelCode());
	    return rows;
	}

	/**
     * 批量删除支付渠道配置对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
	public int deleteByIds(List<Long> ids) {
		List<PayChannelConfigEntity> entities = payChannelConfigMapper.findByIds(ids);
		AssertUtil.notEmpty(entities, "支付渠道配置已被删除");

		PayChannelConfigEntity entity = new PayChannelConfigEntity();
		FillUserUtil.fillUpdateUserInfo(entity);
		return payChannelConfigMapper.deleteByIds(ids, entity);
	}

	@Override
	protected BaseMapper getBaseMapper() {
		return payChannelConfigMapper;
	}

}

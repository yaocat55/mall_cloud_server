package cn.net.mall.pay.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.net.mall.pay.mapper.ReconBatchMapper;
import cn.net.mall.pay.entity.ReconBatchConditionEntity;
import cn.net.mall.pay.entity.ReconBatchEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
/**
 * 对账批次 服务层
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Service
public class ReconBatchService extends BaseService< ReconBatchEntity,  ReconBatchConditionEntity> {

	@Autowired
	private ReconBatchMapper reconBatchMapper;

	/**
     * 查询对账批次信息
     *
     * @param id 对账批次ID
     * @return 对账批次信息
     */
	public ReconBatchEntity findById(Long id) {
	    ReconBatchEntity reconBatchEntity = reconBatchMapper.findById(id);
		return reconBatchEntity;
	}

	/**
     * 根据条件分页查询对账批次列表
     *
     * @param reconBatchConditionEntity 对账批次信息
     * @return 对账批次集合
     */
	public ResponsePageEntity<ReconBatchEntity> searchByPage(ReconBatchConditionEntity reconBatchConditionEntity) {
		int count = reconBatchMapper.searchCount(reconBatchConditionEntity);
		if (count == 0) {
			return ResponsePageEntity.buildEmpty(reconBatchConditionEntity);
		}
		List<ReconBatchEntity> dataList = reconBatchMapper.searchByCondition(reconBatchConditionEntity);
		return ResponsePageEntity.build(reconBatchConditionEntity, count, dataList);
	}

    /**
     * 新增对账批次
     *
     * @param reconBatchEntity 对账批次信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int insert(ReconBatchEntity reconBatchEntity) {
	    int rows = reconBatchMapper.insert(reconBatchEntity);
		return rows;
	}

	/**
     * 修改对账批次
     *
     * @param reconBatchEntity 对账批次信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int update(ReconBatchEntity reconBatchEntity) {
	    int rows = reconBatchMapper.update(reconBatchEntity);
		return rows;
	}

	/**
     * 批量删除对账批次对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
	public int deleteByIds(List<Long> ids) {
		List<ReconBatchEntity> entities = reconBatchMapper.findByIds(ids);
		AssertUtil.notEmpty(entities, "对账批次已被删除");

		ReconBatchEntity entity = new ReconBatchEntity();
		FillUserUtil.fillUpdateUserInfo(entity);
		return reconBatchMapper.deleteByIds(ids, entity);
	}

	@Override
	protected BaseMapper getBaseMapper() {
		return reconBatchMapper;
	}

}

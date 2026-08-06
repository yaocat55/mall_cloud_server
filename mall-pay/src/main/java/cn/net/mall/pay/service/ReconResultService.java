package cn.net.mall.pay.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.net.mall.pay.mapper.ReconResultMapper;
import cn.net.mall.pay.entity.ReconResultConditionEntity;
import cn.net.mall.pay.entity.ReconResultEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
/**
 * 对账逐笔结果 服务层
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Service
public class ReconResultService extends BaseService< ReconResultEntity,  ReconResultConditionEntity> {

	@Autowired
	private ReconResultMapper reconResultMapper;

	/**
     * 查询对账逐笔结果信息
     *
     * @param id 对账逐笔结果ID
     * @return 对账逐笔结果信息
     */
	public ReconResultEntity findById(Long id) {
	    ReconResultEntity reconResultEntity = reconResultMapper.findById(id);
		return reconResultEntity;
	}

	/**
     * 根据条件分页查询对账逐笔结果列表
     *
     * @param reconResultConditionEntity 对账逐笔结果信息
     * @return 对账逐笔结果集合
     */
	public ResponsePageEntity<ReconResultEntity> searchByPage(ReconResultConditionEntity reconResultConditionEntity) {
		int count = reconResultMapper.searchCount(reconResultConditionEntity);
		if (count == 0) {
			return ResponsePageEntity.buildEmpty(reconResultConditionEntity);
		}
		List<ReconResultEntity> dataList = reconResultMapper.searchByCondition(reconResultConditionEntity);
		return ResponsePageEntity.build(reconResultConditionEntity, count, dataList);
	}

    /**
     * 新增对账逐笔结果
     *
     * @param reconResultEntity 对账逐笔结果信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int insert(ReconResultEntity reconResultEntity) {
	    int rows = reconResultMapper.insert(reconResultEntity);
		return rows;
	}

	/**
     * 修改对账逐笔结果
     *
     * @param reconResultEntity 对账逐笔结果信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int update(ReconResultEntity reconResultEntity) {
	    int rows = reconResultMapper.update(reconResultEntity);
		return rows;
	}

	/**
     * 批量删除对账逐笔结果对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
	public int deleteByIds(List<Long> ids) {
		List<ReconResultEntity> entities = reconResultMapper.findByIds(ids);
		AssertUtil.notEmpty(entities, "对账逐笔结果已被删除");

		ReconResultEntity entity = new ReconResultEntity();
		FillUserUtil.fillUpdateUserInfo(entity);
		return reconResultMapper.deleteByIds(ids, entity);
	}

	@Override
	protected BaseMapper getBaseMapper() {
		return reconResultMapper;
	}

}

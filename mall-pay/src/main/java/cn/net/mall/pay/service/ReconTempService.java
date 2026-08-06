package cn.net.mall.pay.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.net.mall.pay.mapper.ReconTempMapper;
import cn.net.mall.pay.entity.ReconTempConditionEntity;
import cn.net.mall.pay.entity.ReconTempEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
/**
 * 对账临时（按批次批量插入，对账完成保留N天后清理） 服务层
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Service
public class ReconTempService extends BaseService< ReconTempEntity,  ReconTempConditionEntity> {

	@Autowired
	private ReconTempMapper reconTempMapper;

	/**
     * 查询对账临时（按批次批量插入，对账完成保留N天后清理）信息
     *
     * @param id 对账临时（按批次批量插入，对账完成保留N天后清理）ID
     * @return 对账临时（按批次批量插入，对账完成保留N天后清理）信息
     */
	public ReconTempEntity findById(Long id) {
	    ReconTempEntity reconTempEntity = reconTempMapper.findById(id);
		return reconTempEntity;
	}

	/**
     * 根据条件分页查询对账临时（按批次批量插入，对账完成保留N天后清理）列表
     *
     * @param reconTempConditionEntity 对账临时（按批次批量插入，对账完成保留N天后清理）信息
     * @return 对账临时（按批次批量插入，对账完成保留N天后清理）集合
     */
	public ResponsePageEntity<ReconTempEntity> searchByPage(ReconTempConditionEntity reconTempConditionEntity) {
		int count = reconTempMapper.searchCount(reconTempConditionEntity);
		if (count == 0) {
			return ResponsePageEntity.buildEmpty(reconTempConditionEntity);
		}
		List<ReconTempEntity> dataList = reconTempMapper.searchByCondition(reconTempConditionEntity);
		return ResponsePageEntity.build(reconTempConditionEntity, count, dataList);
	}

    /**
     * 新增对账临时（按批次批量插入，对账完成保留N天后清理）
     *
     * @param reconTempEntity 对账临时（按批次批量插入，对账完成保留N天后清理）信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int insert(ReconTempEntity reconTempEntity) {
	    int rows = reconTempMapper.insert(reconTempEntity);
		return rows;
	}

	/**
     * 修改对账临时（按批次批量插入，对账完成保留N天后清理）
     *
     * @param reconTempEntity 对账临时（按批次批量插入，对账完成保留N天后清理）信息
     * @return 结果
     */
	@Transactional(rollbackFor = Exception.class)
	public int update(ReconTempEntity reconTempEntity) {
	    int rows = reconTempMapper.update(reconTempEntity);
		return rows;
	}

	/**
     * 批量删除对账临时（按批次批量插入，对账完成保留N天后清理）对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
	public int deleteByIds(List<Long> ids) {
		List<ReconTempEntity> entities = reconTempMapper.findByIds(ids);
		AssertUtil.notEmpty(entities, "对账临时（按批次批量插入，对账完成保留N天后清理）已被删除");
		return reconTempMapper.deleteByIds(ids);
	}

	@Override
	protected BaseMapper getBaseMapper() {
		return reconTempMapper;
	}

}

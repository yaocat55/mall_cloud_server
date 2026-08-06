package cn.net.mall.pay.mapper;

import cn.net.mall.pay.entity.ReconTempConditionEntity;
import cn.net.mall.pay.entity.ReconTempEntity;
import cn.net.mall.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 对账临时（按批次批量插入，对账完成保留N天后清理） mapper
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
public interface ReconTempMapper extends BaseMapper<ReconTempEntity, ReconTempConditionEntity> {
	/**
     * 查询对账临时（按批次批量插入，对账完成保留N天后清理）信息
     *
     * @param id 对账临时（按批次批量插入，对账完成保留N天后清理）ID
     * @return 对账临时（按批次批量插入，对账完成保留N天后清理）信息
     */
	ReconTempEntity findById(Long id);

	/**
     * 添加对账临时（按批次批量插入，对账完成保留N天后清理）
     *
     * @param reconTempEntity 对账临时（按批次批量插入，对账完成保留N天后清理）信息
     * @return 结果
     */
	int insert(ReconTempEntity reconTempEntity);

	/**
     * 修改对账临时（按批次批量插入，对账完成保留N天后清理）
     *
     * @param reconTempEntity 对账临时（按批次批量插入，对账完成保留N天后清理）信息
     * @return 结果
     */
	int update(ReconTempEntity reconTempEntity);

	/**
     * 批量删除对账临时（按批次批量插入，对账完成保留N天后清理）
     *
     * @param ids id集合
     * @return 结果
     */
	int deleteByIds(@Param("ids") List<Long> ids);

	/**
     * 批量查询对账临时（按批次批量插入，对账完成保留N天后清理）信息
     *
     * @param ids ID集合
     * @return 部门信息
    */
	List<ReconTempEntity> findByIds(List<Long> ids);

	// ============ 对账专用 ============

	/** 批量插入临时表（一次 INSERT 多行） */
	int batchInsert(List<ReconTempEntity> list);

	/** 按批次号查询全量临时数据 */
	List<ReconTempEntity> selectByBatchNo(@Param("batchNo") String batchNo);

	/** 按批次号删除 */
	int deleteByBatchNo(@Param("batchNo") String batchNo);

	/** 按批次号统计渠道侧总额和笔数 */
	Map<String, Object> sumByBatchNo(@Param("batchNo") String batchNo);

	/** 按批次号统计退款项总额 */
	Map<String, Object> sumRefundByBatchNo(@Param("batchNo") String batchNo);
}

package cn.net.mall.pay.mapper;

import cn.net.mall.pay.entity.ReconBatchConditionEntity;
import cn.net.mall.pay.entity.ReconBatchEntity;
import cn.net.mall.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 对账批次 mapper
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
public interface ReconBatchMapper extends BaseMapper<ReconBatchEntity, ReconBatchConditionEntity> {
	/**
     * 查询对账批次信息
     *
     * @param id 对账批次ID
     * @return 对账批次信息
     */
	ReconBatchEntity findById(Long id);

	/**
     * 添加对账批次
     *
     * @param reconBatchEntity 对账批次信息
     * @return 结果
     */
	int insert(ReconBatchEntity reconBatchEntity);

	/**
     * 修改对账批次
     *
     * @param reconBatchEntity 对账批次信息
     * @return 结果
     */
	int update(ReconBatchEntity reconBatchEntity);

	/**
     * 批量删除对账批次
     *
     * @param ids id集合
     * @param entity 对账批次实体
     * @return 结果
     */
	int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") ReconBatchEntity entity);

	/**
     * 批量查询对账批次信息
     *
     * @param ids ID集合
     * @return 部门信息
    */
	List<ReconBatchEntity> findByIds(List<Long> ids);
}

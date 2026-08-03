package cn.net.mall.pay.mapper;

import cn.net.mall.pay.entity.ReconResultConditionEntity;
import cn.net.mall.pay.entity.ReconResultEntity;
import cn.net.mall.pay.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 对账逐笔结果 mapper
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
public interface ReconResultMapper extends BaseMapper<ReconResultEntity, ReconResultConditionEntity> {
	/**
     * 查询对账逐笔结果信息
     *
     * @param id 对账逐笔结果ID
     * @return 对账逐笔结果信息
     */
	ReconResultEntity findById(Long id);

	/**
     * 添加对账逐笔结果
     *
     * @param reconResultEntity 对账逐笔结果信息
     * @return 结果
     */
	int insert(ReconResultEntity reconResultEntity);

	/**
     * 修改对账逐笔结果
     *
     * @param reconResultEntity 对账逐笔结果信息
     * @return 结果
     */
	int update(ReconResultEntity reconResultEntity);

	/**
     * 批量删除对账逐笔结果
     *
     * @param ids id集合
     * @param entity 对账逐笔结果实体
     * @return 结果
     */
	int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") ReconResultEntity entity);

	/**
     * 批量查询对账逐笔结果信息
     *
     * @param ids ID集合
     * @return 部门信息
    */
	List<ReconResultEntity> findByIds(List<Long> ids);
}

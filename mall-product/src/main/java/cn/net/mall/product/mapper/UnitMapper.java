package cn.net.mall.product.mapper;

import cn.net.mall.product.entity.UnitConditionEntity;
import cn.net.mall.product.entity.UnitEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单位 mapper
 *
 * @date 2024-05-09 14:43:55
 */
public interface UnitMapper extends BaseMapper<UnitEntity, UnitConditionEntity> {
	/**
     * 查询单位信息
     *
     * @param id 单位ID
     * @return 单位信息
     */
	UnitEntity findById(Long id);

	/**
     * 添加单位
     *
     * @param unitEntity 单位信息
     * @return 结果
     */
	int insert(UnitEntity unitEntity);

	/**
     * 修改单位
     *
     * @param unitEntity 单位信息
     * @return 结果
     */
	int update(UnitEntity unitEntity);

	/**
     * 批量删除单位
     *
     * @param ids id集合
     * @param entity 单位实体
     * @return 结果
     */
	int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") UnitEntity entity);

	/**
     * 批量查询单位信息
     *
     * @param ids ID集合
     * @return 部门信息
    */
	List<UnitEntity> findByIds(List<Long> ids);
}

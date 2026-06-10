package cn.net.mall.product.mapper;

import cn.net.mall.product.entity.AttributeValueConditionEntity;
import cn.net.mall.product.entity.AttributeValueEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性值 mapper
 *
 * @date 2024-05-09 14:43:55
 */
public interface AttributeValueMapper extends BaseMapper<AttributeValueEntity, AttributeValueConditionEntity> {
	/**
     * 查询属性值信息
     *
     * @param id 属性值ID
     * @return 属性值信息
     */
	AttributeValueEntity findById(Long id);

	/**
     * 添加属性值
     *
     * @param attributeValueEntity 属性值信息
     * @return 结果
     */
	int insert(AttributeValueEntity attributeValueEntity);

	/**
     * 修改属性值
     *
     * @param attributeValueEntity 属性值信息
     * @return 结果
     */
	int update(AttributeValueEntity attributeValueEntity);

	/**
     * 批量删除属性值
     *
     * @param ids id集合
     * @param entity 属性值实体
     * @return 结果
     */
	int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") AttributeValueEntity entity);

	/**
     * 批量查询属性值信息
     *
     * @param ids ID集合
     * @return 部门信息
    */
	List<AttributeValueEntity> findByIds(List<Long> ids);
}

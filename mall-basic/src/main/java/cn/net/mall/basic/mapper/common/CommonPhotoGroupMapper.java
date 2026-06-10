package cn.net.mall.basic.mapper.common;

import cn.net.mall.basic.entity.common.CommonPhotoGroupConditionEntity;
import cn.net.mall.basic.entity.common.CommonPhotoGroupEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图片分组 mapper
 *
 * @date 2024-07-03 16:43:09
 */
public interface CommonPhotoGroupMapper extends BaseMapper<CommonPhotoGroupEntity, CommonPhotoGroupConditionEntity> {
	/**
     * 查询图片分组信息
     *
     * @param id 图片分组ID
     * @return 图片分组信息
     */
	CommonPhotoGroupEntity findById(Long id);

	/**
     * 添加图片分组
     *
     * @param commonPhotoGroupEntity 图片分组信息
     * @return 结果
     */
	int insert(CommonPhotoGroupEntity commonPhotoGroupEntity);

	/**
     * 修改图片分组
     *
     * @param commonPhotoGroupEntity 图片分组信息
     * @return 结果
     */
	int update(CommonPhotoGroupEntity commonPhotoGroupEntity);

	/**
     * 批量删除图片分组
     *
     * @param ids id集合
     * @param entity 图片分组实体
     * @return 结果
     */
	int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") CommonPhotoGroupEntity entity);

	/**
     * 批量查询图片分组信息
     *
     * @param ids ID集合
     * @return 部门信息
    */
	List<CommonPhotoGroupEntity> findByIds(List<Long> ids);
}

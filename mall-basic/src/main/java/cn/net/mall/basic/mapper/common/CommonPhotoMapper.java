package cn.net.mall.basic.mapper.common;

import cn.net.mall.basic.entity.common.CommonPhotoConditionEntity;
import cn.net.mall.basic.entity.common.CommonPhotoEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图片 mapper
 *
 * @date 2024-07-03 16:43:09
 */
public interface CommonPhotoMapper extends BaseMapper<CommonPhotoEntity, CommonPhotoConditionEntity> {
	/**
     * 查询图片信息
     *
     * @param id 图片ID
     * @return 图片信息
     */
	CommonPhotoEntity findById(Long id);

	/**
     * 添加图片
     *
     * @param commonPhotoEntity 图片信息
     * @return 结果
     */
	int insert(CommonPhotoEntity commonPhotoEntity);

	/**
     * 修改图片
     *
     * @param commonPhotoEntity 图片信息
     * @return 结果
     */
	int update(CommonPhotoEntity commonPhotoEntity);

	/**
     * 批量删除图片
     *
     * @param ids id集合
     * @param entity 图片实体
     * @return 结果
     */
	int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") CommonPhotoEntity entity);

	/**
     * 批量查询图片信息
     *
     * @param ids ID集合
     * @return 部门信息
    */
	List<CommonPhotoEntity> findByIds(List<Long> ids);
}

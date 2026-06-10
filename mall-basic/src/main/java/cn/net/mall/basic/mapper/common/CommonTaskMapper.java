package cn.net.mall.basic.mapper.common;

import cn.net.mall.basic.entity.common.CommonTaskConditionEntity;
import cn.net.mall.basic.entity.common.CommonTaskEntity;
import cn.net.mall.mapper.BaseMapper;

/**
 * 任务 mapper
 *
 * @date 2024-01-29 17:31:17
 */
public interface CommonTaskMapper  extends BaseMapper<CommonTaskEntity, CommonTaskConditionEntity> {
	/**
     * 查询任务信息
     *
     * @param id 任务ID
     * @return 任务信息
     */
	CommonTaskEntity findById(Long id);

	/**
     * 添加任务
     *
     * @param commonTaskEntity 任务信息
     * @return 结果
     */
	int insert(CommonTaskEntity commonTaskEntity);

	/**
     * 修改任务
     *
     * @param commonTaskEntity 任务信息
     * @return 结果
     */
	int update(CommonTaskEntity commonTaskEntity);

	/**
     * 删除任务
     *
     * @param id 任务ID
     * @return 结果
     */
	int deleteById(Long id);

}

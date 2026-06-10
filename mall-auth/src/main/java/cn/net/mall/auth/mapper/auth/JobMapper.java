package cn.net.mall.auth.mapper.auth;

import cn.net.mall.auth.entity.auth.JobConditionEntity;
import cn.net.mall.auth.entity.auth.JobEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岗位 mapper
 *
 * @date 2024-01-08 14:03:43
 */
public interface JobMapper  extends BaseMapper<JobEntity, JobConditionEntity> {
    /**
     * 查询岗位信息
     *
     * @param id 岗位ID
     * @return 岗位信息
     */
    JobEntity findById(Long id);

    /**
     * 批量查询岗位信息
     *
     * @param ids 岗位ID
     * @return 岗位信息
     */
    List<JobEntity> findByIds(List<Long> ids);

    /**
     * 添加岗位
     *
     * @param jobEntity 岗位信息
     * @return 结果
     */
    int insert(JobEntity jobEntity);

    /**
     * 修改岗位
     *
     * @param jobEntity 岗位信息
     * @return 结果
     */
    int update(JobEntity jobEntity);

    /**
     * 删除岗位
     *
     * @param ids       岗位ID
     * @param jobEntity 岗位实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("jobEntity") JobEntity jobEntity);

}

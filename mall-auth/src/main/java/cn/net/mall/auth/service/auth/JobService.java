package cn.net.mall.auth.service.auth;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.auth.entity.auth.JobConditionEntity;
import cn.net.mall.auth.entity.auth.JobEntity;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.auth.mapper.auth.JobMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位 服务层
 *
 * @date 2024-01-08 14:03:43
 */
@Service
public class JobService extends BaseService<JobEntity, JobConditionEntity> {

    private final JobMapper jobMapper;

    public JobService(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    /**
     * 查询岗位信息
     *
     * @param id 岗位ID
     * @return 岗位信息
     */
    public JobEntity findById(Long id) {
        return jobMapper.findById(id);
    }

    /**
     * 根据条件分页查询岗位列表
     *
     * @param jobConditionEntity 岗位信息
     * @return 岗位集合
     */
    public ResponsePageEntity<JobEntity> searchByPage(JobConditionEntity jobConditionEntity) {
        return super.searchByPage(jobConditionEntity);
    }

    /**
     * 新增岗位
     *
     * @param jobEntity 岗位信息
     * @return 结果
     */
    public int insert(JobEntity jobEntity) {
        return jobMapper.insert(jobEntity);
    }

    /**
     * 修改岗位
     *
     * @param jobEntity 岗位信息
     * @return 结果
     */
    public int update(JobEntity jobEntity) {
        return jobMapper.update(jobEntity);
    }

    /**
     * 删除岗位对象
     *
     * @param ids 系统ID
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<JobEntity> jobEntities = jobMapper.findByIds(ids);
        AssertUtil.notEmpty(jobEntities, "岗位已被删除");

        JobEntity jobEntity = new JobEntity();
        FillUserUtil.fillUpdateUserInfo(jobEntity);
        return jobMapper.deleteByIds(ids, jobEntity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return jobMapper;
    }
}

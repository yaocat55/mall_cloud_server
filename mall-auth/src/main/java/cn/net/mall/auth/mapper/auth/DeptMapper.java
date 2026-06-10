package cn.net.mall.auth.mapper.auth;

import cn.net.mall.auth.entity.auth.DeptConditionEntity;
import cn.net.mall.auth.entity.auth.DeptEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 部门 mapper
 *
 * @date 2024-01-08 14:03:43
 */
@Mapper
public interface DeptMapper extends BaseMapper<DeptEntity, DeptConditionEntity> {
    /**
     * 查询部门信息
     *
     * @param id 部门ID
     * @return 部门信息
     */
    DeptEntity findById(Long id);

    /**
     * 批量查询部门信息
     *
     * @param ids ID集合
     * @return 部门信息
     */
    List<DeptEntity> findByIds(List<Long> ids);

    /**
     * 根据角色ID集合批量查询菜单
     *
     * @return 菜单集合
     */
    List<DeptEntity> findDeptByRoleIdList(@Param("roleIdList") Collection<Long> roleIdList);

    /**
     * 添加部门
     *
     * @param deptEntity 部门信息
     * @return 结果
     */
    int insert(DeptEntity deptEntity);

    /**
     * 修改部门
     *
     * @param deptEntity 部门信息
     * @return 结果
     */
    int update(DeptEntity deptEntity);

    /**
     * 删除部门
     *
     * @param ids        id集合
     * @param deptEntity 部门实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("deptEntity") DeptEntity deptEntity);

}

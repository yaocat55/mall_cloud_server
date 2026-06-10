package cn.net.mall.auth.mapper.auth;

import cn.net.mall.auth.entity.auth.RoleConditionEntity;
import cn.net.mall.auth.entity.auth.RoleEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色 mapper
 *
 * @date 2024-01-08 14:03:44
 */
public interface RoleMapper extends BaseMapper<RoleEntity, RoleConditionEntity> {
    /**
     * 查询角色信息
     *
     * @param id 角色ID
     * @return 角色信息
     */
    RoleEntity findById(Long id);

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色
     */
    List<RoleEntity> findRoleByUserId(Long userId);

    /**
     * 添加角色
     *
     * @param roleEntity 角色信息
     * @return 结果
     */
    int insert(RoleEntity roleEntity);

    /**
     * 修改角色
     *
     * @param roleEntity 角色信息
     * @return 结果
     */
    int update(RoleEntity roleEntity);


    /**
     * 批量删除角色
     *
     * @param ids    角色ID
     * @param entity 角色实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") RoleEntity entity);


    /**
     * 批量查询角色信息
     *
     * @param ids 角色ID
     * @return 角色信息
     */
    List<RoleEntity> findByIds(List<Long> ids);

}

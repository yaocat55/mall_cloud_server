package cn.net.mall.admin.mapper.auth;

import cn.net.mall.admin.entity.auth.UserRoleConditionEntity;
import cn.net.mall.admin.entity.auth.UserRoleEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联 mapper
 *
 * @date 2024-01-08 14:03:45
 */
public interface UserRoleMapper {
    /**
     * 查询用户角色关联信息
     *
     * @param id 用户角色关联ID
     * @return 用户角色关联信息
     */
    UserRoleEntity findById(Long id);

    /**
     * 根据条件查询用户角色关联列表
     *
     * @param userRoleConditionEntity 用户角色关联信息
     * @return 用户角色关联集合
     */
    List<UserRoleEntity> searchByCondition(UserRoleConditionEntity userRoleConditionEntity);

    /**
     * 根据条件查询用户角色关联数量
     *
     * @param userRoleConditionEntity 用户角色关联信息
     * @return 用户角色关联集合
     */
    int searchCount(UserRoleConditionEntity userRoleConditionEntity);

    /**
     * 添加用户角色关联
     *
     * @param userRoleEntity 用户角色关联信息
     * @return 结果
     */
    int insert(UserRoleEntity userRoleEntity);

    /**
     * 批量添加用户角色关联
     *
     * @param list 用户角色关联信息集合
     * @return 结果
     */
    int batchInsert(List<UserRoleEntity> list);

    /**
     * 修改用户角色关联
     *
     * @param userRoleEntity 用户角色关联信息
     * @return 结果
     */
    int update(UserRoleEntity userRoleEntity);

    /**
     * 删除用户角色关联
     *
     * @param id 用户角色关联ID
     * @return 结果
     */
    int deleteById(Long id);

    /**
     * 根据用户ID删除用户角色关联
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);

}

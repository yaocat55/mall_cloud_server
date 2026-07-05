package cn.net.mall.admin.mapper.auth;

import cn.net.mall.admin.entity.auth.RoleMenuConditionEntity;
import cn.net.mall.admin.entity.auth.RoleMenuEntity;

import java.util.List;

/**
 * 角色菜单关联 mapper
 *
 * @date 2024-01-08 14:03:45
 */
public interface RoleMenuMapper {
    /**
     * 查询角色菜单关联信息
     *
     * @param id 角色菜单关联ID
     * @return 角色菜单关联信息
     */
    RoleMenuEntity findById(Long id);

    /**
     * 根据条件查询角色菜单关联列表
     *
     * @param roleMenuConditionEntity 角色菜单关联信息
     * @return 角色菜单关联集合
     */
    List<RoleMenuEntity> searchByCondition(RoleMenuConditionEntity roleMenuConditionEntity);

    /**
     * 根据条件查询角色菜单关联数量
     *
     * @param roleMenuConditionEntity 角色菜单关联信息
     * @return 角色菜单关联集合
     */
    int searchCount(RoleMenuConditionEntity roleMenuConditionEntity);

    /**
     * 添加角色菜单关联
     *
     * @param roleMenuEntity 角色菜单关联信息
     * @return 结果
     */
    int insert(RoleMenuEntity roleMenuEntity);

    /**
     * 批量添加角色菜单关联
     *
     * @param list 角色菜单关联
     * @return 结果
     */
    int batchInsert(List<RoleMenuEntity> list);

    /**
     * 修改角色菜单关联
     *
     * @param roleMenuEntity 角色菜单关联信息
     * @return 结果
     */
    int update(RoleMenuEntity roleMenuEntity);

    /**
     * 删除角色菜单关联
     *
     * @param menuId 角色菜单关联ID
     * @return 结果
     */
    int deleteByMenuId(Long menuId);

    /**
     * 删除角色菜单关联
     *
     * @param roleIds 角色ID
     * @return 结果
     */
    int deleteByRoleIds(List<Long> roleIds);
}

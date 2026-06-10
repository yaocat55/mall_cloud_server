package cn.net.mall.auth.service.auth;

import cn.net.mall.auth.entity.auth.RoleMenuConditionEntity;
import cn.net.mall.auth.entity.auth.RoleMenuEntity;
import cn.net.mall.auth.mapper.auth.RoleMenuMapper;
import cn.net.mall.entity.ResponsePageEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色菜单关联 服务层
 *
 * @date 2024-01-08 14:03:45
 */
@Service
public class RoleMenuService {

	private final RoleMenuMapper roleMenuMapper;

	public RoleMenuService(RoleMenuMapper roleMenuMapper) {
		this.roleMenuMapper = roleMenuMapper;
	}

	/**
     * 查询角色菜单关联信息
     *
     * @param id 角色菜单关联ID
     * @return 角色菜单关联信息
     */
	public RoleMenuEntity findById(Long id) {
	    return roleMenuMapper.findById(id);
	}

	/**
     * 根据条件分页查询角色菜单关联列表
     *
     * @param roleMenuConditionEntity 角色菜单关联信息
     * @return 角色菜单关联集合
     */
	public ResponsePageEntity<RoleMenuEntity> searchByPage(RoleMenuConditionEntity roleMenuConditionEntity) {
		int count = roleMenuMapper.searchCount(roleMenuConditionEntity);
		if (count == 0) {
			return ResponsePageEntity.buildEmpty(roleMenuConditionEntity);
		}
		List<RoleMenuEntity> dataList = roleMenuMapper.searchByCondition(roleMenuConditionEntity);
		return ResponsePageEntity.build(roleMenuConditionEntity, count, dataList);
	}

    /**
     * 新增角色菜单关联
     *
     * @param roleMenuEntity 角色菜单关联信息
     * @return 结果
     */
	public int insert(RoleMenuEntity roleMenuEntity) {
	    return roleMenuMapper.insert(roleMenuEntity);
	}

	/**
     * 修改角色菜单关联
     *
     * @param roleMenuEntity 角色菜单关联信息
     * @return 结果
     */
	public int update(RoleMenuEntity roleMenuEntity) {
	    return roleMenuMapper.update(roleMenuEntity);
	}

	/**
     * 删除角色菜单关联对象
     *
     * @param id 系统ID
     * @return 结果
     */
	public int deleteById(Long id) {
		return roleMenuMapper.deleteByMenuId(id);
	}

}

package cn.net.mall.admin.service.auth;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.admin.entity.auth.RoleConditionEntity;
import cn.net.mall.admin.entity.auth.RoleEntity;
import cn.net.mall.admin.entity.auth.RoleMenuEntity;
import cn.net.mall.workid.IdGenerateHelper;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.admin.mapper.auth.RoleMapper;
import cn.net.mall.admin.mapper.auth.RoleMenuMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色 服务层
 *
 * @date 2024-01-08 14:03:44
 */
@Service
public class RoleService extends BaseService<RoleEntity, RoleConditionEntity> {

    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final IdGenerateHelper idGenerateHelper;

    public RoleService(RoleMapper roleMapper, RoleMenuMapper roleMenuMapper, IdGenerateHelper idGenerateHelper) {
        this.roleMapper = roleMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.idGenerateHelper = idGenerateHelper;
    }

    /**
     * 查询角色信息
     *
     * @param id 角色ID
     * @return 角色信息
     */
    public RoleEntity findById(Long id) {
        return roleMapper.findById(id);
    }

    /**
     * 根据条件分页查询角色列表
     *
     * @param roleConditionEntity 角色信息
     * @return 角色集合
     */
    public ResponsePageEntity<RoleEntity> searchByPage(RoleConditionEntity roleConditionEntity) {
        return super.searchByPage(roleConditionEntity);
    }

    /**
     * 根据查询所有角色
     *
     * @return 所有角色
     */
    public List<RoleEntity> all() {
        RoleConditionEntity roleConditionEntity = new RoleConditionEntity();
        roleConditionEntity.setPageNo(0);
        return roleMapper.searchByCondition(roleConditionEntity);
    }

    /**
     * 新增角色
     *
     * @param roleEntity 角色信息
     * @return 结果
     */
    public int insert(RoleEntity roleEntity) {
        return roleMapper.insert(roleEntity);
    }

    /**
     * 修改角色
     *
     * @param roleEntity 角色信息
     * @return 结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public int update(RoleEntity roleEntity) {
        roleMenuMapper.deleteByRoleIds(Lists.newArrayList(roleEntity.getId()));
        saveRoleMenu(roleEntity);
        return roleMapper.update(roleEntity);
    }

    private void saveRoleMenu(RoleEntity roleEntity) {
        if (CollectionUtils.isEmpty(roleEntity.getMenus())) {
            return;
        }

        List<RoleMenuEntity> roleMenuEntities = roleEntity.getMenus().stream().map(x -> {
            RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
            roleMenuEntity.setId(idGenerateHelper.nextId());
            roleMenuEntity.setRoleId(roleEntity.getId());
            roleMenuEntity.setMenuId(x.getId());
            return roleMenuEntity;
        }).collect(Collectors.toList());

        roleMenuMapper.batchInsert(roleMenuEntities);
    }

    /**
     * 批量删除角色对象
     *
     * @param ids 系统ID
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<RoleEntity> roleEntities = roleMapper.findByIds(ids);
        AssertUtil.notEmpty(roleEntities, "角色已被删除");

        RoleEntity roleEntity = new RoleEntity();
        FillUserUtil.fillUpdateUserInfo(roleEntity);
        return roleMapper.deleteByIds(ids, roleEntity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return roleMapper;
    }
}
